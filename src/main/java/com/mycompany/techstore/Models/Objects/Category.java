package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class Category {
    private String category_id;
    private String category_name;
    private String description;
    private String status;
    private Timestamp created_at;
    private Timestamp updated_at;
    private List<Map<String, Object>> menuGroups;
    private List<Map<String, String>> filters;

    public Category(String category_id, String category_name, List<Map<String, Object>> menuGroups, List<Map<String, String>> filters) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.menuGroups = menuGroups;
        this.filters = filters;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getId() {
        return category_id;
    }

    public void setId(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return category_name;
    }

    public void setName(String category_name) {
        this.category_name = category_name;
    }

    public String getCategoryId() {
        return category_id;
    }

    public void setCategoryId(String category_id) {
        this.category_id = category_id;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public List<Map<String, Object>> getMenuGroups() {
        return menuGroups;
    }

    public void setMenuGroups(List<Map<String, Object>> menuGroups) {
        this.menuGroups = menuGroups;
    }

    public List<Map<String, String>> getFilters() {
        return filters;
    }

    public void setFilters(List<Map<String, String>> filters) {
        this.filters = filters;
    }
}
