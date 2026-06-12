package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class Address {

    private final int address_id;
    private final int user_id;
    private final String line1;
    private final String line2;
    private final String city;
    private final String state;
    private final String postal_code;
    private final String country;
    private final boolean is_default;
    private final Timestamp created_at;
    private final Timestamp updated_at;

    public Address(int address_id, int user_id, String line1, String line2, String city, String state, String postal_code, String country, boolean is_default, Timestamp created_at, Timestamp updated_at) {
        this.address_id = address_id;
        this.user_id = user_id;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.postal_code = postal_code;
        this.country = country;
        this.is_default = is_default;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getAddress_id() {
        return address_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public String getCountry() {
        return country;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }
}
