/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.resources.DbClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author DuyTran
 */
public class VoucherRepository {

    public Voucher getByCode(String code) {

        String sql
                = "SELECT * "
                + "FROM bs_Vouchers "
                + "WHERE code = ?";

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {

            ps.setString(1, code);

            ResultSet rs
                    = ps.executeQuery();

            if (rs.next()) {

                Voucher v
                        = new Voucher();

                v.setVoucherId(
                        rs.getInt("voucher_id"));

                v.setCode(
                        rs.getString("code"));

                v.setDiscountPercent(
                        rs.getDouble(
                                "discount_percent"));

                v.setQuantity(
                        rs.getInt("quantity"));

                v.setExpiredDate(
                        rs.getDate(
                                "expired_date"));

                v.setStatus(
                        rs.getString(
                                "status"));

                return v;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}