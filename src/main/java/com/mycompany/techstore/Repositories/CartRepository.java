/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.CartItem;
import com.mycompany.techstore.resources.DbClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DuyTran
 */
public class CartRepository {

    public int getCartIdByUserId(int userId) {

        String sql
                = "SELECT cart_id FROM bs_Cart WHERE user_id=?";

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("cart_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int createCart(int userId) {

        String sql
                = "INSERT INTO bs_Cart(user_id) VALUES(?)";

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public CartItem getCartItem(
            int cartId,
            int productId) {

        String sql
                = "SELECT * FROM bs_CartItems "
                + "WHERE cart_id=? AND product_id=?";

        try (
                Connection con
                = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, cartId);
            ps.setInt(2, productId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                CartItem item
                        = new CartItem();

                item.setCartItemId(
                        rs.getInt("cart_item_id"));

                item.setQuantity(
                        rs.getInt("quantity"));

                return item;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addCartItem(
            int cartId,
            int productId,
            int quantity,
            double price) {

        String sql
                = "INSERT INTO bs_CartItems"
                + "(cart_id,product_id,quantity,unit_price)"
                + " VALUES(?,?,?,?)";

        try (
                Connection con
                = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateQuantity(
            int cartItemId,
            int quantity) {

        String sql
                = "UPDATE bs_CartItems "
                + "SET quantity=? "
                + "WHERE cart_item_id=? ";

        try (
                Connection con
                = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<CartItem> getCartItems(int cartId) {

        List<CartItem> list = new ArrayList<>();

        String sql
                = "SELECT * FROM bs_CartItems WHERE cart_id = ?";

        try (
                Connection con
                = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setInt(1, cartId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CartItem item = new CartItem();

                item.setCartItemId(
                        rs.getInt("cart_item_id"));

                item.setCartId(
                        rs.getInt("cart_id"));

                item.setProductId(
                        rs.getInt("product_id"));

                item.setQuantity(
                        rs.getInt("quantity"));

                item.setUnitPrice(
                        rs.getDouble("unit_price"));

                list.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
