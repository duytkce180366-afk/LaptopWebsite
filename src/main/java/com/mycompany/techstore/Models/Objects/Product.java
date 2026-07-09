package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;
import java.util.Map;

public class Product {

    private final int product_id;
    private final String category_id;
    private final String category_name;
    private final String product_name;
    private final String brand_name;
    private final long price;
    private final String badge;
    private final Map<String, String> specs;
    private final int stock;
    private final String thumbnail;
    private final String warranty;
    private final String description;
    private final String status;
    private final Timestamp created_at;
    private final Timestamp updated_at;

    public Product(int product_id, String category_id, String category_name, String product_name, String brand_name,
            long price,
            String badge, Map<String, String> specs, int stock, String thumbnail,
            String warranty, String description) {
        this.product_id = product_id;
        this.category_id = category_id;
        this.category_name = category_name;
        this.product_name = product_name;
        this.brand_name = brand_name;
        this.price = price;
        this.badge = badge;
        this.specs = specs;
        this.stock = stock;
        this.thumbnail = thumbnail;
        this.warranty = warranty;
        this.description = description;
        this.status = null;
        this.created_at = null;
        this.updated_at = null;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getBrand_name() {
        return brand_name;
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

    public int getProductId() {
        return product_id;
    }

    public int getId() {
        return product_id;
    }

    public String getCategoryId() {
        return category_id;
    }

    public String getCategory() {
        return category_name;
    }

    public String getName() {
        return product_name;
    }

    public String getBrand() {
        return brand_name;
    }

    public long getPrice() {
        return price;
    }

    public String getBadge() {
        return badge;
    }

    public Map<String, String> getSpecs() {
        return specs;
    }

    public int getStock() {
        return stock;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWarranty() {
        return warranty;
    }

    public String getDescription() {
        return description;
    }

}
