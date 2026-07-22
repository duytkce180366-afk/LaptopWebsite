package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.resources.DbClass;
import com.mycompany.techstore.utils.VietnamTime;
import java.sql.*;
import java.util.*;

public class AdminUserRepository {
  private final AdminAuditRepository audit = new AdminAuditRepository();

  public PageResult<AdminUser> findAll(String q, int roleId, String status, int page, int size)
      throws SQLException {
    String sql =
        """
    SELECT u.user_id,u.role_id,r.role_name,u.full_name,u.email,u.phone,u.isverified,u.status,u.created_at,u.updated_at,COUNT(*) OVER() total_rows
FROM dbo.bs_user u JOIN dbo.bs_Roles r ON r.role_id=u.role_id
WHERE (?='' OR u.full_name LIKE ? OR u.email LIKE ? OR u.phone LIKE ?)
  AND (?=0 OR u.role_id=?) AND (?='' OR u.status=?)
ORDER BY u.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY""";
    List<AdminUser> items = new ArrayList<>();
    int total = 0;
    String search = clean(q), like = "%" + search + "%", s = clean(status);
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, search);
      ps.setString(2, like);
      ps.setString(3, like);
      ps.setString(4, like);
      ps.setInt(5, roleId);
      ps.setInt(6, roleId);
      ps.setString(7, s);
      ps.setString(8, s);
      ps.setInt(9, (page - 1) * size);
      ps.setInt(10, size);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          items.add(map(rs));
          total = rs.getInt("total_rows");
        }
      }
    }
    return new PageResult<>(items, page, size, total);
  }

  public List<LookupOption> roles() throws SQLException {
    List<LookupOption> out = new ArrayList<>();
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps =
            con.prepareStatement(
                "SELECT role_id,role_name FROM dbo.bs_Roles WHERE role_name IN"
                    + " ('Admin','User','Staff') ORDER BY CASE role_name WHEN 'Admin' THEN 1"
                    + " WHEN 'Staff' THEN 2 ELSE 3 END");
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) out.add(new LookupOption(rs.getInt(1), rs.getString(2)));
    }
    return out;
  }

  public AdminUser findById(int id) throws SQLException {
    String sql =
        "SELECT"
            + " u.user_id,u.role_id,r.role_name,u.full_name,u.email,u.phone,u.isverified,u.status,u.created_at,u.updated_at"
            + " FROM dbo.bs_user u JOIN dbo.bs_Roles r ON r.role_id=u.role_id WHERE u.user_id=?";
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? map(rs) : null;
      }
    }
  }

  public boolean isManagedRole(int roleId) throws SQLException {
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps =
            con.prepareStatement(
                "SELECT 1 FROM dbo.bs_Roles WHERE role_id=? AND role_name IN"
                    + " ('Admin','User','Staff')")) {
      ps.setInt(1, roleId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public int findRoleId(String roleName) throws SQLException {
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps =
            con.prepareStatement("SELECT role_id FROM dbo.bs_Roles WHERE role_name=?")) {
      ps.setString(1, roleName);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
      }
    }
  }

  public boolean emailExists(String email, int exceptId) throws SQLException {
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps =
            con.prepareStatement(
                "SELECT 1 FROM dbo.bs_user WHERE LOWER(email)=LOWER(?) AND user_id<>?")) {
      ps.setString(1, email);
      ps.setInt(2, exceptId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public void createStaff(String name, String email, String phone, String passwordHash, int adminId)
      throws SQLException {
    int roleId = findRoleId("Staff");
    if (roleId == 0) throw new SQLException("Staff role is missing");
    String sql =
        "INSERT INTO"
            + " dbo.bs_user(role_id,full_name,email,phone,password,isverified,status,created_at,updated_at)"
            + " VALUES(?,?,?,?,?,1,'Active',SYSUTCDATETIME(),SYSUTCDATETIME())";
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, roleId);
        ps.setString(2, name);
        ps.setString(3, email);
        ps.setString(4, phone);
        ps.setString(5, passwordHash);
        ps.executeUpdate();
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (!keys.next()) throw new SQLException("Unable to create staff account");
          int id = keys.getInt(1);
          audit.log(con, adminId, "STAFF_CREATE", "USER", id, email);
        }
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public void updateStaff(int id, String name, String email, String phone, int adminId)
      throws SQLException {
    String sql =
        "UPDATE u SET full_name=?,email=?,phone=?,updated_at=SYSUTCDATETIME() FROM dbo.bs_user u"
            + " JOIN dbo.bs_Roles r ON r.role_id=u.role_id WHERE u.user_id=? AND"
            + " r.role_name='Staff'";
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, phone);
        ps.setInt(4, id);
        if (ps.executeUpdate() != 1) throw new SQLException("Staff account not found");
        audit.log(con, adminId, "STAFF_UPDATE", "USER", id, email);
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public void deactivateStaff(int id, int adminId) throws SQLException {
    String sql =
        "UPDATE u SET status='Inactive',updated_at=SYSUTCDATETIME() FROM dbo.bs_user u JOIN"
            + " dbo.bs_Roles r ON r.role_id=u.role_id WHERE u.user_id=? AND r.role_name='Staff'";
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);
        if (ps.executeUpdate() != 1) throw new SQLException("Staff account not found");
        audit.log(con, adminId, "STAFF_DELETE", "USER", id, "Soft delete: Inactive");
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public void setStatus(int id, String status, int adminId) throws SQLException {
    mutate(
        id,
        adminId,
        "STATUS_CHANGE",
        status,
        "UPDATE dbo.bs_user SET status=?,updated_at=SYSUTCDATETIME() WHERE user_id=?");
  }

  public void setRole(int id, int roleId, int adminId) throws SQLException {
    mutate(
        id,
        adminId,
        "ROLE_CHANGE",
        String.valueOf(roleId),
        "UPDATE dbo.bs_user SET role_id=?,updated_at=SYSUTCDATETIME() WHERE user_id=?");
  }

  public boolean isLastActiveAdmin(int id) throws SQLException {
    String sql =
        """
    SELECT CASE WHEN EXISTS(SELECT 1 FROM dbo.bs_user u JOIN dbo.bs_Roles r ON r.role_id=u.role_id WHERE u.user_id=? AND r.role_name='Admin' AND u.status='Active')
AND (SELECT COUNT(*) FROM dbo.bs_user u JOIN dbo.bs_Roles r ON r.role_id=u.role_id WHERE r.role_name='Admin' AND u.status='Active')<=1 THEN 1 ELSE 0 END""";
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getBoolean(1);
      }
    }
  }

  private void mutate(int id, int adminId, String action, Object value, String sql)
      throws SQLException {
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setObject(1, value);
        ps.setInt(2, id);
        if (ps.executeUpdate() != 1) throw new SQLException("User not found");
        audit.log(con, adminId, action, "USER", id, String.valueOf(value));
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  private AdminUser map(ResultSet rs) throws SQLException {
    AdminUser u = new AdminUser();
    u.setUserId(rs.getInt("user_id"));
    u.setRoleId(rs.getInt("role_id"));
    u.setRoleName(rs.getString("role_name"));
    u.setFullName(rs.getString("full_name"));
    u.setEmail(rs.getString("email"));
    u.setPhone(rs.getString("phone"));
    u.setVerified(rs.getBoolean("isverified"));
    u.setStatus(rs.getString("status"));
    u.setCreatedAt(VietnamTime.fromUtc(rs.getTimestamp("created_at")));
    u.setUpdatedAt(VietnamTime.fromUtc(rs.getTimestamp("updated_at")));
    return u;
  }

  private String clean(String v) {
    return v == null ? "" : v.trim();
  }
}
