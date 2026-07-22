package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.resources.DbClass;
import com.mycompany.techstore.utils.VietnamTime;
import java.sql.*;
import java.util.*;

public class AdminReviewRepository {
  private final AdminAuditRepository audit = new AdminAuditRepository();

  public PageResult<AdminReview> findAll(String q, int rating, String status, int page, int size)
      throws SQLException {
    String sql =
        """
    SELECT r.*,u.full_name,u.email,p.product_name,COUNT(*) OVER() total_rows
FROM dbo.bs_Reviews r JOIN dbo.bs_user u ON u.user_id=r.user_id JOIN dbo.bs_Products p ON p.product_id=r.product_id
WHERE (?='' OR u.full_name LIKE ? OR u.email LIKE ? OR p.product_name LIKE ? OR r.comment LIKE ?)
  AND (?=0 OR r.rating=?) AND (?='' OR r.status=?)
ORDER BY r.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY""";
    List<AdminReview> items = new ArrayList<>();
    int total = 0;
    String search = clean(q), like = "%" + search + "%", s = clean(status);
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, search);
      ps.setString(2, like);
      ps.setString(3, like);
      ps.setString(4, like);
      ps.setString(5, like);
      ps.setInt(6, rating);
      ps.setInt(7, rating);
      ps.setString(8, s);
      ps.setString(9, s);
      ps.setInt(10, (page - 1) * size);
      ps.setInt(11, size);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          items.add(map(rs));
          total = rs.getInt("total_rows");
        }
      }
    }
    return new PageResult<>(items, page, size, total);
  }

  public void setStatus(int id, String status, int adminId) throws SQLException {
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps =
          con.prepareStatement(
              "UPDATE dbo.bs_Reviews SET"
                  + " status=?,moderated_by=?,moderated_at=SYSUTCDATETIME(),updated_at=SYSUTCDATETIME()"
                  + " WHERE review_id=?")) {
        ps.setString(1, status);
        ps.setInt(2, adminId);
        ps.setInt(3, id);
        if (ps.executeUpdate() != 1) throw new SQLException("Review not found");
        audit.log(con, adminId, "MODERATE", "REVIEW", id, status);
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  private AdminReview map(ResultSet rs) throws SQLException {
    AdminReview r = new AdminReview();
    r.setReviewId(rs.getInt("review_id"));
    r.setUserId(rs.getInt("user_id"));
    r.setProductId(rs.getInt("product_id"));
    r.setRating(rs.getInt("rating"));
    r.setUserName(rs.getString("full_name"));
    r.setUserEmail(rs.getString("email"));
    r.setProductName(rs.getString("product_name"));
    r.setComment(rs.getString("comment"));
    r.setStatus(rs.getString("status"));
    r.setCreatedAt(VietnamTime.fromUtc(rs.getTimestamp("created_at")));
    r.setModeratedAt(VietnamTime.fromUtc(rs.getTimestamp("moderated_at")));
    return r;
  }

  private String clean(String v) {
    return v == null ? "" : v.trim();
  }
}
