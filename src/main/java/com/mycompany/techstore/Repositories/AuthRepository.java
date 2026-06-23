package com.mycompany.techstore.Repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.resources.DbClass;

public class AuthRepository extends DbClass {

    // Check if user exists
    public boolean IsEmailExists(String email) {
        boolean exists = false;

        String sqlCommand = """
                            SELECT 1
                                FROM [bs_user]
                                WHERE [email] = ?;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                exists = rs.next();
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return exists;
    }

    // Sign-in with OIDC (Email only)
    public User GetUserOIDCSignIn(String email) {
        User user = null;

        String sqlCommand = """
                            SELECT TOP(1) [user_id], [role_id], [full_name], [email], [phone], [password], [isverified], [status], [created_at], [updated_at]
                                FROM [bs_user]
                                WHERE [email] = ?;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getInt("role_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("password"),
                            rs.getBoolean("isverified"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return user;
    }

    // Regular Sign-in (email and password)
    public User GetUserSignIn(String email, String pwdHash) {
        User user = null;

        String sqlCommand = """
                            SELECT TOP(1) [user_id], [role_id], [full_name], [email], [phone], [password], [isverified], [status], [created_at], [updated_at]
                                FROM [bs_user]
                                WHERE [email] = ? AND password = ?;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            ps.setString(2, pwdHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getInt("role_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("password"),
                            rs.getBoolean("isverified"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return user;
    }

    // Create new user (OIDC/Password)
    public User CreateUser(String email, String pwdHash, String fullName) {
        User user = null;

        String sqlInsert = """
                           INSERT INTO [bs_user] ([role_id], [full_name], [email], [phone], [password], [isverified], [status], [created_at], [updated_at])
                                VALUES (?, ?, ?, NULL, ?, ?, 'Active', SYSUTCDATETIME(), SYSUTCDATETIME());
                           """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlInsert)) {
            ps.setInt(1, 2);
            ps.setString(2, fullName);
            ps.setString(3, email);

            if (pwdHash != null) {
                // Non-OIDC (Not trusted)
                ps.setString(4, pwdHash);
                // New users registered with email/password must verify their email via OTP
                ps.setBoolean(5, false);
            } else {
                // OIDC
                ps.setNull(4, java.sql.Types.NVARCHAR);
                ps.setBoolean(5, true);
            }

            ps.executeUpdate();
            user = GetUserOIDCSignIn(email);
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return user;
    }
    
    public boolean VerifiedUser(String email) {
        boolean updated = false;

        String sqlUpdate = """
                           UPDATE [bs_user]
                                SET [isverified] = ?, [updated_at] = SYSUTCDATETIME()
                                WHERE [email] = ?;
                           """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlUpdate)) {
            ps.setBoolean(1, true);
            ps.setString(2, email);
            
            int rows = ps.executeUpdate();
            updated = (rows > 0);
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
            System.out.println(sqlEx.toString());
        }

        return updated;
    }

    // Update password for existing user
    public boolean UpdatePassword(String email, String pwdHash) {
        boolean updated = false;

        String sqlUpdate = """
                           UPDATE [bs_user]
                                SET [password] = ?, [updated_at] = SYSUTCDATETIME()
                                WHERE [email] = ?;
                           """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlUpdate)) {
            ps.setString(1, pwdHash);
            ps.setString(2, email);

            int rows = ps.executeUpdate();
            updated = (rows > 0);
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
            System.out.println(sqlEx.toString());
        }

        return updated;
    }
}
