package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class Address {

    private final int addressId;
    private final int userId;
    private final String homeAddress;
    private final String phone;
    private final String province;
    private final String postalCode;
    private final String ward;
    private final boolean isDefault;
    private final Timestamp createdAt;

    public Address(int addressId, int userId, String homeAddress, String phone, String province, String postalCode, String ward, boolean isDefault, Timestamp createdAt) {
        this.addressId = addressId;
        this.userId = userId;
        this.homeAddress = homeAddress;
        this.phone = phone;
        this.province = province;
        this.postalCode = postalCode;
        this.ward = ward;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public int getAddressId() {
        return addressId;
    }

    public int getUserId() {
        return userId;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getPhone() {
        return phone;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getWard() {
        return ward;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
