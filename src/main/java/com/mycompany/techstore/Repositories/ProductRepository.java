package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Models.Objects.Review;
import com.mycompany.techstore.resources.DbClass;
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
                .filter(product -> product.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Product> search(String query, String categoryId, long minPrice, long maxPrice,
            Map<String, String> filters, String sortOrder) {
        String normalizedQuery = query == null ? "" : query.toLowerCase();
        List<Product> results = getAll().stream()
                .filter(product -> normalizedQuery.isEmpty() || getSearchableText(product).contains(normalizedQuery))
                .filter(product -> categoryId == null || categoryId.isEmpty() || categoryId.equals("all") || product.getCategoryId().equals(categoryId))
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() < maxPrice)
                .filter(product -> matchesFilters(product, filters))
                .collect(Collectors.toList());

        sortResults(results, sortOrder);
        return results;
    }

    private List<ProductRow> queryProducts(Integer productId) {
        List<ProductRow> products = new ArrayList<>();
        String sqlCommand = """
                            SELECT p.product_id, p.product_name, p.description, p.price, p.stock, p.thumbnail,
                                   c.category_name, b.brand_name
                            FROM dbo.bs_Products p
                            INNER JOIN dbo.bs_Categories c ON c.category_id = p.category_id
                            INNER JOIN dbo.bs_Brands b ON b.brand_id = p.brand_id
                            WHERE (? IS NULL OR p.product_id = ?)
                              AND (p.status IS NULL OR LOWER(p.status) = 'active')
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
                    products.add(new ProductRow(
                            rs.getInt("product_id"),
                            toSlug(rs.getString("category_name")),
                            rs.getString("category_name"),
                            rs.getString("product_name"),
                            rs.getString("brand_name"),
                            rs.getLong("price"),
                            rs.getInt("stock"),
                            rs.getString("thumbnail"),
                            rs.getString("description")
                    ));
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
        Map<Integer, List<Review>> reviewsByProduct = getReviewsByProduct(productIds);
        List<Product> products = new ArrayList<>();

        for (ProductRow row : productRows) {
            Map<String, String> rawSpecs = specsByProduct.getOrDefault(row.id, new LinkedHashMap<>());
            String badge = rawSpecs.getOrDefault("badge", "Recommended");
            String warranty = rawSpecs.getOrDefault("warranty", "12 months official warranty");
            Map<String, String> visibleSpecs = new LinkedHashMap<>(rawSpecs);
            visibleSpecs.remove("badge");
            visibleSpecs.remove("warranty");

            products.add(new Product(
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
                    row.description == null ? "" : row.description,
                    reviewsByProduct.getOrDefault(row.id, new ArrayList<>())
            ));
        }

        return products;
    }

    private Map<Integer, Map<String, String>> getSpecsByProduct(List<Integer> productIds) {
        Map<Integer, Map<String, String>> specsByProduct = new HashMap<>();
        String sqlCommand = """
                            SELECT product_id, spec_key, spec_value
                            FROM dbo.bs_ProductSpecifications
                            ORDER BY product_id, sort_order;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
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

    private Map<Integer, List<Review>> getReviewsByProduct(List<Integer> productIds) {
        Map<Integer, List<Review>> reviewsByProduct = new HashMap<>();
        String sqlCommand = """
                            SELECT r.product_id, u.full_name, r.rating, r.comment, r.created_at
                            FROM dbo.bs_Reviews r
                            INNER JOIN dbo.bs_user u ON u.user_id = r.user_id
                            ORDER BY r.product_id, r.created_at DESC, r.review_id DESC;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                if (!productIds.contains(productId)) {
                    continue;
                }

                String reviewDate = "";
                if (rs.getTimestamp("created_at") != null) {
                    reviewDate = rs.getTimestamp("created_at").toLocalDateTime().toLocalDate().toString();
                }

                reviewsByProduct
                        .computeIfAbsent(productId, key -> new ArrayList<>())
                        .add(new Review(
                                rs.getString("full_name"),
                                rs.getInt("rating"),
                                reviewDate,
                                rs.getString("comment")
                        ));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(ProductRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return reviewsByProduct;
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

            String specValue = "brand".equals(filter.getKey())
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

        private ProductRow(int id, String categorySlug, String categoryName, String name, String brand,
                long price, int stock, String thumbnail, String description) {
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