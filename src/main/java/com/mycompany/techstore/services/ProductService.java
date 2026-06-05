package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.Product;
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

    public List<Product> getByCategory(String categoryId) {
        return this.productRepository.getByCategory(categoryId);
    }

    public List<Product> search(String query, String categoryId, long minPrice, long maxPrice,
            Map<String, String> filters, String sortOrder) {
        return this.productRepository.search(query, categoryId, minPrice, maxPrice, filters, sortOrder);
    }
}
