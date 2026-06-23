/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import model.Product;
import repository.ProductRepository;

/**
 *
 * @author Admin
 */
public class ProductService {

    private ProductRepository productRepo;

    public ProductService() {
        productRepo = new ProductRepository();
    }

    public String validateProduct(Product p) {
        if (p.getCategoryId() <= 0) {
            return "Please select category";
        }

        if (p.getBrandId() <= 0) {
            return "Please select brand";
        }

        if (p.getSku() == null
                || p.getSku().trim().isEmpty()) {

            return "SKU is required";
        }

        if (productRepo.isSkuExist(p.getSku())) {

            return "SKU already exists";
        }

        if (p.getProductName() == null
                || p.getProductName().trim().isEmpty()) {

            return "Product name is required";
        }

        if (p.getPrice() <= 0) {

            return "Price must be greater than 0";
        }

        if (p.getStock() < 0) {

            return "Stock cannot be negative";
        }

        if (p.getThumbnail() == null
                || p.getThumbnail().isEmpty()) {

            return "Product image is required";
        }
        if (!isValidStatus(p.getStatus())) {

            return "Invalid product status";
        }

        return null;
    }

    private boolean isValidStatus(String status) {

        return status.equals("Active")
                || status.equals("Out of Stock")
                || status.equals("Inactive")
                || status.equals("Hidden");
    }

    public String createProduct(Product p) {

        String error = validateProduct(p);

        if (error != null) {
            return error;
        }

        productRepo.insert(p);

        return null;
    }

    public String updateProduct(Product p) {

        if (productRepo.isSkuExistForOther(
                p.getSku(),
                p.getProductId())) {

            return "SKU already exists";
        }

        if (p.getPrice() <= 0) {

            return "Price must be greater than 0";
        }

        if (p.getStock() < 0) {

            return "Stock cannot be negative";
        }

        if (!isValidStatus(p.getStatus())) {

            return "Invalid status";
        }

        productRepo.update(p);

        return null;
    }

    public void deleteProduct(int id) {

        productRepo.softDelete(id);
    }

    public List<Product> getAllProducts() {

        return productRepo.getAllProducts();
    }

    public Product getProductById(int id) {

        return productRepo.getProductById(id);
    }

    public List<Product> searchProducts(
            String keyword) {

        return productRepo.searchProducts(keyword);
    }

    public List<Product> paging(
            int pageIndex,
            int pageSize) {

        return productRepo.paging(
                pageIndex,
                pageSize);
    }

    public int getTotalProducts() {

        return productRepo.getTotalProducts();
    }

    public List<Product> filterStatus(String status) {

        return productRepo.filterStatus(status);

    }

    public List<Product> filterByCategory(
            int categoryId) {

        return productRepo
                .filterByCategory(categoryId);
    }

    public List<Product> filterByBrand(int brandId) {

        return productRepo.filterByBrand(brandId);

    }

    public List<Product> filterProducts(
            String keyword,
            String status,
            Integer categoryId,
            Integer brandId) {

        return productRepo.filterProducts(
                keyword,
                status,
                categoryId,
                brandId);
    }

    public int countActiveProducts() {

        return productRepo.countActiveProducts();

    }

    public int countInactiveProducts() {

        return productRepo.countInactiveProducts();

    }

    public int countBrands() {

        return productRepo.countBrands();

    }

    public void importStock(
            int productId,
            int quantity) {

        productRepo.importStock(
                productId,
                quantity);
    }

}
