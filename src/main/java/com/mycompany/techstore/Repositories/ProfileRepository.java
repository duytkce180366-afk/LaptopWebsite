package com.mycompany.techstore.Repositories;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.resources.DbClass;

public class ProfileRepository extends DbClass {

    // Update user's profile (full name and phone) by email
    public boolean UpdateProfileByEmail(String email, String fullName, String phone) {
        boolean updated = false;

        String sqlUpdate = """
                            UPDATE [bs_user]
                                SET [full_name] = ?, [phone] = ?, [updated_at] = SYSUTCDATETIME()
                                WHERE [email] = ?;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlUpdate)) {
            ps.setString(1, fullName);

            if (phone == null || phone.isEmpty()) {
                ps.setNull(2, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(2, phone);
            }

            ps.setString(3, email);

            int rows = ps.executeUpdate();
            updated = (rows > 0);
        } catch (SQLException sqlEx) {
            Logger.getLogger(ProfileRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return updated;
    }
}
