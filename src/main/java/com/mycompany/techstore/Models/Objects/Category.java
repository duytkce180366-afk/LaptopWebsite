package com.mycompany.techstore.Models.Objects;

import java.util.List;
import java.util.Map;

public class Category {
    private String id;
    private String name;
    private List<Map<String, Object>> menuGroups;
    private List<Map<String, String>> filters;

    public Category(String id, String name, List<Map<String, Object>> menuGroups, List<Map<String, String>> filters) {
        this.id = id;
        this.name = name;
        this.menuGroups = menuGroups;
        this.filters = filters;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
