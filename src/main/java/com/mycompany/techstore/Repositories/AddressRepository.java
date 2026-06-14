package com.mycompany.techstore.Repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.Models.Objects.Address;
import com.mycompany.techstore.resources.DbClass;

public class AddressRepository extends DbClass {

    public List<Address> GetAddressesByUserId(int userId) {
        List<Address> list = new ArrayList<>();

        String sql = """
            SELECT address_id,
                   user_id,
                   receiver_name AS line1,
                   detail_address AS line2,
                   province AS city,
                   district AS state,
                   ward AS postal_code,
                   phone AS country,
                   is_default,
                   created_at,
                   NULL AS updated_at
              FROM dbo.bs_Addresses
             WHERE user_id = ?
             ORDER BY is_default DESC, address_id;
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Address a = new Address(
                            rs.getInt("address_id"),
                            rs.getInt("user_id"),
                            rs.getString("line1"),
                            rs.getString("line2"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("postal_code"),
                            rs.getString("country"),
                            rs.getBoolean("is_default"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at"));
                    list.add(a);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public boolean CreateAddress(int userId, String line1, String line2, String city, String state, String postal, String country, boolean isDefault) {
        boolean status = false;

        String sql = """
            INSERT INTO dbo.bs_Addresses (user_id, receiver_name, phone, province, district, ward, detail_address, is_default, created_at)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME());
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            // Mapping application fields to DB columns:
            // line1 -> receiver_name
            // line2 -> detail_address
            // city  -> province
            // state -> district
            // postal_code -> ward
            // country -> phone
            ps.setString(2, line1);
            ps.setString(3, country);
            ps.setString(4, city);
            ps.setString(5, state);
            ps.setString(6, postal);
            ps.setString(7, line2);
            ps.setBoolean(8, isDefault);
            status = (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }

    public boolean UpdateAddress(int addressId, int userId, String line1, String line2, String city, String state,
            String postal, String country, boolean isDefault) {
        boolean status = false;

        String sql = """
            UPDATE dbo.bs_Addresses
               SET receiver_name = ?,
                   phone = ?,
                   province = ?,
                   district = ?,
                   ward = ?,
                   detail_address = ?,
                   is_default = ?
             WHERE address_id = ? AND user_id = ?;
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            // Apply same mapping as insert
            ps.setString(1, line1);
            ps.setString(2, country);
            ps.setString(3, city);
            ps.setString(4, state);
            ps.setString(5, postal);
            ps.setString(6, line2);
            ps.setBoolean(7, isDefault);
            ps.setInt(8, addressId);
            ps.setInt(9, userId);
            status = (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }

    public boolean DeleteAddress(int addressId, int userId) {
        boolean status = false;

        String sql = """
                DELETE FROM dbo.bs_Addresses
                   WHERE address_id = ? AND user_id = ?;
                """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            status = (rows > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }
}
