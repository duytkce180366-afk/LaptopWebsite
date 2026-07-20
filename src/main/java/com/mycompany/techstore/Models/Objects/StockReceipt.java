package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class StockReceipt {
    private long receiptId;
    private int productId,quantity,previousStock,resultingStock;
    private String sku,productName,note,adminName;
    private Timestamp createdAt;
    public long getReceiptId(){return receiptId;} public void setReceiptId(long v){receiptId=v;}
    public int getProductId(){return productId;} public void setProductId(int v){productId=v;}
    public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;}
    public int getPreviousStock(){return previousStock;} public void setPreviousStock(int v){previousStock=v;}
    public int getResultingStock(){return resultingStock;} public void setResultingStock(int v){resultingStock=v;}
    public String getSku(){return sku;} public void setSku(String v){sku=v;}
    public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getNote(){return note;} public void setNote(String v){note=v;}
    public String getAdminName(){return adminName;} public void setAdminName(String v){adminName=v;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;}
}
