package com.mycompany.techstore.Repositories;
 
import com.mycompany.techstore.Models.Objects.Order;
import com.mycompany.techstore.Models.Objects.OrderDetail;
import com.mycompany.techstore.resources.DbClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
 
/**
 * @author Nguyen Lam Khang
 */
public class OrderDetailRepository {
 
    public Order getOrderById(int orderId, int userId) {
 
        String sql = "SELECT order_id, total_amount, shipping_fee, discount_amount, "
                   + "order_status, created_at, address_info, phone, note "
                   + "FROM bs_Orders "
                   + "WHERE order_id = ? AND user_id = ?";
 
        try {
            Connection conn = new DbClass().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                Order o = new Order();
                o.setOrderId(rs.getInt("order_id"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setShippingFee(rs.getDouble("shipping_fee"));
                o.setDiscountAmount(rs.getDouble("discount_amount"));
                o.setOrderStatus(rs.getString("order_status"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                o.setNote(rs.getString("note"));
                o.setAddressInfo(rs.getString("address_info"));
                o.setPhone(rs.getString("phone"));
                return o;
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return null;
    }
 
    public List<OrderDetail> getDetailsByOrderId(int orderId) {
 
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.order_detail_id, od.quantity, od.unit_price, od.subtotal, "
                   + "p.product_id, p.product_name, p.thumbnail, p.sku "
                   + "FROM bs_OrderDetails od "
                   + "JOIN bs_Products p ON od.product_id = p.product_id "
                   + "WHERE od.order_id = ?";
 
        try {
            Connection conn = new DbClass().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                OrderDetail d = new OrderDetail();
                d.setOrderDetailId(rs.getInt("order_detail_id"));
                d.setProductId(rs.getInt("product_id"));
                d.setProductName(rs.getString("product_name"));
                d.setThumbnail(rs.getString("thumbnail"));
                d.setSku(rs.getString("sku"));
                d.setQuantity(rs.getInt("quantity"));
                d.setUnitPrice(rs.getDouble("unit_price"));
                d.setSubtotal(rs.getDouble("subtotal"));
                list.add(d);
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return list;
    }
}