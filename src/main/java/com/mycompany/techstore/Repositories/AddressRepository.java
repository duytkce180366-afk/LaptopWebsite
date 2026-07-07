package com.mycompany.techstore.Repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.Models.Objects.Address;
import com.mycompany.techstore.resources.DbClass;

public class AddressRepository extends DbClass {

    public ArrayList<Address> GetAddressesByUserId(int userId) {
        ArrayList<Address> list = new ArrayList<>();

        String sql = """
            SELECT [address_id], [user_id], [home_address], [phone], [province], [ward], [is_default], [created_at]
                FROM dbo.bs_Addresses
                WHERE user_id = ?
                ORDER BY is_default DESC, address_id;
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Address address = new Address(
                            rs.getInt("address_id"),
                            rs.getInt("user_id"),
                            rs.getString("home_address"),
                            rs.getString("phone"),
                            rs.getString("province"),
                            rs.getString("ward"),
                            rs.getBoolean("is_default"),
                            rs.getTimestamp("created_at"));
                    list.add(address);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public boolean CreateAddress(int userId, String homeAddress, String phone, String province, String ward, boolean isDefault) {
        boolean status = false;

        String sql = """
            INSERT INTO dbo.bs_Addresses ([user_id], [home_address], [phone], [province], [ward], [is_default], [created_at])
                VALUES (?, ?, ?, ?, ?, ?, SYSUTCDATETIME());
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, homeAddress);
            ps.setString(3, phone);
            ps.setString(4, province);
            ps.setString(5, ward);
            ps.setBoolean(6, isDefault);
            status = (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return status;
    }

    public boolean UpdateAddress(int userId, int addressId, String homeAddress, String phone, String province, String ward, boolean isDefault) {
        boolean status = false;

        String sql = """
            UPDATE dbo.bs_Addresses
                SET home_address = ?, phone = ?, province = ?, ward = ?, is_default = ?
                WHERE address_id = ? AND user_id = ?;
            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setString(1, homeAddress);
            ps.setString(2, phone);
            ps.setString(3, province);
            ps.setString(4, ward);
            ps.setBoolean(5, isDefault);

            ps.setInt(6, addressId);
            ps.setInt(7, userId);
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
