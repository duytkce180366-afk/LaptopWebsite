package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class Product {

    private final int productId;
    private final int categoryId;
    private final int brandId;

    private final String sku;
    private final String productName;
    private final String description;

    private final long price;
    private final int stock;

    private final String thumbnail;
    private final String status;

    private final Timestamp created_at;
    private final Timestamp updated_at;

    public Product(int productId, int categoryId, int brandId, String sku, String productName, String description, long price, int stock, String thumbnail, String status, Timestamp created_at, Timestamp updated_at) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.sku = sku;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.thumbnail = thumbnail;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getProductId() {
        return productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }
}
