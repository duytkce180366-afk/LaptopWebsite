/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.Models.Objects;

/**
 *
 * @author Nguyen Lam Khang
 */
public class OrderDetail {
     private int orderDetailId;
    private int productId;
    private String productName;
    private String thumbnail;
    private String sku;
    private int quantity;
    private double unitPrice;
    private double subtotal;
 
    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }
 
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
 
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
 
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
 
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
 
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
 
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
 
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

}
