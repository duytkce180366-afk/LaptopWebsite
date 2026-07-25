package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.Category;
import com.mycompany.techstore.Repositories.CategoryRepository;
import java.util.List;
import java.util.Map;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    public List<Category> getAll() {
        return this.categoryRepository.getAll();
    }

    public Category getById(String id) {
        return this.categoryRepository.getById(id);
    }

    public Map<String, List<Map<String, String>>> getSecondaryFilterOptions() {
        return this.categoryRepository.getSecondaryFilterOptions();
    }

    public Map<String, List<Map<String, Object>>> getMenuGroupsByCategory() {
        return this.categoryRepository.getMenuGroupsByCategory();
    }

    public Map<String, List<Map<String, String>>> getFiltersByCategory() {
        return this.categoryRepository.getFiltersByCategory();
    }

    public List<Map<String, String>> getSecondaryFilterOptionsByCategory(String categoryId) {
        return this.categoryRepository.getSecondaryFilterOptionsByCategory(categoryId);
    }
}
