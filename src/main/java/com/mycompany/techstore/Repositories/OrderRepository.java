package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Order;
import com.mycompany.techstore.resources.DbClass;
import com.mycompany.techstore.services.VietnamTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

  private static final double SHIPPING_FEE = 0;

  // ================= PLACE ORDER =================
  // Order is created as 'Pending'. Stock and voucher are NOT deducted here anymore.
  // They are only deducted when the order moves to 'Confirmed'
  // (either by Admin action, or automatically on successful VNPay payment).
  public int placeOrder(
      int userId,
      String paymentMethod,
      String address,
      String district,
      String province,
      String phone) {
    return placeOrder(userId, paymentMethod, address, district, province, phone, 0, 0);
  }

  public int placeOrder(
      int userId,
      String paymentMethod,
      String address,
      String district,
      String province,
      String phone,
      int voucherId,
      double discountAmount) {

    Connection conn = null;

    try {
      conn = new DbClass().getConnection();
      conn.setAutoCommit(false);

      // Block if user already used this voucher
      if (voucherId > 0 && isVoucherUsedByUser(userId, voucherId, conn)) {
        conn.rollback();
        return -2;
      }

      double subtotal = calculateTotal(userId, conn);

      String createOrder =
          "INSERT INTO bs_Orders("
              + "user_id, voucher_id, total_amount, shipping_fee, discount_amount, "
              + "payment_method, order_status, phone, address_info)"
              + " VALUES(?, ?, ?, ?, ?, ?, 'Pending', ?, ?)";

      PreparedStatement psOrder =
          conn.prepareStatement(createOrder, Statement.RETURN_GENERATED_KEYS);

      psOrder.setInt(1, userId);
      if (voucherId > 0) {
        psOrder.setInt(2, voucherId);
      } else {
        psOrder.setNull(2, java.sql.Types.INTEGER);
      }
      psOrder.setDouble(3, subtotal);
      psOrder.setDouble(4, SHIPPING_FEE);
      psOrder.setDouble(5, discountAmount);
      psOrder.setString(6, paymentMethod);
      psOrder.setString(7, phone);
      psOrder.setString(8, address + ", " + district + ", " + province);
      psOrder.executeUpdate();

      ResultSet rs = psOrder.getGeneratedKeys();
      int orderId = -1;
      if (rs.next()) {
        orderId = rs.getInt(1);
      }

      // NOTE: voucher and stock deduction removed from here.
      // They now happen only in confirmOrder()/confirmPaymentSuccess()
      // when the order transitions from Pending to Confirmed.
      String cartSql = "SELECT cart_id FROM bs_Cart WHERE user_id=?";
      PreparedStatement psCart = conn.prepareStatement(cartSql);
      psCart.setInt(1, userId);
      ResultSet rsCart = psCart.executeQuery();
      int cartId = 0;
      if (rsCart.next()) {
        cartId = rsCart.getInt("cart_id");
      }

      String itemSql = "SELECT * FROM bs_CartItems WHERE cart_id=?";
      PreparedStatement psItem = conn.prepareStatement(itemSql);
      psItem.setInt(1, cartId);
      ResultSet rsItem = psItem.executeQuery();

      while (rsItem.next()) {
        int productId = rsItem.getInt("product_id");
        int quantity = rsItem.getInt("quantity");
        double price = rsItem.getDouble("unit_price");

        String detailSql =
            "INSERT INTO bs_OrderDetails("
                + "order_id, product_id, quantity, unit_price)"
                + " VALUES(?, ?, ?, ?)";
        PreparedStatement psDetail = conn.prepareStatement(detailSql);
        psDetail.setInt(1, orderId);
        psDetail.setInt(2, productId);
        psDetail.setInt(3, quantity);
        psDetail.setDouble(4, price);
        psDetail.executeUpdate();
      }

      // Cart is cleared once the order record + details are created,
      // regardless of payment method (the cart's job is done).
      String clearSql = "DELETE FROM bs_CartItems WHERE cart_id=?";
      PreparedStatement psClear = conn.prepareStatement(clearSql);
      psClear.setInt(1, cartId);
      psClear.executeUpdate();

      conn.commit();
      return orderId;

    } catch (Exception e) {
      try {
        if (conn != null) {
          conn.rollback();
        }
      } catch (Exception ex) {
      }
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      } catch (Exception ex) {
      }
    }

    return -1;
  }

  // ================= CONFIRM (Pending -> Confirmed) =================
  // Called by Admin for COD/manual orders, and automatically by
  // confirmPaymentSuccess() for successful VNPay payments.
  // Returns: 1 = success, 0 = invalid transition, -1 = system error, -3 = not enough stock
  public int confirmOrder(int orderId) {

    Connection conn = null;

    try {
      conn = new DbClass().getConnection();
      conn.setAutoCommit(false);

      int result = doConfirm(orderId, conn);

      if (result == 1) {
        conn.commit();
      } else {
        conn.rollback();
      }
      return result;

    } catch (Exception e) {
      try {
        if (conn != null) {
          conn.rollback();
        }
      } catch (Exception ex) {
      }
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      } catch (Exception ex) {
      }
    }

    return -1;
  }

  // Shared logic used by both confirmOrder() (admin/COD) and
  // confirmPaymentSuccess() (VNPay), so stock/voucher deduction stays consistent.
  private int doConfirm(int orderId, Connection conn) throws Exception {

    String checkSql = "SELECT order_status, voucher_id FROM bs_Orders WHERE order_id=?";
    PreparedStatement psCheck = conn.prepareStatement(checkSql);
    psCheck.setInt(1, orderId);
    ResultSet rs = psCheck.executeQuery();

    if (!rs.next()) {
      return 0;
    }

    String currentStatus = rs.getString("order_status");
    int voucherId = rs.getInt("voucher_id");
    boolean hasVoucher = !rs.wasNull() && voucherId > 0;

    // Orders can be confirmed from Pending, or from Payment Failed (after a successful retry)
    if (!"Pending".equals(currentStatus) && !"Payment Failed".equals(currentStatus)) {
      return 0;
    }

    // Check stock is sufficient for every item before deducting anything
    String itemsSql = "SELECT product_id, quantity FROM bs_OrderDetails WHERE order_id=?";
    PreparedStatement psItems = conn.prepareStatement(itemsSql);
    psItems.setInt(1, orderId);
    ResultSet rsItems = psItems.executeQuery();

    List<int[]> items = new ArrayList<>(); // [productId, quantity]
    while (rsItems.next()) {
      items.add(new int[] {rsItems.getInt("product_id"), rsItems.getInt("quantity")});
    }

    for (int[] item : items) {
      String stockCheckSql = "SELECT stock FROM bs_Products WHERE product_id=?";
      PreparedStatement psStockCheck = conn.prepareStatement(stockCheckSql);
      psStockCheck.setInt(1, item[0]);
      ResultSet rsStock = psStockCheck.executeQuery();
      if (rsStock.next()) {
        int currentStock = rsStock.getInt("stock");
        if (currentStock < item[1]) {
          return -3; // not enough stock, cannot confirm
        }
      }
    }

    // Deduct stock for each item
    for (int[] item : items) {
      String stockSql = "UPDATE bs_Products SET stock = stock - ? WHERE product_id=?";
      PreparedStatement psStock = conn.prepareStatement(stockSql);
      psStock.setInt(1, item[1]);
      psStock.setInt(2, item[0]);
      psStock.executeUpdate();
    }

    // Deduct voucher quantity
    if (hasVoucher) {
      String voucherSql =
          "UPDATE bs_Vouchers SET quantity = quantity - 1 "
              + "WHERE voucher_id = ? AND quantity > 0";
      PreparedStatement psVoucher = conn.prepareStatement(voucherSql);
      psVoucher.setInt(1, voucherId);
      psVoucher.executeUpdate();
    }

    // Move status to Confirmed
    String updateSql = "UPDATE bs_Orders SET order_status='Confirmed' WHERE order_id=?";
    PreparedStatement psUpdate = conn.prepareStatement(updateSql);
    psUpdate.setInt(1, orderId);
    psUpdate.executeUpdate();

    return 1;
  }

  // ================= SHIP (Confirmed -> Shipping) =================
  public boolean shipOrder(int orderId) {
    return transitionStatus(orderId, "Confirmed", "Shipping");
  }

  // ================= DELIVER (Shipping -> Delivered) =================
  public boolean deliverOrder(int orderId) {
    return transitionStatus(orderId, "Shipping", "Delivered");
  }

  // Generic guarded transition: only succeeds if current status matches expected value.
  // This is what prevents skipping steps (e.g. Pending -> Delivered directly).
  private boolean transitionStatus(int orderId, String fromStatus, String toStatus) {

    String sql = "UPDATE bs_Orders SET order_status=? " + "WHERE order_id=? AND order_status=?";

    try {
      Connection conn = new DbClass().getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, toStatus);
      ps.setInt(2, orderId);
      ps.setString(3, fromStatus);
      return ps.executeUpdate() > 0;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  // ================= CANCEL (Pending or Confirmed -> Cancelled) =================
  // Stock/voucher are only restored if the order was already Confirmed
  // (i.e. they were actually deducted). No restore needed from Pending.
  public boolean cancelOrder(int orderId, String note) {
    return cancelOrderInternal(orderId, null, note);
  }

  public boolean cancelOrder(int orderId, int userId, String note) {
    return cancelOrderInternal(orderId, userId, note);
  }

  private boolean cancelOrderInternal(int orderId, Integer userId, String note) {

    Connection conn = null;

    try {
      conn = new DbClass().getConnection();
      conn.setAutoCommit(false);

      String checkSql =
          "SELECT order_status, voucher_id FROM bs_Orders WHERE order_id=?"
              + (userId == null ? "" : " AND user_id=?");
      PreparedStatement psCheck = conn.prepareStatement(checkSql);
      psCheck.setInt(1, orderId);
      if (userId != null) {
        psCheck.setInt(2, userId);
      }
      ResultSet rs = psCheck.executeQuery();

      if (!rs.next()) {
        conn.rollback();
        return false;
      }

      String currentStatus = rs.getString("order_status");
      int voucherId = rs.getInt("voucher_id");
      boolean hasVoucher = !rs.wasNull() && voucherId > 0;

      // Only Pending or Confirmed orders can be cancelled.
      // Shipping/Delivered orders can no longer be cancelled.
      boolean isPending = "Pending".equals(currentStatus);
      boolean isConfirmed = "Confirmed".equals(currentStatus);
      boolean isPaymentFailed = "Payment Failed".equals(currentStatus);

      // Payment Failed behaves like Pending: cancellable, no stock/voucher was ever deducted
      if (!isPending && !isConfirmed && !isPaymentFailed) {
        conn.rollback();
        return false;
      }

      String updateSql =
          "UPDATE bs_Orders " + "SET order_status='Cancelled', note=? " + "WHERE order_id=?";
      PreparedStatement psUpdate = conn.prepareStatement(updateSql);
      psUpdate.setString(1, note);
      psUpdate.setInt(2, orderId);
      int row = psUpdate.executeUpdate();

      // Only restore stock/voucher if the order had already been Confirmed
      // (meaning stock/voucher were actually deducted at that point).
      if (row > 0 && isConfirmed) {

        if (hasVoucher) {
          String voucherSql =
              "UPDATE bs_Vouchers SET quantity = quantity + 1 " + "WHERE voucher_id = ?";
          PreparedStatement psVoucher = conn.prepareStatement(voucherSql);
          psVoucher.setInt(1, voucherId);
          psVoucher.executeUpdate();
        }

        String itemsSql = "SELECT product_id, quantity FROM bs_OrderDetails WHERE order_id=?";
        PreparedStatement psItems = conn.prepareStatement(itemsSql);
        psItems.setInt(1, orderId);
        ResultSet rsItems = psItems.executeQuery();

        while (rsItems.next()) {
          int productId = rsItems.getInt("product_id");
          int quantity = rsItems.getInt("quantity");

          String stockSql = "UPDATE bs_Products SET stock = stock + ? WHERE product_id=?";
          PreparedStatement psStock = conn.prepareStatement(stockSql);
          psStock.setInt(1, quantity);
          psStock.setInt(2, productId);
          psStock.executeUpdate();
        }
      }

      conn.commit();
      return row > 0;

    } catch (Exception e) {
      try {
        if (conn != null) {
          conn.rollback();
        }
      } catch (Exception ex) {
      }
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      } catch (Exception ex) {
      }
    }

    return false;
  }

  public boolean updateOrderStatus(int orderId, String status) {

    String sql = "UPDATE bs_Orders SET order_status=? WHERE order_id=?";

    try {
      Connection conn = new DbClass().getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, status);
      ps.setInt(2, orderId);
      return ps.executeUpdate() > 0;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }

  // ================= VNPay payment success =================
  // A successful VNPay payment is treated as equivalent to Admin confirmation:
  // it moves the order straight from Pending to Confirmed, deducting
  // stock/voucher using the same shared logic as confirmOrder().
  public boolean confirmPaymentSuccess(int orderId) {

    Connection conn = null;

    try {
      conn = new DbClass().getConnection();
      conn.setAutoCommit(false);

      // Idempotency guard: if already Confirmed/Shipping/Delivered, do nothing further
      String checkSql = "SELECT order_status FROM bs_Orders WHERE order_id=?";
      PreparedStatement psCheck = conn.prepareStatement(checkSql);
      psCheck.setInt(1, orderId);
      ResultSet rs = psCheck.executeQuery();

      if (!rs.next()) {
        conn.rollback();
        return false;
      }
      String currentStatus = rs.getString("order_status");
      // Allow confirming from Pending (first attempt) or Payment Failed (after retry).
      // Anything else (Confirmed/Shipping/Delivered/Cancelled) is already final -> skip.
      if (!"Pending".equals(currentStatus) && !"Payment Failed".equals(currentStatus)) {
        conn.commit();
        return true; // already processed, avoid double deduction
      }
      int result = doConfirm(orderId, conn);

      if (result == 1) {
        conn.commit();
        return true;
      } else {
        conn.rollback();
        return false;
      }

    } catch (Exception e) {
      try {
        if (conn != null) {
          conn.rollback();
        }
      } catch (Exception ex) {
      }
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      } catch (Exception ex) {
      }
    }

    return false;
  }

  public double getOrderTotal(int orderId) {

    String sql =
        "SELECT total_amount + shipping_fee - discount_amount AS final_total "
            + "FROM bs_Orders WHERE order_id=?";

    try {
      Connection conn = new DbClass().getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setInt(1, orderId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getDouble("final_total");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  private double calculateTotal(int userId, Connection conn) throws Exception {

    String sql =
        "SELECT SUM(quantity * unit_price) total "
            + "FROM bs_CartItems ci "
            + "JOIN bs_Cart c ON ci.cart_id = c.cart_id "
            + "WHERE c.user_id=?";

    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, userId);
    ResultSet rs = ps.executeQuery();

    if (rs.next()) {
      return rs.getDouble("total");
    }

    return 0;
  }

  public List<Order> getOrdersByUser(int userId) {

    List<Order> list = new ArrayList<>();
    String sql =
        "SELECT order_id, total_amount, shipping_fee, discount_amount, "
            + "payment_method, order_status, created_at, address_info, phone, note "
            + "FROM bs_Orders "
            + "WHERE user_id = ? "
            + "ORDER BY created_at DESC";

    try {
      Connection conn = new DbClass().getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setInt(1, userId);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setShippingFee(rs.getDouble("shipping_fee"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setCreatedAt(VietnamTime.fromUtc(rs.getTimestamp("created_at")));
        order.setNote(rs.getString("note"));
        order.setPhone(rs.getString("phone"));
        order.setAddressInfo(rs.getString("address_info"));
        list.add(order);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  // ================= ADMIN: get all orders =================
  // Returns Map-based rows (rather than the Order model) to avoid depending
  // on Order.java fields not yet confirmed. Adjust field names to match
  // your actual bs_Orders columns if different.
  public List<Map<String, Object>> getAllOrdersForAdmin() {

    List<Map<String, Object>> list = new ArrayList<>();
    String sql =
        "SELECT order_id, user_id, total_amount, shipping_fee, discount_amount, "
            + "payment_method, order_status, created_at "
            + "FROM bs_Orders "
            + "ORDER BY created_at DESC";

    try {
      Connection conn = new DbClass().getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        row.put("order_id", rs.getInt("order_id"));
        row.put("user_id", rs.getInt("user_id"));
        row.put("total_amount", rs.getDouble("total_amount"));
        row.put("shipping_fee", rs.getDouble("shipping_fee"));
        row.put("discount_amount", rs.getDouble("discount_amount"));
        row.put("payment_method", rs.getString("payment_method"));
        row.put("order_status", rs.getString("order_status"));
        row.put("created_at", VietnamTime.fromUtc(rs.getTimestamp("created_at")));
        list.add(row);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  public List<Map<String, Object>> getOrderDetails(int orderId) {

    List<Map<String, Object>> list = new ArrayList<>();
    String sql =
        "SELECT p.product_name, p.thumbnail, "
            + "od.quantity, od.unit_price, od.subtotal "
            + "FROM bs_OrderDetails od "
            + "JOIN bs_Products p ON od.product_id = p.product_id "
            + "WHERE od.order_id = ?";

    try {
      Connection conn = new DbClass().getConnection();
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setInt(1, orderId);
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        Map<String, Object> item = new HashMap<>();
        item.put("product_name", rs.getString("product_name"));
        item.put("thumbnail", rs.getString("thumbnail"));
        item.put("quantity", rs.getInt("quantity"));
        item.put("unit_price", rs.getDouble("unit_price"));
        item.put("subtotal", rs.getDouble("subtotal"));
        list.add(item);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  private boolean isVoucherUsedByUser(int userId, int voucherId, Connection conn) throws Exception {
    String sql =
        "SELECT 1 FROM bs_Orders "
            + "WHERE user_id = ? AND voucher_id = ? "
            + "AND order_status NOT IN ('Cancelled', 'Payment Failed')";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setInt(1, userId);
    ps.setInt(2, voucherId);
    ResultSet rs = ps.executeQuery();
    return rs.next();
  }

  public boolean isVoucherUsedByUser(int userId, int voucherId) {
    String sql =
        "SELECT 1 FROM bs_Orders "
            + "WHERE user_id = ? AND voucher_id = ? "
            + "AND order_status <> 'Cancelled'";
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setInt(2, voucherId);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // Verifies the order belongs to this user, uses VNPay, and is currently in "Payment Failed"
  // state.

  public boolean canRetryPayment(int orderId, int userId) {
    String sql =
        "SELECT 1 FROM bs_Orders "
            + "WHERE order_id = ? AND user_id = ? "
            + "AND payment_method = 'VNPay' AND order_status = 'Payment Failed'";
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, orderId);
      ps.setInt(2, userId);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  // Automatically cancels VNPay orders that have been sitting in Pending or
  // Payment Failed for more than 15 minutes. Called right before displaying
  // order lists, since there is no background scheduler in this project.

  public void autoCancelExpiredVnpayOrders() {

    String selectSql =
        "SELECT order_id FROM bs_Orders "
            + "WHERE payment_method = 'VNPay' "
            + "AND order_status IN ('Pending', 'Payment Failed') "
            + "AND DATEDIFF(MINUTE, created_at, SYSUTCDATETIME()) >= 15";

    List<Integer> expiredIds = new ArrayList<>();

    try (Connection conn = new DbClass().getConnection();
        PreparedStatement ps = conn.prepareStatement(selectSql);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        expiredIds.add(rs.getInt("order_id"));
      }

    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    // Reuse the existing cancelOrder() logic so stock/voucher rules stay consistent
    // (Pending/Payment Failed never had stock deducted, so nothing to restore).
    for (int orderId : expiredIds) {
      cancelOrder(orderId, "Payment session expired (15 minutes)");
    }
  }

  // Restores this order's items back into the user's cart, cancels the old
  // order (no stock/voucher restore needed since Pending/Payment Failed never
  // deducted them), and returns voucher info (if any) so the Controller can
  // restore it into session for the checkout page.

  public Map<String, Object> retryToCheckout(int orderId, int userId) {

    Connection conn = null;

    try {
      conn = new DbClass().getConnection();
      conn.setAutoCommit(false);

      // Verify ownership + status
      String checkSql =
          "SELECT order_status, payment_method, voucher_id, discount_amount "
              + "FROM bs_Orders WHERE order_id = ? AND user_id = ?";
      PreparedStatement psCheck = conn.prepareStatement(checkSql);
      psCheck.setInt(1, orderId);
      psCheck.setInt(2, userId);
      ResultSet rs = psCheck.executeQuery();

      if (!rs.next()) {
        conn.rollback();
        return null;
      }

      String status = rs.getString("order_status");
      String paymentMethod = rs.getString("payment_method");
      int voucherId = rs.getInt("voucher_id");
      boolean hasVoucher = !rs.wasNull() && voucherId > 0;
      double discountAmount = rs.getDouble("discount_amount");

      boolean canRetry =
          "VNPay".equals(paymentMethod)
              && ("Pending".equals(status) || "Payment Failed".equals(status));

      if (!canRetry) {
        conn.rollback();
        return null;
      }

      // Get or create the user's cart
      int cartId = getOrCreateCartId(userId, conn);

      // Restore each order item back into the cart
      String itemsSql =
          "SELECT product_id, quantity, unit_price FROM bs_OrderDetails WHERE order_id=?";
      PreparedStatement psItems = conn.prepareStatement(itemsSql);
      psItems.setInt(1, orderId);
      ResultSet rsItems = psItems.executeQuery();

      while (rsItems.next()) {
        int productId = rsItems.getInt("product_id");
        int quantity = rsItems.getInt("quantity");
        double unitPrice = rsItems.getDouble("unit_price");

        String insertSql =
            "INSERT INTO bs_CartItems (cart_id, product_id, quantity, unit_price) "
                + "VALUES (?, ?, ?, ?)";
        PreparedStatement psInsert = conn.prepareStatement(insertSql);
        psInsert.setInt(1, cartId);
        psInsert.setInt(2, productId);
        psInsert.setInt(3, quantity);
        psInsert.setDouble(4, unitPrice);
        psInsert.executeUpdate();
      }

      // Cancel the old order (Pending/Payment Failed -> Cancelled, no stock/voucher restore needed)
      String updateSql = "UPDATE bs_Orders SET order_status='Cancelled', note=? WHERE order_id=?";
      PreparedStatement psUpdate = conn.prepareStatement(updateSql);
      psUpdate.setString(1, "Moved back to checkout for retry");
      psUpdate.setInt(2, orderId);
      psUpdate.executeUpdate();

      conn.commit();

      Map<String, Object> result = new HashMap<>();
      result.put("voucherId", hasVoucher ? voucherId : null);
      result.put("discountAmount", discountAmount);
      return result;

    } catch (Exception e) {
      try {
        if (conn != null) {
          conn.rollback();
        }
      } catch (Exception ex) {
      }
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.setAutoCommit(true);
        }
      } catch (Exception ex) {
      }
    }

    return null;
  }

  private int getOrCreateCartId(int userId, Connection conn) throws Exception {
    String selectSql = "SELECT cart_id FROM bs_Cart WHERE user_id=?";
    PreparedStatement psSelect = conn.prepareStatement(selectSql);
    psSelect.setInt(1, userId);
    ResultSet rs = psSelect.executeQuery();

    if (rs.next()) {
      return rs.getInt("cart_id");
    }

    String insertSql = "INSERT INTO bs_Cart (user_id) VALUES (?)";
    PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
    psInsert.setInt(1, userId);
    psInsert.executeUpdate();

    ResultSet rsKeys = psInsert.getGeneratedKeys();
    if (rsKeys.next()) {
      return rsKeys.getInt(1);
    }

    throw new Exception("Failed to create cart for user " + userId);
  }
}
