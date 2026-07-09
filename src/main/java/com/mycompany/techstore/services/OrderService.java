package com.mycompany.techstore.services;
 
import com.mycompany.techstore.Repositories.OrderRepository;
 



public class OrderService {

    private OrderRepository repo = new OrderRepository();

    public int placeOrder(int userId, String paymentMethod,
            String address, String district,
            String province, String phone) {
        return repo.placeOrder(userId, paymentMethod, address, district, province, phone);
    }

    public int placeOrder(int userId, String paymentMethod,
            String address, String district,
            String province, String phone,
            int voucherId, double discountAmount) {
        return repo.placeOrder(userId, paymentMethod, address, district, province, phone,
                voucherId, discountAmount);
    }

    public double getOrderTotal(int orderId) {
        return repo.getOrderTotal(orderId);
    }

    public boolean updateOrderStatus(int orderId, String status) {
        return repo.updateOrderStatus(orderId, status);
    }

    public boolean confirmPaymentSuccess(int orderId) {
        return repo.confirmPaymentSuccess(orderId);
    }
}