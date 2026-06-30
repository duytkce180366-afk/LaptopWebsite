package com.mycompany.techstore.services;
 
import com.mycompany.techstore.Repositories.OrderRepository;
 
public class OrderService {
 
    private OrderRepository repo = new OrderRepository();
 
    public boolean placeOrder(int userId, String paymentMethod,
            String address, String district,
            String province, String phone) {
        return repo.placeOrder(userId, paymentMethod, address, district, province, phone);
    }
 
    public boolean placeOrder(int userId, String paymentMethod,
            String address, String district,
            String province, String phone,
            int voucherId, double discountAmount) {
        return repo.placeOrder(userId, paymentMethod, address, district, province, phone,
                voucherId, discountAmount);
    }
}
