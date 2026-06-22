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

        int stock
                = repo.getProductStock(productId);

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

        // Chưa có sản phẩm trong cart
        if (item == null) {

            if (quantity > stock) {
                return false;
            }

            return repo.addCartItem(
                    cartId,
                    productId,
                    quantity,
                    price);
        }

        // Đã có sản phẩm trong cart
        int newQuantity
                = item.getQuantity()
                + quantity;

        if (newQuantity > stock) {
            return false;
        }

        return repo.updateQuantity(
                item.getCartItemId(),
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

    public boolean deleteCartItem(int cartItemId) {
        return repo.deleteCartItem(cartItemId);
    }

    public int getProductStock(int productId) {
        return repo.getProductStock(productId);
    }

    public double getCartTotal(int userId) {

        List<CartItem> items = getCartItems(userId);

        double total = 0;

        for (CartItem item : items) {
            total += item.getSubtotal();
        }

        return total;
    }
}
