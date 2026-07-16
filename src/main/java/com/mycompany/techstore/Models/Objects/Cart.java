package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class Cart {

    private int cartId;
    private int userId;
    private Timestamp createdAt;

    public Cart() {
    }

    public Cart(int cartId, int userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
