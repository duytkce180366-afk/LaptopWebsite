package com.mycompany.techstore.Repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.resources.DbClass;

public class AuthRepository extends DbClass {

    // OIDC
    public User GetUserOIDCSignIn(String email) {
        User user = null;

        String sqlCommand = """
                            SELECT TOP(1) [user_id], [role_id], [full_name], [email], [phone], [password], [avatar], [status], [created_at], [updated_at]
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
                            rs.getString("avatar"),
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

    public boolean IsEmailExists(String email) {
        boolean exists = false;

        String sqlCommand = """
                            SELECT 1 FROM [bs_user] WHERE [email] = ?;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return exists;
    }

    public User CreateUser(String email, String pwdHash, String fullName) {
        User user = null;

        String sqlInsert = """
                           INSERT INTO [bs_user] ([role_id], [full_name], [email], [phone], [password], [avatar], [status], [created_at], [updated_at])
                           VALUES (?, ?, ?, NULL, ?, NULL, 'Active', SYSUTCDATETIME(), NULL);
                           """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlInsert)) {
            ps.setInt(1, 2); // default regular role
            ps.setString(2, fullName);
            ps.setString(3, email);
            if (pwdHash != null) {
                ps.setString(4, pwdHash);
            } else {
                ps.setNull(4, java.sql.Types.NVARCHAR);
            }

            ps.executeUpdate();

            // fetch created user
            user = GetUserOIDCSignIn(email);
        } catch (SQLException sqlEx) {
            Logger.getLogger(AuthRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return user;
    }

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
        }

        return updated;
    }

    // Regular User sign in
    public User GetUserSignIn(String email, String pwdHash) {
        User user = null;

        String sqlCommand = """
                            SELECT TOP(1) [user_id], [role_id], [full_name], [email], [phone], [password], [avatar], [status], [created_at], [updated_at]
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
                            rs.getString("avatar"),
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

    
}
