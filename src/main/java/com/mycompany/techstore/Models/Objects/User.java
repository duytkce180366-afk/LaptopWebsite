package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class User {

    private final int user_id;
    private final int role_id;
    private final String full_name;
    private final String email;
    private final String phone;
    private final String password;
    private final String avatar;
    private final String status;
    private final Timestamp created_at;
    private final Timestamp updated_at;

    public User(int user_id, int role_id, String full_name, String email, String phone, String password, String avatar, String status, Timestamp created_at, Timestamp updated_at) {
        this.user_id = user_id;
        this.role_id = role_id;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.avatar = avatar;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
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
}