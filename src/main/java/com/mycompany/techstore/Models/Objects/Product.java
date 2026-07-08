package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class Product {
    private int product_id;
    private String category_id;
    private String category_name;
    private String product_name;
    private String brand_name;
    private long price;
    private String badge;
    private Map<String, String> specs;
    private int stock;
    private String thumbnail;
    private String warranty;
    private String description;
    private String status;
    private Timestamp created_at;
    private Timestamp updated_at;
    private List<Review> reviews;

    public Product(int product_id, String category_id, String category_name, String product_name, String brand_name, long price,
                   String badge, Map<String, String> specs, int stock, String thumbnail,
                   String warranty, String description, List<Review> reviews) {
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
        this.reviews = reviews;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public int getId() {
        return product_id;
    }

    public void setId(int id) {
        this.product_id = id;
    }

    public String getCategoryId() {
        return category_id;
    }

    public void setCategoryId(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory() {
        return category_name;
    }

    public void setCategory(String category_name) {
        this.category_name = category_name;
    }

    public String getName() {
        return product_name;
    }

    public void setName(String product_name) {
        this.product_name = product_name;
    }

    public String getBrand() {
        return brand_name;
    }

    public void setBrand(String brand_name) {
        this.brand_name = brand_name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public Map<String, String> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, String> specs) {
        this.specs = specs;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        double total = reviews.stream().mapToInt(Review::getRating).sum();
        return Math.round((total / reviews.size()) * 10.0) / 10.0;
    }
}
