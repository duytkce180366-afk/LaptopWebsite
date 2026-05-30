package com.mycompany.techstore.Models.Objects;

public class Role {

    private final int role_id;
    private final String role_name;

    public Role(int role_id, String role_name) {
        this.role_id = role_id;
        this.role_name = role_name;
    }

    public int getRole_id() {
        return role_id;
    }

    public String getRole_name() {
        return role_name;
    }
}
