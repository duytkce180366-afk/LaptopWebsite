/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.CartItem;
import com.mycompany.techstore.Repositories.CartRepository;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DuyTran
 */
public class CartService {

    private CartRepository repo
            = new CartRepository();

    public boolean addProduct(
            int userId,
            int productId,
            int quantity,
            double price) {

        int cartId
                = repo.getCartIdByUserId(userId);

        if (cartId == -1) {
            cartId
                    = repo.createCart(userId);
        }

        CartItem item
                = repo.getCartItem(
                        cartId,
                        productId);

        if (item == null) {

            return repo.addCartItem(
                    cartId,
                    productId,
                    quantity,
                    price);
        }

        int newQuantity
                = item.getQuantity()
                + quantity;

        return repo.updateQuantity(
                cartId,
                newQuantity);
    }

    public List<CartItem> getCartItems(int userId) {

        int cartId
                = repo.getCartIdByUserId(userId);

        if (cartId == -1) {
            return new ArrayList<>();
        }

        return repo.getCartItems(cartId);
    }
}
