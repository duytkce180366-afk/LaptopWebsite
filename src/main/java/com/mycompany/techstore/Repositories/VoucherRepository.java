package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.resources.DbClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public boolean decreaseQuantity(int voucherId) {
        String sql
                = "UPDATE bs_Vouchers "
                + "SET quantity = quantity - 1 "
                + "WHERE voucher_id = ? AND quantity > 0";
        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps
                = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Voucher> getAll() {

        List<Voucher> list = new ArrayList<>();

        String sql
                = "SELECT * "
                + "FROM bs_Vouchers "
                + "ORDER BY voucher_id DESC";

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Voucher v = new Voucher();

                v.setVoucherId(rs.getInt("voucher_id"));

                v.setCode(rs.getString("code"));

                v.setDiscountPercent(
                        rs.getDouble("discount_percent"));

                v.setQuantity(
                        rs.getInt("quantity"));

                v.setExpiredDate(
                        rs.getDate("expired_date"));

                v.setStatus(
                        rs.getString("status"));
                v.setCreatedAt(
                        rs.getTimestamp("created_at"));

                v.setUpdatedAt(
                        rs.getTimestamp("updated_at"));
                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean createVoucher(Voucher voucher) {

        String sql = """
        INSERT INTO bs_Vouchers
        (
            code,
            discount_percent,
            quantity,
            expired_date,
            status,
            created_at,
            updated_at
        )
        VALUES
        (
            ?, ?, ?, ?, ?,
            GETDATE(),
            GETDATE()
        )
        """;

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, voucher.getCode());
            ps.setDouble(2, voucher.getDiscountPercent());
            ps.setInt(3, voucher.getQuantity());
            ps.setDate(4, new java.sql.Date(voucher.getExpiredDate().getTime()));
            ps.setString(5, voucher.getStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Voucher getById(int voucherId) {
        String sql = "SELECT * FROM bs_Vouchers WHERE voucher_id = ?";
        try (Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher v = new Voucher();
                v.setVoucherId(rs.getInt("voucher_id"));
                v.setCode(rs.getString("code"));
                v.setDiscountPercent(rs.getDouble("discount_percent"));
                v.setQuantity(rs.getInt("quantity"));
                v.setExpiredDate(rs.getDate("expired_date"));
                v.setStatus(rs.getString("status"));
                v.setCreatedAt(rs.getTimestamp("created_at"));
                v.setUpdatedAt(rs.getTimestamp("updated_at"));

                return v;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateVoucher(Voucher voucher) {

        String sql
                = "UPDATE bs_Vouchers "
                + "SET code=?, "
                + "discount_percent=?, "
                + "quantity=?, "
                + "expired_date=?, "
                + "status=?, "
                + "updated_at=GETDATE() "
                + "WHERE voucher_id=?";

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, voucher.getCode());
            ps.setDouble(2, voucher.getDiscountPercent());
            ps.setInt(3, voucher.getQuantity());
            ps.setDate(4,
                    new java.sql.Date(voucher.getExpiredDate().getTime()));
            ps.setString(5, voucher.getStatus());
            ps.setInt(6, voucher.getVoucherId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteVoucher(int voucherId) {

        String sql
                = "UPDATE bs_Vouchers "
                + "SET status='Inactive', "
                + "updated_at=GETDATE() "
                + "WHERE voucher_id=?";

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voucherId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Voucher> filterVoucher(
            String keyword,
            String status,
            String discountPercent,
            String expiredDate) {

        List<Voucher> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM bs_Vouchers WHERE 1=1 ");

        if (!keyword.isEmpty()) {
            sql.append("AND code LIKE ? ");
        }

        if (!status.isEmpty()) {
            sql.append("AND status=? ");
        }

        if (!discountPercent.isEmpty()) {
            sql.append("AND discount_percent=? ");
        }

        if (!expiredDate.isEmpty()) {
            sql.append("AND expired_date=? ");
        }

        sql.append("ORDER BY voucher_id DESC");

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (!keyword.isEmpty()) {
                ps.setString(index++, "%" + keyword + "%");
            }

            if (!status.isEmpty()) {
                ps.setString(index++, status);
            }

            if (!discountPercent.isEmpty()) {
                ps.setDouble(index++, Double.parseDouble(discountPercent));
            }

            if (!expiredDate.isEmpty()) {
                ps.setDate(index++, java.sql.Date.valueOf(expiredDate));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Voucher v = new Voucher();

                v.setVoucherId(rs.getInt("voucher_id"));
                v.setCode(rs.getString("code"));
                v.setDiscountPercent(rs.getDouble("discount_percent"));
                v.setQuantity(rs.getInt("quantity"));
                v.setExpiredDate(rs.getDate("expired_date"));
                v.setStatus(rs.getString("status"));
                v.setCreatedAt(rs.getTimestamp("created_at"));
                v.setUpdatedAt(rs.getTimestamp("updated_at"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }

    public void updateExpiredVoucher() {

        String sql = """
        UPDATE bs_Vouchers
        SET status='Expired'
        WHERE expired_date < CAST(GETDATE() AS DATE)
        AND status='Active'
        """;

        try (
                Connection con = new DbClass().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
