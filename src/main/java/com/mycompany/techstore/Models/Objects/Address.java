package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class Address {

    private final int address_id;
    private final int user_id;
    private final String receiver_name;
    private final String phone;
    private final String province;
    private final String postal_code;
    private final String ward;
    private final boolean is_default;
    private final Timestamp created_at;

    public Address(int address_id, int user_id, String receiver_name, String phone, String province, String postal_code, String ward, boolean is_default, Timestamp created_at) {
        this.address_id = address_id;
        this.user_id = user_id;
        this.receiver_name = receiver_name;
        this.phone = phone;
        this.postal_code = postal_code;
        this.province = province;
        this.ward = ward;
        this.is_default = is_default;
        this.created_at = created_at;
    }

    public int getAddress_id() {
        return address_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getProvince() {
        return province;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public String getWard() {
        return ward;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }
}
