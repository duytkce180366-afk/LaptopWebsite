package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class Category {

    private final String category_id;
    private final String category_name;
    private final String description;
    private final String status;
    private final Timestamp created_at;
    private final Timestamp updated_at;

    public Category(String category_id, String category_name) {
        this(category_id, category_name, null, null, null, null);
    }

    public Category(String category_id, String category_name, String description, String status,
            Timestamp created_at, Timestamp updated_at) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.description = description;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getDescription() {
        return description;
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

    public String getId() {
        return category_id;
    }

    public String getName() {
        return category_name;
    }

    public String getCategoryId() {
        return category_id;
    }

    public String getCategoryName() {
        return category_name;
    }
}
