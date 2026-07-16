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

public class OrderRepository {

    private static final double SHIPPING_FEE = 0;

    public int placeOrder(int userId, String paymentMethod,
            String address, String district,
            String province, String phone) {
        return placeOrder(userId, paymentMethod, address, district, province, phone, 0, 0);
    }

    public int placeOrder(int userId, String paymentMethod,
            String address, String district,
            String province, String phone,
            int voucherId, double discountAmount) {

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

            String createOrder = "INSERT INTO bs_Orders("
                    + "user_id, voucher_id, total_amount, shipping_fee, discount_amount, "
                    + "payment_method, order_status, phone, address_info)"
                    + " VALUES(?, ?, ?, ?, ?, ?, 'Pending', ?, ?)";

            PreparedStatement psOrder = conn.prepareStatement(
                    createOrder, Statement.RETURN_GENERATED_KEYS);

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
            String paymentSql = "INSERT INTO bs_Payments(order_id,payment_method,payment_status,created_at,updated_at) "
                    + "VALUES(?,?,'Pending',SYSUTCDATETIME(),SYSUTCDATETIME())";
            try (PreparedStatement psPayment = conn.prepareStatement(paymentSql)) {
                psPayment.setInt(1, orderId);
                psPayment.setString(2, paymentMethod);
                psPayment.executeUpdate();
            }

            // For non-VNPay orders, deduct voucher immediately
            if (!"VNPay".equals(paymentMethod) && voucherId > 0) {
                String voucherSql = "UPDATE bs_Vouchers SET quantity = quantity - 1 "
                        + "WHERE voucher_id = ? AND quantity > 0";
                PreparedStatement psVoucher = conn.prepareStatement(voucherSql);
                psVoucher.setInt(1, voucherId);
                psVoucher.executeUpdate();
            }

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
                int quantity  = rsItem.getInt("quantity");
                double price  = rsItem.getDouble("unit_price");

                String detailSql = "INSERT INTO bs_OrderDetails("
                        + "order_id, product_id, quantity, unit_price)"
                        + " VALUES(?, ?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(detailSql);
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, productId);
                psDetail.setInt(3, quantity);
                psDetail.setDouble(4, price);
                psDetail.executeUpdate();

                // For non-VNPay orders, deduct stock immediately
                if (!"VNPay".equals(paymentMethod)) {
                    String stockSql = "UPDATE bs_Products SET stock = stock - ? WHERE product_id=?";
                    PreparedStatement psStock = conn.prepareStatement(stockSql);
                    psStock.setInt(1, quantity);
                    psStock.setInt(2, productId);
                    psStock.executeUpdate();
                }
            }

            // For non-VNPay orders, clear cart immediately
            if (!"VNPay".equals(paymentMethod)) {
                String clearSql = "DELETE FROM bs_CartItems WHERE cart_id=?";
                PreparedStatement psClear = conn.prepareStatement(clearSql);
                psClear.setInt(1, cartId);
                psClear.executeUpdate();
            }

            conn.commit();
            return orderId;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) {}
        }

        return -1;
    }

    public boolean cancelOrder(int orderId, String note) {

        Connection conn = null;

        try {
            conn = new DbClass().getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT order_status, payment_method, voucher_id "
                    + "FROM bs_Orders WHERE order_id=? AND user_id=?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, orderId);
            psCheck.setInt(2, userId);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            String currentStatus = rs.getString("order_status");
            String paymentMethod = rs.getString("payment_method");
            int voucherId = rs.getInt("voucher_id");
            boolean hasVoucher = !rs.wasNull() && voucherId > 0;

            // Only allow cancelling Pending orders
            if (!"Pending".equals(currentStatus)) {
                conn.rollback();
                return false;
            }

            // Stock/voucher deducted only for non-VNPay or Completed VNPay
            boolean wasDeducted = !"VNPay".equals(paymentMethod) || "Completed".equals(currentStatus);

            String updateSql = "UPDATE bs_Orders "
                    + "SET order_status='Cancelled', note=? "
                    + "WHERE order_id=?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setString(1, note);
            psUpdate.setInt(2, orderId);
            int row = psUpdate.executeUpdate();

            if (row > 0 && wasDeducted) {

                // Restore voucher quantity
                if (hasVoucher) {
                    String voucherSql = "UPDATE bs_Vouchers SET quantity = quantity + 1 "
                            + "WHERE voucher_id = ?";
                    PreparedStatement psVoucher = conn.prepareStatement(voucherSql);
                    psVoucher.setInt(1, voucherId);
                    psVoucher.executeUpdate();
                }

                // Restore product stock
                String itemsSql = "SELECT product_id, quantity FROM bs_OrderDetails WHERE order_id=?";
                PreparedStatement psItems = conn.prepareStatement(itemsSql);
                psItems.setInt(1, orderId);
                ResultSet rsItems = psItems.executeQuery();

                while (rsItems.next()) {
                    int productId = rsItems.getInt("product_id");
                    int quantity  = rsItems.getInt("quantity");

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
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) {}
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

    public boolean confirmPaymentSuccess(int orderId) {

        Connection conn = null;

        try {
            conn = new DbClass().getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT order_status, voucher_id FROM bs_Orders WHERE order_id=?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, orderId);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            String currentStatus = rs.getString("order_status");
            int voucherId = rs.getInt("voucher_id");
            boolean hasVoucher = !rs.wasNull() && voucherId > 0;

            // Already completed — avoid double deduction
            if ("Completed".equals(currentStatus)) {
                conn.commit();
                return true;
            }

            // Update order status to Completed
            String updateSql = "UPDATE bs_Orders SET order_status='Completed' WHERE order_id=?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setInt(1, orderId);
            psUpdate.executeUpdate();

            // Deduct voucher quantity
            if (hasVoucher) {
                String voucherSql = "UPDATE bs_Vouchers SET quantity = quantity - 1 "
                        + "WHERE voucher_id = ? AND quantity > 0";
                PreparedStatement psVoucher = conn.prepareStatement(voucherSql);
                psVoucher.setInt(1, voucherId);
                psVoucher.executeUpdate();
            }

            // Deduct product stock (was not deducted at placeOrder for VNPay)
            String itemsSql = "SELECT product_id, quantity FROM bs_OrderDetails WHERE order_id=?";
            PreparedStatement psItems = conn.prepareStatement(itemsSql);
            psItems.setInt(1, orderId);
            ResultSet rsItems = psItems.executeQuery();

            while (rsItems.next()) {
                int productId = rsItems.getInt("product_id");
                int quantity  = rsItems.getInt("quantity");

                String stockSql = "UPDATE bs_Products SET stock = stock - ? WHERE product_id=?";
                PreparedStatement psStock = conn.prepareStatement(stockSql);
                psStock.setInt(1, quantity);
                psStock.setInt(2, productId);
                psStock.executeUpdate();
            }

            // Clear cart after VNPay payment confirmed
            String userSql = "SELECT user_id FROM bs_Orders WHERE order_id=?";
            PreparedStatement psUser = conn.prepareStatement(userSql);
            psUser.setInt(1, orderId);
            ResultSet rsUser = psUser.executeQuery();

            if (rsUser.next()) {
                int confirmedUserId = rsUser.getInt("user_id");

                String cartSql = "SELECT cart_id FROM bs_Cart WHERE user_id=?";
                PreparedStatement psCart = conn.prepareStatement(cartSql);
                psCart.setInt(1, confirmedUserId);
                ResultSet rsCart = psCart.executeQuery();

                if (rsCart.next()) {
                    int cartId = rsCart.getInt("cart_id");
                    String clearSql = "DELETE FROM bs_CartItems WHERE cart_id=?";
                    PreparedStatement psClear = conn.prepareStatement(clearSql);
                    psClear.setInt(1, cartId);
                    psClear.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception ex) {}
        }

        return false;
    }

    public double getOrderTotal(int orderId) {

        String sql = "SELECT total_amount + shipping_fee - discount_amount AS final_total "
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

        String sql = "SELECT SUM(quantity * unit_price) total "
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
        String sql = "SELECT order_id, total_amount, shipping_fee, discount_amount, "
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
                order.setCreatedAt(rs.getTimestamp("created_at"));
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

    private boolean isVoucherUsedByUser(int userId, int voucherId, Connection conn) throws Exception {
        String sql = "SELECT 1 FROM bs_Orders "
                + "WHERE user_id = ? AND voucher_id = ? "
                + "AND ("
                + "  (payment_method <> 'VNPay' AND order_status <> 'Cancelled')"
                + "  OR (payment_method = 'VNPay' AND order_status = 'Completed')"
                + ")";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, voucherId);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public boolean isVoucherUsedByUser(int userId, int voucherId) {
        String sql = "SELECT 1 FROM bs_Orders "
                + "WHERE user_id = ? AND voucher_id = ? "
                + "AND ("
                + "  (payment_method <> 'VNPay' AND order_status <> 'Cancelled')"
                + "  OR (payment_method = 'VNPay' AND order_status = 'Completed')"
                + ")";
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
}