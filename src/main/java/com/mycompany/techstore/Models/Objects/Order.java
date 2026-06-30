package com.mycompany.techstore.Models.Objects;
 
import java.sql.Timestamp;
 
/**
 * @author Nguyen Lam Khang
 */
public class Order {
 
    private int orderId;
    private int voucherId;
    private double totalAmount;
    private double shippingFee;
    private double discountAmount;
    private String paymentMethod;
    private String orderStatus;
    private Timestamp createdAt;
    private String addressInfo;
    private String phone;
    private String note;
 
    public Order() {}
 
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
 
    public int getVoucherId() { return voucherId; }
    public void setVoucherId(int voucherId) { this.voucherId = voucherId; }
 
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
 
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
 
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
 
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
 
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
 
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
 
    public String getAddressInfo() { return addressInfo; }
    public void setAddressInfo(String addressInfo) { this.addressInfo = addressInfo; }
 
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
 
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
 
    // Calculated field: final total = subtotal + shipping - discount
    public double getFinalTotal() {
        return totalAmount + shippingFee - discountAmount;
    }
}