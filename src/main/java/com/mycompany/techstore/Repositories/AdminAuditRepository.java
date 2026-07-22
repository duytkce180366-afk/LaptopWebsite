package com.mycompany.techstore.Repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminAuditRepository {
  public void log(
      Connection connection,
      int adminId,
      String action,
      String entityType,
      Object entityId,
      String details)
      throws SQLException {
    String sql =
        "INSERT INTO dbo.bs_AdminAuditLogs (admin_id,action,entity_type,entity_id,details) VALUES"
            + " (?,?,?,?,?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, adminId);
      statement.setString(2, action);
      statement.setString(3, entityType);
      statement.setString(4, entityId == null ? null : String.valueOf(entityId));
      statement.setString(
          5, details != null && details.length() > 1000 ? details.substring(0, 1000) : details);
      statement.executeUpdate();
    }
  }
}
