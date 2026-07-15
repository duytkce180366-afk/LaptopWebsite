package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

public class Product {

    private final int product_id;
    private final int categoryNumericId;
    private final int brandId;
    private final String sku;
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

    public Product(int product_id, String category_id, String category_name, String product_name,
            String brand_name, long price, String badge, Map<String, String> specs, int stock,
            String thumbnail, String warranty, String description) {
        this(product_id, 0, 0, null, category_id, category_name, product_name, brand_name,
                price, badge, specs, stock, thumbnail, warranty, description, null, null, null);
    }

    public Product(int productId, int categoryId, int brandId, String sku, String productName,
            String description, long price, int stock, String thumbnail, String status,
            Timestamp createdAt, Timestamp updatedAt) {
        this(productId, categoryId, brandId, sku, String.valueOf(categoryId), null, productName,
                null, price, null, Collections.emptyMap(), stock, thumbnail, null, description,
                status, createdAt, updatedAt);
    }

    public Product(int productId, int categoryNumericId, int brandId, String sku,
            String categoryId, String categoryName, String productName, String brandName,
            long price, String badge, Map<String, String> specs, int stock, String thumbnail,
            String warranty, String description, String status, Timestamp createdAt,
            Timestamp updatedAt) {
        this.product_id = productId;
        this.categoryNumericId = categoryNumericId;
        this.brandId = brandId;
        this.sku = sku;
        this.category_id = categoryId;
        this.category_name = categoryName;
        this.product_name = productName;
        this.brand_name = brandName;
        this.price = price;
        this.badge = badge;
        this.specs = specs == null ? Collections.emptyMap() : specs;
        this.stock = stock;
        this.thumbnail = thumbnail;
        this.warranty = warranty;
        this.description = description;
        this.status = status;
        this.created_at = createdAt;
        this.updated_at = updatedAt;
    }

    public int getProduct_id() { return product_id; }
    public int getProductId() { return product_id; }
    public int getId() { return product_id; }
    public String getCategory_id() { return category_id; }
    public String getCategoryId() { return category_id; }
    public int getCategoryNumericId() { return categoryNumericId; }
    public String getCategory_name() { return category_name; }
    public String getCategory() { return category_name; }
    public String getProduct_name() { return product_name; }
    public String getProductName() { return product_name; }
    public String getName() { return product_name; }
    public String getBrand_name() { return brand_name; }
    public String getBrand() { return brand_name; }
    public int getBrandId() { return brandId; }
    public String getSku() { return sku; }
    public long getPrice() { return price; }
    public String getBadge() { return badge; }
    public Map<String, String> getSpecs() { return specs; }
    public int getStock() { return stock; }
    public String getThumbnail() { return thumbnail; }
    public String getWarranty() { return warranty; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public Timestamp getCreated_at() { return created_at; }
    public Timestamp getUpdated_at() { return updated_at; }
}
