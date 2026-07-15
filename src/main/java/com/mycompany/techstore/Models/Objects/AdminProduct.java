package com.mycompany.techstore.Models.Objects;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminProduct {
    private int productId;
    private int categoryId;
    private int brandId;
    private String categoryName;
    private String brandName;
    private String sku;
    private String productName;
    private String description;
    private BigDecimal price;
    private int stock;
    private String thumbnail;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Map<String,String> specifications=new LinkedHashMap<>();

    public int getProductId(){return productId;} public void setProductId(int v){productId=v;}
    public int getCategoryId(){return categoryId;} public void setCategoryId(int v){categoryId=v;}
    public int getBrandId(){return brandId;} public void setBrandId(int v){brandId=v;}
    public String getCategoryName(){return categoryName;} public void setCategoryName(String v){categoryName=v;}
    public String getBrandName(){return brandName;} public void setBrandName(String v){brandName=v;}
    public String getSku(){return sku;} public void setSku(String v){sku=v;}
    public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public int getStock(){return stock;} public void setStock(int v){stock=v;}
    public String getThumbnail(){return thumbnail;} public void setThumbnail(String v){thumbnail=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;}
    public Timestamp getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Timestamp v){updatedAt=v;}
    public Map<String,String> getSpecifications(){return specifications;}
    public void setSpecifications(Map<String,String> v){specifications=v==null?new LinkedHashMap<>():v;}
}
