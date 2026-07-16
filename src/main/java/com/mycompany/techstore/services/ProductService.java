package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Models.Objects.Review;
import com.mycompany.techstore.Repositories.ProductRepository;
import java.util.List;
import java.util.Map;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    public List<Product> getAll() {
        return this.productRepository.getAll();
    }

    public Product getById(int id) {
        return this.productRepository.getById(id);
    }

    public List<Review> getReviewsByProductId(int id) {
        return this.productRepository.getReviewsByProductId(id);
    }

    public Review getReviewByOrderAndProduct(int orderId, int productId, int userId) {
        return this.productRepository.getReviewByOrderAndProduct(orderId, productId, userId);
    }

    public List<Integer> getReviewedProductIdsByOrder(int orderId, int userId) {
        return this.productRepository.getReviewedProductIdsByOrder(orderId, userId);
    }

    public boolean saveReview(int userId, int orderId, int productId, int rating, String comment) {
        return this.productRepository.saveReview(userId, orderId, productId, rating, comment);
    }

    public boolean canReviewOrderProduct(int orderId, int productId, int userId) {
        return this.productRepository.canReviewOrderProduct(orderId, productId, userId);
    }

    public List<Product> getByCategory(String categoryId) {
        return this.productRepository.getByCategory(categoryId);
    }

    public List<Product> search(String query, String categoryId, long minPrice, long maxPrice,
            Map<String, String> filters, String sortOrder) {
        return this.productRepository.search(query, categoryId, minPrice, maxPrice, filters, sortOrder);
    }
}
