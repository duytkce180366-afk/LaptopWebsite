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
                     SELECT address_id, user_id, line1, line2, city, state, postal_code, country, is_default, created_at, updated_at
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
                            rs.getTimestamp("updated_at")
                    );
                    list.add(a);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public boolean CreateAddress(int userId, String line1, String line2, String city, String state, String postal, String country, boolean isDefault) {
        boolean ok = false;

        String sql = """
                     INSERT INTO dbo.bs_Addresses (user_id, line1, line2, city, state, postal_code, country, is_default, created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME(), SYSUTCDATETIME());
                     """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, line1);
            if (line2 == null || line2.isEmpty()) ps.setNull(3, java.sql.Types.NVARCHAR); else ps.setString(3, line2);
            ps.setString(4, city);
            if (state == null || state.isEmpty()) ps.setNull(5, java.sql.Types.NVARCHAR); else ps.setString(5, state);
            if (postal == null || postal.isEmpty()) ps.setNull(6, java.sql.Types.NVARCHAR); else ps.setString(6, postal);
            if (country == null || country.isEmpty()) ps.setNull(7, java.sql.Types.NVARCHAR); else ps.setString(7, country);
            ps.setBoolean(8, isDefault);

            int rows = ps.executeUpdate();
            ok = (rows > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ok;
    }

    public boolean UpdateAddress(int addressId, int userId, String line1, String line2, String city, String state, String postal, String country, boolean isDefault) {
        boolean ok = false;

        String sql = """
                     UPDATE dbo.bs_Addresses
                        SET line1 = ?, line2 = ?, city = ?, state = ?, postal_code = ?, country = ?, is_default = ?, updated_at = SYSUTCDATETIME()
                        WHERE address_id = ? AND user_id = ?;
                     """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setString(1, line1);
            if (line2 == null || line2.isEmpty()) ps.setNull(2, java.sql.Types.NVARCHAR); else ps.setString(2, line2);
            ps.setString(3, city);
            if (state == null || state.isEmpty()) ps.setNull(4, java.sql.Types.NVARCHAR); else ps.setString(4, state);
            if (postal == null || postal.isEmpty()) ps.setNull(5, java.sql.Types.NVARCHAR); else ps.setString(5, postal);
            if (country == null || country.isEmpty()) ps.setNull(6, java.sql.Types.NVARCHAR); else ps.setString(6, country);
            ps.setBoolean(7, isDefault);
            ps.setInt(8, addressId);
            ps.setInt(9, userId);

            int rows = ps.executeUpdate();
            ok = (rows > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ok;
    }

    public boolean DeleteAddress(int addressId, int userId) {
        boolean ok = false;

        String sql = """
                     DELETE FROM dbo.bs_Addresses
                        WHERE address_id = ? AND user_id = ?;
                     """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            ok = (rows > 0);
        } catch (SQLException ex) {
            Logger.getLogger(AddressRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ok;
    }
}
