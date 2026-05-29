package Models.Objects;

import java.util.List;
import java.util.Map;

public class Product {
    private int id;
    private String categoryId;
    private String category;
    private String name;
    private String brand;
    private long price;
    private String badge;
    private Map<String, String> specs;
    private int stock;
    private String image;
    private String warranty;
    private String description;
    private List<Review> reviews;

    public Product(int id, String categoryId, String category, String name, String brand, long price,
                   String badge, Map<String, String> specs, int stock, String image,
                   String warranty, String description, List<Review> reviews) {
        this.id = id;
        this.categoryId = categoryId;
        this.category = category;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.badge = badge;
        this.specs = specs;
        this.stock = stock;
        this.image = image;
        this.warranty = warranty;
        this.description = description;
        this.reviews = reviews;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
