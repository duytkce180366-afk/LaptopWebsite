package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.resources.DbClass;
import com.mycompany.techstore.services.VietnamTime;
import java.sql.*;
import java.util.*;

public class AdminProductRepository {
  private final AdminAuditRepository audit = new AdminAuditRepository();

  public PageResult<AdminProduct> findAll(
      String search, int categoryId, int brandId, String status, int page, int size)
      throws SQLException {
    String sql =
        """
        SELECT p.*,c.category_name,b.brand_name,COUNT(*) OVER() total_rows
        FROM dbo.bs_Products p JOIN dbo.bs_Categories c ON c.category_id=p.category_id
        JOIN dbo.bs_Brands b ON b.brand_id=p.brand_id
        WHERE (?='' OR p.product_name LIKE ? OR p.sku LIKE ?)
          AND (?=0 OR p.category_id=?) AND (?=0 OR p.brand_id=?)
          AND (?='' OR LOWER(p.status)=LOWER(?))
        ORDER BY p.product_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;
    List<AdminProduct> items = new ArrayList<>();
    int total = 0;
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      String q = search == null ? "" : search.trim(),
          like = "%" + q + "%",
          s = status == null ? "" : status;
      ps.setString(1, q);
      ps.setString(2, like);
      ps.setString(3, like);
      ps.setInt(4, categoryId);
      ps.setInt(5, categoryId);
      ps.setInt(6, brandId);
      ps.setInt(7, brandId);
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

  public AdminProduct findById(int id) throws SQLException {
    String sql =
        "SELECT p.*,c.category_name,b.brand_name FROM dbo.bs_Products p JOIN dbo.bs_Categories c ON"
            + " c.category_id=p.category_id JOIN dbo.bs_Brands b ON b.brand_id=p.brand_id WHERE"
            + " p.product_id=?";
    AdminProduct product = null;
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) product = map(rs);
      }
      if (product != null) product.setSpecifications(loadSpecs(con, id));
    }
    return product;
  }

  public List<LookupOption> categories() throws SQLException {
    return lookups(
        "SELECT category_id id,category_name name FROM dbo.bs_Categories ORDER BY category_name");
  }

  public List<LookupOption> brands() throws SQLException {
    return lookups("SELECT brand_id id,brand_name name FROM dbo.bs_Brands ORDER BY brand_name");
  }

  public int create(AdminProduct p, int adminId) throws SQLException {
    String sql =
        "INSERT INTO"
            + " dbo.bs_Products(category_id,brand_id,sku,product_name,description,price,stock,thumbnail,status,created_at,updated_at)"
            + " VALUES(?,?,?,?,?,?,?,?,?,SYSUTCDATETIME(),SYSUTCDATETIME())";
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        bindCreate(ps, p);
        ps.executeUpdate();
        try (ResultSet keys = ps.getGeneratedKeys()) {
          if (!keys.next()) throw new SQLException("Product ID was not generated");
          p.setProductId(keys.getInt(1));
        }
        saveSpecs(con, p);
        audit.log(con, adminId, "CREATE", "PRODUCT", p.getProductId(), p.getSku());
        con.commit();
        return p.getProductId();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public void update(AdminProduct p, int adminId) throws SQLException {
    String sql =
        "UPDATE dbo.bs_Products SET"
            + " category_id=?,brand_id=?,sku=?,product_name=?,description=?,price=?,thumbnail=?,status=?,updated_at=SYSUTCDATETIME()"
            + " WHERE product_id=?";
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps = con.prepareStatement(sql)) {
        bindUpdate(ps, p);
        ps.setInt(9, p.getProductId());
        if (ps.executeUpdate() != 1) throw new SQLException("Product not found");
        try (PreparedStatement del =
            con.prepareStatement("DELETE FROM dbo.bs_ProductSpecifications WHERE product_id=?")) {
          del.setInt(1, p.getProductId());
          del.executeUpdate();
        }
        saveSpecs(con, p);
        audit.log(con, adminId, "UPDATE", "PRODUCT", p.getProductId(), p.getSku());
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public void deactivate(int id, int adminId) throws SQLException {
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try (PreparedStatement ps =
          con.prepareStatement(
              "UPDATE dbo.bs_Products SET status='Inactive',updated_at=SYSUTCDATETIME() WHERE"
                  + " product_id=?")) {
        ps.setInt(1, id);
        if (ps.executeUpdate() != 1) throw new SQLException("Product not found");
        audit.log(con, adminId, "DEACTIVATE", "PRODUCT", id, null);
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public boolean skuExists(String sku, int exceptId) throws SQLException {
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps =
            con.prepareStatement("SELECT 1 FROM dbo.bs_Products WHERE sku=? AND product_id<>?")) {
      ps.setString(1, sku);
      ps.setInt(2, exceptId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  public void receiveStock(int productId, int quantity, String note, int adminId)
      throws SQLException {
    try (Connection con = new DbClass().getConnection()) {
      con.setAutoCommit(false);
      try {
        int previous;
        try (PreparedStatement lock =
            con.prepareStatement(
                "SELECT stock FROM dbo.bs_Products WITH (UPDLOCK,ROWLOCK) WHERE product_id=?")) {
          lock.setInt(1, productId);
          try (ResultSet rs = lock.executeQuery()) {
            if (!rs.next()) throw new SQLException("Product not found");
            previous = rs.getInt(1);
          }
        }
        int resulting = previous + quantity;
        try (PreparedStatement update =
            con.prepareStatement(
                "UPDATE dbo.bs_Products SET stock=?,status=CASE WHEN status='Out of Stock' THEN"
                    + " 'Active' ELSE status END,updated_at=SYSUTCDATETIME() WHERE product_id=?")) {
          update.setInt(1, resulting);
          update.setInt(2, productId);
          update.executeUpdate();
        }
        try (PreparedStatement receipt =
            con.prepareStatement(
                "INSERT INTO"
                    + " dbo.bs_StockReceipts(product_id,quantity,previous_stock,resulting_stock,note,admin_id)"
                    + " VALUES(?,?,?,?,?,?)")) {
          receipt.setInt(1, productId);
          receipt.setInt(2, quantity);
          receipt.setInt(3, previous);
          receipt.setInt(4, resulting);
          receipt.setString(5, note);
          receipt.setInt(6, adminId);
          receipt.executeUpdate();
        }
        audit.log(
            con,
            adminId,
            "STOCK_RECEIPT",
            "PRODUCT",
            productId,
            "+"
                + quantity
                + " ("
                + previous
                + " -> "
                + resulting
                + ")"
                + (note.isBlank() ? "" : "; " + note));
        con.commit();
      } catch (Exception ex) {
        con.rollback();
        throw ex;
      }
    }
  }

  public List<StockReceipt> recentReceipts() throws SQLException {
    String sql =
        "SELECT TOP 50"
            + " r.receipt_id,r.product_id,p.sku,p.product_name,r.quantity,r.previous_stock,r.resulting_stock,r.note,u.full_name"
            + " admin_name,r.created_at FROM dbo.bs_StockReceipts r JOIN dbo.bs_Products p ON"
            + " p.product_id=r.product_id JOIN dbo.bs_user u ON u.user_id=r.admin_id ORDER BY"
            + " r.created_at DESC,r.receipt_id DESC";
    List<StockReceipt> out = new ArrayList<>();
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        StockReceipt r = new StockReceipt();
        r.setReceiptId(rs.getLong("receipt_id"));
        r.setProductId(rs.getInt("product_id"));
        r.setSku(rs.getString("sku"));
        r.setProductName(rs.getString("product_name"));
        r.setQuantity(rs.getInt("quantity"));
        r.setPreviousStock(rs.getInt("previous_stock"));
        r.setResultingStock(rs.getInt("resulting_stock"));
        r.setNote(rs.getString("note"));
        r.setAdminName(rs.getString("admin_name"));
        r.setCreatedAt(VietnamTime.fromUtc(rs.getTimestamp("created_at")));
        out.add(r);
      }
    }
    return out;
  }

  private void bindCreate(PreparedStatement ps, AdminProduct p) throws SQLException {
    ps.setInt(1, p.getCategoryId());
    ps.setInt(2, p.getBrandId());
    ps.setString(3, p.getSku());
    ps.setString(4, p.getProductName());
    ps.setString(5, p.getDescription());
    ps.setBigDecimal(6, p.getPrice());
    ps.setInt(7, p.getStock());
    ps.setString(8, p.getThumbnail());
    ps.setString(9, p.getStatus());
  }

  private void bindUpdate(PreparedStatement ps, AdminProduct p) throws SQLException {
    ps.setInt(1, p.getCategoryId());
    ps.setInt(2, p.getBrandId());
    ps.setString(3, p.getSku());
    ps.setString(4, p.getProductName());
    ps.setString(5, p.getDescription());
    ps.setBigDecimal(6, p.getPrice());
    ps.setString(7, p.getThumbnail());
    ps.setString(8, p.getStatus());
  }

  private void saveSpecs(Connection con, AdminProduct p) throws SQLException {
    String sql =
        "INSERT INTO"
            + " dbo.bs_ProductSpecifications(product_id,spec_key,spec_label,spec_value,sort_order,created_at,updated_at)"
            + " VALUES(?,?,?,?,?,SYSUTCDATETIME(),SYSUTCDATETIME())";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
      int order = 0;
      for (Map.Entry<String, String> e : p.getSpecifications().entrySet()) {
        ps.setInt(1, p.getProductId());
        ps.setString(2, e.getKey());
        ps.setString(3, label(e.getKey()));
        ps.setString(4, e.getValue());
        ps.setInt(5, order++);
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }

  private Map<String, String> loadSpecs(Connection con, int id) throws SQLException {
    Map<String, String> out = new LinkedHashMap<>();
    try (PreparedStatement ps =
        con.prepareStatement(
            "SELECT spec_key,spec_value FROM dbo.bs_ProductSpecifications WHERE product_id=? ORDER"
                + " BY sort_order")) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) out.put(rs.getString(1), rs.getString(2));
      }
    }
    return out;
  }

  private List<LookupOption> lookups(String sql) throws SQLException {
    List<LookupOption> out = new ArrayList<>();
    try (Connection con = new DbClass().getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) out.add(new LookupOption(rs.getInt("id"), rs.getString("name")));
    }
    return out;
  }

  private AdminProduct map(ResultSet rs) throws SQLException {
    AdminProduct p = new AdminProduct();
    p.setProductId(rs.getInt("product_id"));
    p.setCategoryId(rs.getInt("category_id"));
    p.setBrandId(rs.getInt("brand_id"));
    p.setCategoryName(rs.getString("category_name"));
    p.setBrandName(rs.getString("brand_name"));
    p.setSku(rs.getString("sku"));
    p.setProductName(rs.getString("product_name"));
    p.setDescription(rs.getString("description"));
    p.setPrice(rs.getBigDecimal("price"));
    p.setStock(rs.getInt("stock"));
    p.setThumbnail(rs.getString("thumbnail"));
    p.setStatus(canonicalStatus(rs.getString("status")));
    p.setCreatedAt(VietnamTime.fromUtc(rs.getTimestamp("created_at")));
    p.setUpdatedAt(VietnamTime.fromUtc(rs.getTimestamp("updated_at")));
    return p;
  }

  private String canonicalStatus(String value) {
    if (value == null) return "";
    return switch (value.trim().toLowerCase(Locale.ROOT)) {
      case "active" -> "Active";
      case "out of stock" -> "Out of Stock";
      case "hidden" -> "Hidden";
      case "inactive" -> "Inactive";
      default -> value.trim();
    };
  }

  private String label(String key) {
    if (key == null || key.isBlank()) return "Specification";
    return Character.toUpperCase(key.charAt(0)) + key.substring(1);
  }
}
