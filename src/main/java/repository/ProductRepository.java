/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dbcontext.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Product;

public class ProductRepository extends DBContext {

    private Product mapProduct(ResultSet rs) throws Exception {

        Product p = new Product();

        p.setProductId(rs.getInt("product_id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setBrandId(rs.getInt("brand_id"));

        p.setSku(rs.getString("sku"));
        p.setProductName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));

        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));

        p.setThumbnail(rs.getString("thumbnail"));
        p.setStatus(rs.getString("status"));

        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        p.setCategoryName(rs.getString("category_name"));
        p.setBrandName(rs.getString("brand_name"));
        return p;
    }

    public List<Product> filterStatus(String status) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "INNER JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "INNER JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE p.status = ? "
                + "ORDER BY p.product_id DESC";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setString(1, status);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> getAllProducts() {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "INNER JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "INNER JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE p.status <> 'Inactive' "
                + "ORDER BY p.product_id ASC";
        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                list.add(mapProduct(rs));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public Product getProductById(int id) {

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "INNER JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "INNER JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE p.product_id=?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, id);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {

                return mapProduct(rs);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> searchProducts(String keyword) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "INNER JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "INNER JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE p.status <> 'Inactive' "
                + "AND (p.product_name LIKE ? OR p.sku LIKE ?) "
                + "ORDER BY p.product_id DESC";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                list.add(mapProduct(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> filterProducts(
            String keyword,
            String status,
            Integer categoryId,
            Integer brandId) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE 1=1 ";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += "AND (p.product_name LIKE ? OR p.sku LIKE ?) ";
        }

        if (status != null && !status.isEmpty()) {
            sql += "AND p.status = ? ";
        }

        if (categoryId != null) {
            sql += "AND p.category_id = ? ";
        }

        if (brandId != null) {
            sql += "AND p.brand_id = ? ";
        }

        sql += "ORDER BY p.product_id DESC";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            int index = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {

                st.setString(index++,
                        "%" + keyword + "%");

                st.setString(index++,
                        "%" + keyword + "%");
            }

            if (status != null && !status.isEmpty()) {

                st.setString(index++,
                        status);
            }

            if (categoryId != null) {

                st.setInt(index++,
                        categoryId);
            }

            if (brandId != null) {

                st.setInt(index++,
                        brandId);
            }

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                list.add(mapProduct(rs));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;
    }

    public List<Product> paging(
            int pageIndex,
            int pageSize) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "INNER JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "INNER JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE p.status <> 'Inactive' "
                + "ORDER BY p.product_id DESC "
                + "OFFSET ? ROWS "
                + "FETCH NEXT ? ROWS ONLY";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(
                    1,
                    (pageIndex - 1) * pageSize);

            st.setInt(
                    2,
                    pageSize);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                list.add(mapProduct(rs));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getTotalProducts() {

        String sql
                = "SELECT COUNT(*) "
                + "FROM bs_Products "
                + "WHERE status <> 'Inactive'";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {

                return rs.getInt(1);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void insert(Product p) {

        String sql
                = "INSERT INTO bs_Products ("
                + "category_id, "
                + "brand_id, "
                + "sku, "
                + "product_name, "
                + "description, "
                + "price, "
                + "stock, "
                + "thumbnail, "
                + "status"
                + ") VALUES (?,?,?,?,?,?,?,?,?)";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, p.getCategoryId());
            st.setInt(2, p.getBrandId());

            st.setString(3, p.getSku());
            st.setString(4, p.getProductName());
            st.setString(5, p.getDescription());

            st.setDouble(6, p.getPrice());
            st.setInt(7, p.getStock());

            st.setString(8, p.getThumbnail());
            st.setString(9, p.getStatus());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Product p) {

        String sql
                = "UPDATE bs_Products "
                + "SET "
                + "category_id=?, "
                + "brand_id=?, "
                + "sku=?, "
                + "product_name=?, "
                + "description=?, "
                + "price=?, "
                + "stock=?, "
                + "thumbnail=?, "
                + "status=?, "
                + "updated_at=GETDATE() "
                + "WHERE product_id=?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, p.getCategoryId());
            st.setInt(2, p.getBrandId());

            st.setString(3, p.getSku());
            st.setString(4, p.getProductName());
            st.setString(5, p.getDescription());

            st.setDouble(6, p.getPrice());
            st.setInt(7, p.getStock());

            st.setString(8, p.getThumbnail());
            st.setString(9, p.getStatus());

            st.setInt(10, p.getProductId());

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void softDelete(int id) {

        String sql
                = "UPDATE bs_Products "
                + "SET status='Inactive' "
                + "WHERE product_id=?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, id);

            st.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSkuExist(String sku) {

        String sql
                = "SELECT COUNT(*) "
                + "FROM bs_Products "
                + "WHERE sku = ?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setString(1, sku);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isSkuExistForOther(
            String sku,
            int productId) {

        String sql
                = "SELECT COUNT(*) "
                + "FROM bs_Products "
                + "WHERE sku = ? "
                + "AND product_id <> ?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setString(1, sku);
            st.setInt(2, productId);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Product> filterByCategory(
            int categoryId) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "INNER JOIN bs_Categories c "
                + "ON p.category_id=c.category_id "
                + "INNER JOIN bs_Brands b "
                + "ON p.brand_id=b.brand_id "
                + "WHERE p.category_id=?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, categoryId);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                list.add(mapProduct(rs));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;
    }

    public List<Product> filterByBrand(int brandId) {

        List<Product> list = new ArrayList<>();

        String sql
                = "SELECT p.*, "
                + "c.category_name, "
                + "b.brand_name "
                + "FROM bs_Products p "
                + "JOIN bs_Categories c "
                + "ON p.category_id = c.category_id "
                + "JOIN bs_Brands b "
                + "ON p.brand_id = b.brand_id "
                + "WHERE p.brand_id = ? "
                + "ORDER BY p.product_id DESC";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, brandId);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                list.add(mapProduct(rs));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;
    }

    public int countActiveProducts() {

        String sql
                = "SELECT COUNT(*) "
                + "FROM bs_Products "
                + "WHERE status = 'Active'";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            ResultSet rs
                    = st.executeQuery();

            if (rs.next()) {

                return rs.getInt(1);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;
    }

    public int countInactiveProducts() {

        String sql
                = "SELECT COUNT(*) "
                + "FROM bs_Products "
                + "WHERE status = 'Inactive'";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            ResultSet rs
                    = st.executeQuery();

            if (rs.next()) {

                return rs.getInt(1);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;
    }

    public int countBrands() {

        String sql
                = "SELECT COUNT(*) "
                + "FROM bs_Brands";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            ResultSet rs
                    = st.executeQuery();

            if (rs.next()) {

                return rs.getInt(1);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return 0;
    }

    public void importStock(
            int productId,
            int quantity) {

        String sql
                = "UPDATE bs_Products "
                + "SET stock = stock + ? "
                + "WHERE product_id = ?";

        try {

            PreparedStatement st
                    = connection.prepareStatement(sql);

            st.setInt(1, quantity);

            st.setInt(2, productId);

            st.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

}
