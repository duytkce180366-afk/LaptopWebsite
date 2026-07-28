package com.mycompany.techstore.services;

import com.mycompany.techstore.Exceptions.BackOfficeValidationException;
import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminProductRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class AdminProductService {

    private static final Set<String> STATUSES
            = Set.of("Active", "Out of Stock", "Hidden", "Inactive");
    private static final int SKU_MAX_LENGTH = 80;
    private static final int PRODUCT_NAME_MAX_LENGTH = 200;
    private static final int THUMBNAIL_MAX_LENGTH = 500;
    private final AdminProductRepository repository = new AdminProductRepository();

    public PageResult<AdminProduct> findAll(
            String q, int category, int brand, String status, int page) throws SQLException {
        return repository.findAll(q, category, brand, status, Math.max(1, page), 12);
    }

    public AdminProduct findById(int id) throws SQLException {
        return repository.findById(id);
    }

    public List<LookupOption> categories() throws SQLException {
        return repository.categories();
    }

    public List<LookupOption> brands() throws SQLException {
        return repository.brands();
    }

    public Map<Integer, LinkedHashMap<String, String>> specificationTemplates()
            throws SQLException {
        return repository.specificationTemplates();
    }

    public int create(AdminProduct product, int adminId) throws SQLException {
        product.setStock(0);
        Set<String> templateKeys = templateKeys(product.getCategoryId());
        normalizeTemplateSpecifications(product, null, templateKeys);
        validate(product, templateKeys);
        return repository.create(product, adminId);
    }

    public void update(AdminProduct product, int adminId) throws SQLException {
        AdminProduct current = repository.findById(product.getProductId());
        if (current == null) {
            throw new BackOfficeValidationException("Product not found.");
        }
        product.setSku(current.getSku());
        product.setStock(current.getStock());
        Set<String> templateKeys = templateKeys(product.getCategoryId());
        normalizeTemplateSpecifications(product, current, templateKeys);
        validate(product, templateKeys);
        repository.update(product, adminId);
    }

    public void deactivate(int id, int adminId) throws SQLException {
        repository.deactivate(id, adminId);
    }

    public void receiveStock(int id, int quantity, String note, int adminId) throws SQLException {
        if (id <= 0) {
            throw new BackOfficeValidationException("Product is required.");
        }
        if (quantity <= 0) {
            throw new BackOfficeValidationException("Received quantity must be greater than zero.");
        }
        if (quantity > 100000) {
            throw new BackOfficeValidationException("Received quantity is too large.");
        }
        repository.receiveStock(id, quantity, clean(note), adminId);
    }

    public List<StockReceipt> recentReceipts() throws SQLException {
        return repository.recentReceipts();
    }

    private void validate(AdminProduct p, Set<String> templateKeys) throws SQLException {
        p.setSku(clean(p.getSku()));
        p.setProductName(clean(p.getProductName()));
        p.setDescription(clean(p.getDescription()));
        p.setThumbnail(clean(p.getThumbnail()));
        p.setStatus(canonicalStatus(p.getStatus()));
        if (p.getSku().isEmpty()) {
            throw new BackOfficeValidationException("SKU is required.");
        }
        if (p.getSku().length() > SKU_MAX_LENGTH) {
            throw new BackOfficeValidationException(
                    "SKU must not exceed " + SKU_MAX_LENGTH + " characters.");
        }
        if (p.getProductName().isEmpty()) {
            throw new BackOfficeValidationException("Product name is required.");
        }
        if (p.getProductName().length() > PRODUCT_NAME_MAX_LENGTH) {
            throw new BackOfficeValidationException(
                    "Product name must not exceed " + PRODUCT_NAME_MAX_LENGTH + " characters.");
        }
        if (p.getThumbnail().length() > THUMBNAIL_MAX_LENGTH) {
            throw new BackOfficeValidationException(
                    "Thumbnail URL must not exceed " + THUMBNAIL_MAX_LENGTH + " characters.");
        }
        if (p.getCategoryId() <= 0 || p.getBrandId() <= 0) {
            throw new BackOfficeValidationException("Category and brand are required.");
        }
        if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BackOfficeValidationException("Price must be greater than zero.");
        }
        if (p.getStock() < 0) {
            throw new BackOfficeValidationException("Stock cannot be negative.");
        }
        if (repository.skuExists(p.getSku(), p.getProductId())) {
            throw new BackOfficeValidationException("SKU already exists.");
        }
        if (p.getStock() == 0 && "Active".equals(p.getStatus())) {
            p.setStatus("Out of Stock");
        }
        if (p.getStock() > 0 && "Out of Stock".equals(p.getStatus())) {
            p.setStatus("Active");
        }
        for (String key : templateKeys) {
            if (clean(p.getSpecifications().get(key)).isEmpty()) {
                throw new BackOfficeValidationException(
                        "Specification '" + key + "' is required for the selected category.");
            }
        }
    }

    private Set<String> templateKeys(int categoryId) throws SQLException {
        Map<String, String> template = repository.specificationTemplates().get(categoryId);
        return template == null ? Collections.emptySet() : new LinkedHashSet<>(template.keySet());
    }

    private void normalizeTemplateSpecifications(
            AdminProduct submitted, AdminProduct current, Set<String> templateKeys) {
        Map<String, String> submittedSpecs = submitted.getSpecifications();
        Map<String, String> currentSpecs
                = current == null ? Collections.emptyMap() : current.getSpecifications();

        for (String templateKey : templateKeys) {
            String submittedValue = removeIgnoreCase(submittedSpecs, templateKey);
            if (submittedValue != null) {
                submittedSpecs.put(templateKey, submittedValue);
                continue;
            }

            String currentValue = findIgnoreCase(currentSpecs, templateKey);
            if (currentValue != null) {
                throw new BackOfficeValidationException(
                        "Specification key '" + templateKey + "' cannot be changed or removed.");
            }
        }
    }

    private String removeIgnoreCase(Map<String, String> specifications, String targetKey) {
        String value = null;
        Iterator<Map.Entry<String, String>> iterator = specifications.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (targetKey.equalsIgnoreCase(clean(entry.getKey()))) {
                value = entry.getValue();
                iterator.remove();
            }
        }
        return value;
    }

    private String findIgnoreCase(Map<String, String> specifications, String targetKey) {
        for (Map.Entry<String, String> entry : specifications.entrySet()) {
            if (targetKey.equalsIgnoreCase(clean(entry.getKey()))) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String canonicalStatus(String value) {
        for (String status : STATUSES) {
            if (status.equalsIgnoreCase(clean(value))) {
                return status;
            }
        }
        throw new BackOfficeValidationException("Invalid product status.");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
