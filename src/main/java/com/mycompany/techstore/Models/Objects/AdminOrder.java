package com.mycompany.techstore.Models.Objects;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminOrder {
  private int orderId, userId;
  private String customerName,
      email,
      phone,
      addressInfo,
      paymentMethod,
      paymentStatus,
      orderStatus,
      note;
  private BigDecimal totalAmount, shippingFee, discountAmount;
  private Timestamp createdAt, updatedAt;
  private List<OrderDetail> details = new ArrayList<>();

  public int getOrderId() {
    return orderId;
  }

  public void setOrderId(int v) {
    orderId = v;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int v) {
    userId = v;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String v) {
    customerName = v;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String v) {
    email = v;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String v) {
    phone = v;
  }

  public String getAddressInfo() {
    return addressInfo;
  }

  public void setAddressInfo(String v) {
    addressInfo = v;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String v) {
    paymentMethod = v;
  }

  public String getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String v) {
    paymentStatus = v;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String v) {
    orderStatus = v;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String v) {
    note = v;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal v) {
    totalAmount = v;
  }

  public BigDecimal getShippingFee() {
    return shippingFee;
  }

  public void setShippingFee(BigDecimal v) {
    shippingFee = v;
  }

  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(BigDecimal v) {
    discountAmount = v;
  }

  public BigDecimal getFinalTotal() {
    return totalAmount.add(shippingFee).subtract(discountAmount);
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp v) {
    createdAt = v;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp v) {
    updatedAt = v;
  }

  public List<OrderDetail> getDetails() {
    return details;
  }

  public void setDetails(List<OrderDetail> v) {
    details = v;
  }
}
