package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.resources.DbClass;
import com.mycompany.techstore.services.OrderStatusPolicy;
import com.mycompany.techstore.services.VietnamTime;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AdminOrderRepository {
  private final AdminAuditRepository audit = new AdminAuditRepository();

  public PageResult<AdminOrder> findAll(
      String q, String status, String payment, LocalDate from, LocalDate to, int page, int size)
      throws SQLException {
    String sql =
        """
        SELECT o.*,u.full_name,u.email,py.payment_status,v.code AS voucher_code,COUNT(*) OVER() total_rows
        FROM dbo.bs_Orders o JOIN dbo.bs_user u ON u.user_id=o.user_id
        LEFT JOIN dbo.bs_Payments py ON py.order_id=o.order_id
        LEFT JOIN dbo.bs_Vouchers v ON v.voucher_id=o.voucher_id
        WHERE (?='' OR CONVERT(varchar(20),o.order_id)=? OR u.full_name LIKE ? OR u.email LIKE ?)
          AND (?='' OR o.order_status=?) AND (?='' OR o.payment_method=?)
          AND (? IS NULL OR o.created_at>=?) AND (? IS NULL OR o.created_at<DATEADD(day,1,?))
        ORDER BY o.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;
    List<AdminOrder> items = new ArrayList<>();
    int total = 0;
    String search = clean(q), like = "%" + clean(q) + "%";
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, search);
      ps.setString(2, search);
      ps.setString(3, like);
      ps.setString(4, like);
      ps.setString(5, clean(status));
      ps.setString(6, clean(status));
      ps.setString(7, clean(payment));
      ps.setString(8, clean(payment));
      setDate(ps, 9, from);
      setDate(ps, 10, from);
      setDate(ps, 11, to);
      setDate(ps, 12, to);
      ps.setInt(13, (page - 1) * size);
      ps.setInt(14, size);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          items.add(map(rs));
          total = rs.getInt("total_rows");
        }
      }
    }
    return new PageResult<>(items, page, size, total);
  }

  public AdminOrder findById(int id) throws SQLException {
    String sql =
        "SELECT o.*,u.full_name,u.email,py.payment_status,v.code AS voucher_code FROM dbo.bs_Orders o JOIN dbo.bs_user u"
            + " ON u.user_id=o.user_id LEFT JOIN dbo.bs_Payments py ON py.order_id=o.order_id"
            + " LEFT JOIN dbo.bs_Vouchers v ON v.voucher_id=o.voucher_id WHERE"
            + " o.order_id=?";
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      AdminOrder order = null;
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) order = map(rs);
      }
      if (order != null) order.setDetails(details(con, id));
      return order;
    }
  }

  public void changeStatus(int orderId, String target, String note, int adminId)
      throws SQLException {
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try {
        String current = lockStatus(con, orderId);
        validateTransition(current, target);
        if ("Confirmed".equals(target)) deductStock(con, orderId);
        if ("Cancelled".equals(target) && "Confirmed".equals(current)) restoreStock(con, orderId);
        try (PreparedStatement ps =
            con.prepareStatement(
                "UPDATE dbo.bs_Orders SET"
                    + " order_status=?,note=COALESCE(NULLIF(?,''),note),updated_at=SYSUTCDATETIME()"
                    + " WHERE order_id=?")) {
          ps.setString(1, target);
          ps.setString(2, clean(note));
          ps.setInt(3, orderId);
          ps.executeUpdate();
        }
        audit.log(con, adminId, "STATUS_CHANGE", "ORDER", orderId, current + " -> " + target);
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  private String lockStatus(Connection con, int id) throws SQLException {
    try (PreparedStatement ps =
        con.prepareStatement(
            "SELECT order_status FROM dbo.bs_Orders WITH (UPDLOCK,ROWLOCK) WHERE order_id=?")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) throw new SQLException("Order not found");
        return rs.getString(1);
      }
    }
  }

  private void validateTransition(String current, String target) {
    OrderStatusPolicy.requireValid(current, target);
    boolean valid =
        ("Pending".equals(current) && Set.of("Confirmed", "Cancelled").contains(target))
            || ("Confirmed".equals(current) && Set.of("Shipping", "Cancelled").contains(target))
            || ("Shipping".equals(current) && "Delivered".equals(target));
    if (!valid)
      throw new IllegalArgumentException("Invalid order transition: " + current + " -> " + target);
  }

  private void deductStock(Connection con, int orderId) throws SQLException {
    for (OrderDetail item : details(con, orderId))
      try (PreparedStatement ps =
          con.prepareStatement(
              "UPDATE dbo.bs_Products SET stock=stock-?,status=CASE WHEN stock-?=0 THEN 'Out of"
                  + " Stock' ELSE status END,updated_at=SYSUTCDATETIME() WHERE product_id=? AND"
                  + " stock>=?")) {
        ps.setInt(1, item.getQuantity());
        ps.setInt(2, item.getQuantity());
        ps.setInt(3, item.getProductId());
        ps.setInt(4, item.getQuantity());
        if (ps.executeUpdate() != 1)
          throw new IllegalArgumentException("Insufficient stock for " + item.getProductName());
      }
  }

  private void restoreStock(Connection con, int orderId) throws SQLException {
    for (OrderDetail item : details(con, orderId))
      try (PreparedStatement ps =
          con.prepareStatement(
              "UPDATE dbo.bs_Products SET stock=stock+?,status=CASE WHEN status='Out of Stock' THEN"
                  + " 'Active' ELSE status END,updated_at=SYSUTCDATETIME() WHERE product_id=?")) {
        ps.setInt(1, item.getQuantity());
        ps.setInt(2, item.getProductId());
        ps.executeUpdate();
      }
  }

  private List<OrderDetail> details(Connection con, int id) throws SQLException {
    String sql =
        "SELECT"
            + " d.order_detail_id,d.product_id,p.product_name,p.thumbnail,p.sku,d.quantity,d.unit_price,d.subtotal"
            + " FROM dbo.bs_OrderDetails d JOIN dbo.bs_Products p ON p.product_id=d.product_id"
            + " WHERE d.order_id=?";
    List<OrderDetail> out = new ArrayList<>();
    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          OrderDetail d = new OrderDetail();
          d.setOrderDetailId(rs.getInt(1));
          d.setProductId(rs.getInt(2));
          d.setProductName(rs.getString(3));
          d.setThumbnail(rs.getString(4));
          d.setSku(rs.getString(5));
          d.setQuantity(rs.getInt(6));
          d.setUnitPrice(rs.getDouble(7));
          d.setSubtotal(rs.getDouble(8));
          out.add(d);
        }
      }
    }
    return out;
  }

  private AdminOrder map(ResultSet rs) throws SQLException {
    AdminOrder o = new AdminOrder();
    o.setOrderId(rs.getInt("order_id"));
    o.setUserId(rs.getInt("user_id"));
    o.setCustomerName(rs.getString("full_name"));
    o.setEmail(rs.getString("email"));
    o.setPhone(rs.getString("phone"));
    o.setAddressInfo(rs.getString("address_info"));
    o.setPaymentMethod(rs.getString("payment_method"));
    String payment = rs.getString("payment_status");
    o.setPaymentStatus(payment == null ? "Pending" : payment);
    o.setOrderStatus(rs.getString("order_status"));
    o.setNote(rs.getString("note"));
    o.setTotalAmount(rs.getBigDecimal("total_amount"));
    o.setShippingFee(rs.getBigDecimal("shipping_fee"));
    o.setDiscountAmount(rs.getBigDecimal("discount_amount"));
    o.setVoucherCode(rs.getString("voucher_code"));
    o.setCreatedAt(VietnamTime.fromUtc(rs.getTimestamp("created_at")));
    o.setUpdatedAt(VietnamTime.fromUtc(rs.getTimestamp("updated_at")));
    return o;
  }

  private void setDate(PreparedStatement ps, int index, LocalDate value) throws SQLException {
    if (value == null) ps.setNull(index, Types.DATE);
    else ps.setDate(index, java.sql.Date.valueOf(value));
  }

  private String clean(String value) {
    return value == null ? "" : value.trim();
  }
}
