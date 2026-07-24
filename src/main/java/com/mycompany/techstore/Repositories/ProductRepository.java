package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Models.Objects.Review;
import com.mycompany.techstore.resources.DbClass;
import com.mycompany.techstore.services.VietnamTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductRepository extends DbClass {

  public List<Product> getAll() {
    return hydrateProducts(queryProducts(null));
  }

  public Product getById(int id) {
    List<ProductRow> rows = queryProducts(id);
    List<Product> products = hydrateProducts(rows);
    return products.isEmpty() ? null : products.get(0);
  }

  public List<Product> getByCategory(String categoryId) {
    return getAll().stream()
        .filter(product -> product.getCategory_id().equals(categoryId))
        .collect(Collectors.toList());
  }

  public List<Product> search(
      String query,
      String categoryId,
      long minPrice,
      long maxPrice,
      Map<String, String> filters,
      String sortOrder) {
    String normalizedQuery = query == null ? "" : query.toLowerCase();
    List<Product> results =
        getAll().stream()
            .filter(
                product ->
                    normalizedQuery.isEmpty()
                        || getSearchableText(product).contains(normalizedQuery))
            .filter(
                product ->
                    categoryId == null
                        || categoryId.isEmpty()
                        || categoryId.equals("all")
                        || product.getCategory_id().equals(categoryId))
            .filter(product -> product.getPrice() >= minPrice && product.getPrice() < maxPrice)
            .filter(product -> matchesFilters(product, filters))
            .collect(Collectors.toList());

    sortResults(results, sortOrder);
    return results;
  }

  private List<ProductRow> queryProducts(Integer productId) {
    List<ProductRow> products = new ArrayList<>();
    String sqlCommand =
        """
        SELECT p.product_id, p.product_name, p.description, p.price, p.stock, p.thumbnail,
               c.category_name, b.brand_name
        FROM dbo.bs_Products p
        INNER JOIN dbo.bs_Categories c ON c.category_id = p.category_id
        INNER JOIN dbo.bs_Brands b ON b.brand_id = p.brand_id
        WHERE (? IS NULL OR p.product_id = ?)
          AND (p.status IS NULL OR LOWER(p.status) IN ('active', 'out of stock'))
        ORDER BY p.product_id;
        """;

    try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
      if (productId == null) {
        ps.setNull(1, java.sql.Types.INTEGER);
        ps.setNull(2, java.sql.Types.INTEGER);
      } else {
        ps.setInt(1, productId);
        ps.setInt(2, productId);
      }

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          products.add(
              new ProductRow(
                  rs.getInt("product_id"),
                  toSlug(rs.getString("category_name")),
                  rs.getString("category_name"),
                  rs.getString("product_name"),
                  rs.getString("brand_name"),
                  rs.getLong("price"),
                  rs.getInt("stock"),
                  rs.getString("thumbnail"),
                  rs.getString("description")));
        }
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return products;
  }

  private List<Product> hydrateProducts(List<ProductRow> productRows) {
    if (productRows.isEmpty()) {
      return new ArrayList<>();
    }

    List<Integer> productIds = productRows.stream().map(row -> row.id).collect(Collectors.toList());
    Map<Integer, Map<String, String>> specsByProduct = getSpecsByProduct(productIds);
    List<Product> products = new ArrayList<>();

    for (ProductRow row : productRows) {
      Map<String, String> rawSpecs = specsByProduct.getOrDefault(row.id, new LinkedHashMap<>());
      String badge = rawSpecs.getOrDefault("badge", "Recommended");
      String warranty = rawSpecs.getOrDefault("warranty", "12 months official warranty");
      Map<String, String> visibleSpecs = new LinkedHashMap<>(rawSpecs);
      visibleSpecs.remove("badge");
      visibleSpecs.remove("warranty");

      products.add(
          new Product(
              row.id,
              row.categorySlug,
              row.categoryName,
              row.name,
              row.brand,
              row.price,
              badge,
              visibleSpecs,
              row.stock,
              row.thumbnail == null ? "" : row.thumbnail,
              warranty,
              row.description == null ? "" : row.description));
    }

    return products;
  }

  private Map<Integer, Map<String, String>> getSpecsByProduct(List<Integer> productIds) {
    Map<Integer, Map<String, String>> specsByProduct = new HashMap<>();
    String sqlCommand =
        """
        SELECT product_id, spec_key, spec_value
        FROM dbo.bs_ProductSpecifications
        ORDER BY product_id, sort_order;
        """;

    try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        int productId = rs.getInt("product_id");
        if (!productIds.contains(productId)) {
          continue;
        }
        specsByProduct
            .computeIfAbsent(productId, key -> new LinkedHashMap<>())
            .put(rs.getString("spec_key"), rs.getString("spec_value"));
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return specsByProduct;
  }

  public List<Review> getReviewsByProductId(int productId) {
    List<Review> reviews = new ArrayList<>();
    String sqlCommand =
        """
        SELECT r.review_id, r.user_id, r.order_id, r.product_id, r.rating, r.comment, r.created_at,
               u.full_name AS user_name
        FROM dbo.bs_Reviews r
        LEFT JOIN dbo.bs_user u ON u.user_id = r.user_id
        WHERE r.product_id = ?
          AND (r.status IS NULL OR r.status = 'Visible')
        ORDER BY r.created_at DESC, r.review_id DESC;
        """;

    try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
      ps.setInt(1, productId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String reviewDate =
              VietnamTime.formatUtcOrVietnamLocal(rs.getTimestamp("created_at"));

          reviews.add(
              new Review(
                  rs.getInt("review_id"),
                  rs.getInt("user_id"),
                  rs.getInt("order_id"),
                  rs.getInt("product_id"),
                  rs.getInt("rating"),
                  rs.getString("comment"),
                  reviewDate,
                  rs.getString("user_name")));
        }
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return reviews;
  }

  public Review getReviewByOrderAndProduct(int orderId, int productId, int userId) {
    String sqlCommand =
        """
SELECT TOP 1 r.review_id, r.user_id, r.order_id, r.product_id, r.rating, r.comment, r.created_at,
       u.full_name AS user_name
FROM dbo.bs_Reviews r
LEFT JOIN dbo.bs_user u ON u.user_id = r.user_id
WHERE r.order_id = ? AND r.product_id = ? AND r.user_id = ?
ORDER BY r.created_at DESC, r.review_id DESC;
""";

    try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
      ps.setInt(1, orderId);
      ps.setInt(2, productId);
      ps.setInt(3, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String reviewDate =
              VietnamTime.formatUtcOrVietnamLocal(rs.getTimestamp("created_at"));

          return new Review(
              rs.getInt("review_id"),
              rs.getInt("user_id"),
              rs.getInt("order_id"),
              rs.getInt("product_id"),
              rs.getInt("rating"),
              rs.getString("comment"),
              reviewDate,
              rs.getString("user_name"));
        }
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return null;
  }

  public List<Integer> getReviewedProductIdsByOrder(int orderId, int userId) {
    List<Integer> reviewedProductIds = new ArrayList<>();
    String sqlCommand =
        """
        SELECT r.product_id
        FROM dbo.bs_Reviews r
        WHERE r.order_id = ? AND r.user_id = ?;
        """;

    try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
      ps.setInt(1, orderId);
      ps.setInt(2, userId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          reviewedProductIds.add(rs.getInt("product_id"));
        }
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return reviewedProductIds;
  }

  public boolean saveReview(int userId, int orderId, int productId, int rating, String comment) {
    String existingSql =
        """
        SELECT review_id
        FROM dbo.bs_Reviews
        WHERE user_id = ? AND order_id = ? AND product_id = ?;
        """;
    String updateSql =
        """
        UPDATE dbo.bs_Reviews
        SET rating = ?, comment = ?, updated_at = SYSUTCDATETIME()
        WHERE review_id = ?;
        """;
    String insertSql =
        """
INSERT INTO dbo.bs_Reviews (user_id, order_id, product_id, rating, comment, created_at, updated_at)
VALUES (?, ?, ?, ?, ?, SYSUTCDATETIME(), SYSUTCDATETIME());
""";

    try (PreparedStatement existingPs = super.getConnection().prepareStatement(existingSql)) {
      existingPs.setInt(1, userId);
      existingPs.setInt(2, orderId);
      existingPs.setInt(3, productId);

      Integer reviewId = null;
      try (ResultSet rs = existingPs.executeQuery()) {
        if (rs.next()) {
          reviewId = rs.getInt("review_id");
        }
      }

      if (reviewId != null) {
        try (PreparedStatement updatePs = super.getConnection().prepareStatement(updateSql)) {
          updatePs.setInt(1, rating);
          updatePs.setString(2, comment);
          updatePs.setInt(3, reviewId);
          return updatePs.executeUpdate() > 0;
        }
      }

      try (PreparedStatement insertPs = super.getConnection().prepareStatement(insertSql)) {
        insertPs.setInt(1, userId);
        insertPs.setInt(2, orderId);
        insertPs.setInt(3, productId);
        insertPs.setInt(4, rating);
        insertPs.setString(5, comment);
        return insertPs.executeUpdate() > 0;
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return false;
  }

  public boolean canReviewOrderProduct(int orderId, int productId, int userId) {
    String sqlCommand =
        """
SELECT 1
FROM dbo.bs_Orders o
INNER JOIN dbo.bs_OrderDetails od ON od.order_id = o.order_id
WHERE o.order_id = ? AND o.user_id = ? AND o.order_status = 'Delivered' AND od.product_id = ?;
""";

    try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand)) {
      ps.setInt(1, orderId);
      ps.setInt(2, userId);
      ps.setInt(3, productId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException sqlEx) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
    }

    return false;
  }

  private String toSlug(String value) {
    if (value == null) {
      return "";
    }
    return value.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
  }

  private String getSearchableText(Product product) {
    List<String> values = new ArrayList<>();
    values.add(product.getName());
    values.add(product.getBrand());
    values.add(product.getCategory());
    values.add(product.getBadge());
    values.addAll(product.getSpecs().values());
    return String.join(" ", values).toLowerCase();
  }

  private boolean matchesFilters(Product product, Map<String, String> filters) {
    if (filters == null || filters.isEmpty()) {
      return true;
    }

    for (Map.Entry<String, String> filter : filters.entrySet()) {
      if ("all".equals(filter.getValue())) {
        continue;
      }

      String specValue =
          "brand".equals(filter.getKey())
              ? product.getBrand()
              : product.getSpecs().get(filter.getKey());
      if (specValue == null || !specValue.equals(filter.getValue())) {
        return false;
      }
    }
    return true;
  }

  private void sortResults(List<Product> results, String sortOrder) {
    if (sortOrder == null || sortOrder.isEmpty() || sortOrder.equals("recommended")) {
      return;
    }

    if (sortOrder.equals("price-asc")) {
      results.sort(Comparator.comparingLong(Product::getPrice));
    } else if (sortOrder.equals("price-desc")) {
      results.sort((p1, p2) -> Long.compare(p2.getPrice(), p1.getPrice()));
    }
  }

  public boolean CreateProduct(
      int categoryId,
      int brandId,
      String sku,
      String productName,
      String description,
      long price,
      int stock,
      String thumbnail,
      String status) {
    String sql =
        """
        INSERT INTO bs_Products
            (category_id, brand_id, sku, product_name, description, price, stock,
             thumbnail, status, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSUTCDATETIME(), SYSUTCDATETIME())
        """;
    try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
      ps.setInt(1, categoryId);
      ps.setInt(2, brandId);
      ps.setString(3, sku);
      ps.setString(4, productName);
      ps.setString(5, description);
      ps.setLong(6, price);
      ps.setInt(7, stock);
      ps.setString(8, thumbnail);
      ps.setString(9, status);
      return ps.executeUpdate() > 0;
    } catch (SQLException ex) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }

  public boolean UpdateProduct(
      int productId,
      int categoryId,
      int brandId,
      String sku,
      String productName,
      String description,
      long price,
      int stock,
      String thumbnail,
      String status) {
    String sql =
        """
        UPDATE bs_Products
        SET category_id=?, brand_id=?, sku=?, product_name=?, description=?, price=?,
            stock=?, thumbnail=?, status=?, updated_at=SYSUTCDATETIME()
        WHERE product_id=?
        """;
    try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
      ps.setInt(1, categoryId);
      ps.setInt(2, brandId);
      ps.setString(3, sku);
      ps.setString(4, productName);
      ps.setString(5, description);
      ps.setLong(6, price);
      ps.setInt(7, stock);
      ps.setString(8, thumbnail);
      ps.setString(9, status);
      ps.setInt(10, productId);
      return ps.executeUpdate() > 0;
    } catch (SQLException ex) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }

  public boolean DeleteProduct(int id) {
    String sql = "DELETE FROM bs_Products WHERE product_id=?";
    try (PreparedStatement ps = super.getConnection().prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException ex) {
      Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, ex);
      return false;
    }
  }

  private class ProductRow {

    private final int id;
    private final String categorySlug;
    private final String categoryName;
    private final String name;
    private final String brand;
    private final long price;
    private final int stock;
    private final String thumbnail;
    private final String description;

    private ProductRow(
        int id,
        String categorySlug,
        String categoryName,
        String name,
        String brand,
        long price,
        int stock,
        String thumbnail,
        String description) {
      this.id = id;
      this.categorySlug = categorySlug;
      this.categoryName = categoryName;
      this.name = name;
      this.brand = brand;
      this.price = price;
      this.stock = stock;
      this.thumbnail = thumbnail;
      this.description = description;
    }
  }
}
