package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.resources.DbClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public String findRoleName(int userId)throws SQLException{
        String sql="SELECT r.role_name FROM dbo.bs_user u JOIN dbo.bs_Roles r ON r.role_id=u.role_id WHERE u.user_id=?";
        DbClass database=new DbClass();Connection connection=database.getConnection();if(connection==null)throw new SQLException("Database connection is unavailable.");
        try(connection;PreparedStatement statement=connection.prepareStatement(sql)){statement.setInt(1,userId);try(ResultSet result=statement.executeQuery()){return result.next()?result.getString(1):null;}}
    }

    public User findById(int userId) throws SQLException {
        String sql = """
                SELECT user_id, role_id, full_name, email, phone, password,
                       isverified, status, created_at, updated_at
                FROM dbo.bs_user
                WHERE user_id = ?
                """;

        DbClass database = new DbClass();
        Connection connection = database.getConnection();
        if (connection == null) {
            throw new SQLException("Database connection is unavailable.");
        }

        try (connection; PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return new User(
                        result.getInt("user_id"),
                        result.getInt("role_id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        result.getString("phone"),
                        result.getString("password"),
                        result.getBoolean("isverified"),
                        result.getString("status"),
                        result.getTimestamp("created_at"),
                        result.getTimestamp("updated_at")
                );
            }
        }
    }
}
