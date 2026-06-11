/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Order;
import com.mycompany.techstore.resources.DbClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Nguyen Lam Khang
 */
public class OrderRepository {

    public boolean placeOrder(
            int userId,
            String paymentMethod,
            String addressInfo,
            String phone) {

        Connection conn = null;

        try {

            conn = new DbClass().getConnection();

            conn.setAutoCommit(false);

            //----------------------------------
            // Ghép địa chỉ + phone
            //----------------------------------
            String fullAddress
                    = "Address: " + addressInfo
                    + " | Phone: " + phone;

            //----------------------------------
            // Tạo Order
            //----------------------------------
            String createOrder = "INSERT INTO bs_Orders("
                    + "user_id,total_amount,"
                    + "payment_method,"
                    + "order_status,"
                    + "address_info)"
                    + " VALUES(?,?,?,'Pending',?)";

            PreparedStatement psOrder
                    = conn.prepareStatement(
                            createOrder,
                            Statement.RETURN_GENERATED_KEYS);

            psOrder.setInt(1, userId);

            psOrder.setDouble(
                    2,
                    calculateTotal(userId, conn));

            psOrder.setString(
                    3,
                    paymentMethod);

            psOrder.setString(
                    4,
                    fullAddress);

            psOrder.executeUpdate();

            ResultSet rs
                    = psOrder.getGeneratedKeys();

            int orderId = 0;

            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            //----------------------------------
            // Lấy Cart
            //----------------------------------
            String cartSql
                    = "SELECT cart_id "
                    + "FROM bs_Cart "
                    + "WHERE user_id=?";

            PreparedStatement psCart
                    = conn.prepareStatement(cartSql);

            psCart.setInt(1, userId);

            ResultSet rsCart
                    = psCart.executeQuery();

            int cartId = 0;

            if (rsCart.next()) {
                cartId = rsCart.getInt("cart_id");
            }

            //----------------------------------
            // CartItems -> OrderDetails
            //----------------------------------
            String itemSql
                    = "SELECT * "
                    + "FROM bs_CartItems "
                    + "WHERE cart_id=?";

            PreparedStatement psItem
                    = conn.prepareStatement(itemSql);

            psItem.setInt(1, cartId);

            ResultSet rsItem
                    = psItem.executeQuery();

            while (rsItem.next()) {

                int productId
                        = rsItem.getInt("product_id");

                int quantity
                        = rsItem.getInt("quantity");

                double price
                        = rsItem.getDouble("unit_price");

                //----------------------------------
                // Insert OrderDetails
                //----------------------------------
                String detailSql
                        = "INSERT INTO bs_OrderDetails("
                        + "order_id,product_id,"
                        + "quantity,unit_price)"
                        + " VALUES(?,?,?,?)";

                PreparedStatement psDetail
                        = conn.prepareStatement(detailSql);

                psDetail.setInt(1, orderId);
                psDetail.setInt(2, productId);
                psDetail.setInt(3, quantity);
                psDetail.setDouble(4, price);

                psDetail.executeUpdate();

                //----------------------------------
                // Update Stock
                //----------------------------------
                String stockSql
                        = "UPDATE bs_Products "
                        + "SET stock = stock - ? "
                        + "WHERE product_id=?";

                PreparedStatement psStock
                        = conn.prepareStatement(stockSql);

                psStock.setInt(1, quantity);
                psStock.setInt(2, productId);

                psStock.executeUpdate();
            }

            //----------------------------------
            // Clear Cart
            //----------------------------------
            String clearSql
                    = "DELETE FROM bs_CartItems "
                    + "WHERE cart_id=?";

            PreparedStatement psClear
                    = conn.prepareStatement(clearSql);

            psClear.setInt(1, cartId);

            psClear.executeUpdate();

            conn.commit();

            return true;

        } catch (Exception e) {

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
            }

            e.printStackTrace();
        }

        return false;
    }

    public boolean cancelOrder(
            int orderId,
            String note) {

        String sql
                = "UPDATE bs_Orders "
                + "SET order_status='Cancelled', "
                + "note=? "
                + "WHERE order_id=? "
                + "AND order_status='Pending'";

        try {

            Connection conn
                    = new DbClass().getConnection();

            PreparedStatement ps
                    = conn.prepareStatement(sql);

            ps.setString(1, note);
            ps.setInt(2, orderId);

            int row = ps.executeUpdate();
            System.out.println("ROW = " + row);
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private double calculateTotal(
            int userId,
            Connection conn) throws Exception {

        String sql
    = "SELECT SUM(quantity * unit_price) total "
    + "FROM bs_CartItems ci "
    + "JOIN bs_Cart c "
    + "ON ci.cart_id=c.cart_id "
    + "WHERE c.user_id=?";

        PreparedStatement ps
                = conn.prepareStatement(sql);

        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getDouble("total");
        }

        return 0;
    }

    public List<Order> getOrdersByUser(int userId) {

        List<Order> list = new ArrayList<>();
        String sql
                = "SELECT order_id, "
                + "total_amount, "
                + "order_status, "
                + "created_at, "
                + "address_info, "
                + "note "
                + "FROM bs_Orders "
                + "WHERE user_id=?";

        try {

            Connection conn = new DbClass().getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Order order = new Order();

                order.setOrderId(rs.getInt("order_id"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setOrderStatus(rs.getString("order_status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                String info = rs.getString("address_info");
                order.setNote(
                        rs.getString("note")
                );

                if (info != null) {
    if (info.contains("|")) {
        String[] parts = info.split("\\|");
        order.setAddressInfo(parts[0].replace("Address:", "").trim());
        order.setPhone(parts[1].replace("Phone:", "").trim());
    } else {
        order.setAddressInfo(info.trim());
    }
}
                list.add(order);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
        public List<Map<String, Object>> getOrderDetails(int orderId) {
 
        List<Map<String, Object>> list = new ArrayList<>();
 
        String sql = "SELECT p.product_name, p.thumbnail, "
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
                item.put("thumbnail",    rs.getString("thumbnail"));
                item.put("quantity",     rs.getInt("quantity"));
                item.put("unit_price",   rs.getDouble("unit_price"));
                item.put("subtotal",     rs.getDouble("subtotal"));
                list.add(item);
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return list;
    }

}
