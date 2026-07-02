package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.Category;
import com.mycompany.techstore.resources.DbClass;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryRepository extends DbClass {

    public List<Category> getAll() {
        Map<Integer, CategoryRow> categoryRows = getCategoryRows();
        Map<Integer, List<Map<String, Object>>> menuGroups = getMenuGroups();
        Map<Integer, List<Map<String, String>>> filters = getFilters();
        List<Category> categories = new ArrayList<>();

        for (CategoryRow row : categoryRows.values()) {
            categories.add(new Category(
                    row.slug,
                    row.name,
                    menuGroups.getOrDefault(row.categoryId, new ArrayList<>()),
                    filters.getOrDefault(row.categoryId, new ArrayList<>())
            ));
        }

        return categories;
    }

    public Category getById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        for (Category category : getAll()) {
            if (category.getId().equals(id)) {
                return category;
            }
        }

        return null;
    }

    public Map<String, List<Map<String, String>>> getSecondaryFilterOptions() {
        Map<String, List<Map<String, String>>> optionsByCategory = new LinkedHashMap<>();
        Map<Integer, CategoryRow> categoryRows = getCategoryRows();
        Map<Integer, Map<String, FilterOptionRow>> filterRowsByCategory = new LinkedHashMap<>();

        String sqlCommand = """
                            SELECT c.category_id, cf.filter_key, cf.filter_label, cfo.option_value
                            FROM dbo.bs_Categories c
                            INNER JOIN dbo.bs_CategoryFilters cf ON cf.category_id = c.category_id
                            LEFT JOIN dbo.bs_CategoryFilterOptions cfo ON cfo.category_filter_id = cf.category_filter_id
                            WHERE c.status IS NULL OR LOWER(c.status) = 'active'
                            ORDER BY c.category_id, cf.sort_order, cfo.sort_order;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String filterKey = rs.getString("filter_key");
                String filterLabel = rs.getString("filter_label");
                String optionValue = rs.getString("option_value");

                Map<String, FilterOptionRow> categoryFilters = filterRowsByCategory.computeIfAbsent(categoryId, key -> new LinkedHashMap<>());
                FilterOptionRow filter = categoryFilters.computeIfAbsent(filterKey, key -> new FilterOptionRow(filterKey, filterLabel));
                if (optionValue != null && !optionValue.isBlank()) {
                    filter.values.add(optionValue);
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(CategoryRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        for (Map.Entry<Integer, CategoryRow> categoryEntry : categoryRows.entrySet()) {
            List<Map<String, String>> filterOptions = new ArrayList<>();
            Map<String, FilterOptionRow> filterRows = filterRowsByCategory.getOrDefault(categoryEntry.getKey(), new LinkedHashMap<>());

            for (FilterOptionRow row : filterRows.values()) {
                Map<String, String> item = new HashMap<>();
                item.put("key", row.key);
                item.put("label", row.label);
                item.put("values", String.join(",", row.values));
                filterOptions.add(item);
            }

            optionsByCategory.put(categoryEntry.getValue().slug, filterOptions);
        }

        return optionsByCategory;
    }

    public List<Map<String, String>> getSecondaryFilterOptionsByCategory(String categoryId) {
        return getSecondaryFilterOptions().getOrDefault(categoryId, new ArrayList<>());
    }

    private Map<Integer, CategoryRow> getCategoryRows() {
        Map<Integer, CategoryRow> categoryRows = new LinkedHashMap<>();
        String sqlCommand = """
                            SELECT category_id, category_name
                            FROM dbo.bs_Categories
                            WHERE status IS NULL OR LOWER(status) = 'active'
                            ORDER BY category_id;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String categoryName = rs.getString("category_name");
                categoryRows.put(categoryId, new CategoryRow(categoryId, categoryName, toSlug(categoryName)));
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(CategoryRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return categoryRows;
    }

    private Map<Integer, List<Map<String, Object>>> getMenuGroups() {
        Map<Integer, List<Map<String, Object>>> groupsByCategory = new LinkedHashMap<>();
        Map<Integer, Map<String, Object>> groupsById = new LinkedHashMap<>();
        String sqlCommand = """
                            SELECT cmg.category_id, cmg.menu_group_id, cmg.group_title, cmo.option_value
                            FROM dbo.bs_CategoryMenuGroups cmg
                            LEFT JOIN dbo.bs_CategoryMenuOptions cmo ON cmo.menu_group_id = cmg.menu_group_id
                            ORDER BY cmg.category_id, cmg.sort_order, cmo.sort_order;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                int menuGroupId = rs.getInt("menu_group_id");
                String groupTitle = rs.getString("group_title");
                String optionValue = rs.getString("option_value");

                Map<String, Object> group = groupsById.get(menuGroupId);
                if (group == null) {
                    group = new LinkedHashMap<>();
                    group.put("title", groupTitle);
                    group.put("options", new ArrayList<String>());
                    groupsById.put(menuGroupId, group);
                    groupsByCategory.computeIfAbsent(categoryId, key -> new ArrayList<>()).add(group);
                }

                if (optionValue != null && !optionValue.isBlank()) {
                    @SuppressWarnings("unchecked")
                    List<String> options = (List<String>) group.get("options");
                    options.add(optionValue);
                }
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(CategoryRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return groupsByCategory;
    }

    private Map<Integer, List<Map<String, String>>> getFilters() {
        Map<Integer, List<Map<String, String>>> filtersByCategory = new LinkedHashMap<>();
        String sqlCommand = """
                            SELECT category_id, filter_key, filter_label
                            FROM dbo.bs_CategoryFilters
                            ORDER BY category_id, sort_order;
                            """;

        try (PreparedStatement ps = super.getConnection().prepareStatement(sqlCommand); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> filter = new HashMap<>();
                filter.put("key", rs.getString("filter_key"));
                filter.put("label", rs.getString("filter_label"));
                filtersByCategory.computeIfAbsent(rs.getInt("category_id"), key -> new ArrayList<>()).add(filter);
            }
        } catch (SQLException sqlEx) {
            Logger.getLogger(CategoryRepository.class.getName()).log(Level.SEVERE, null, sqlEx);
        }

        return filtersByCategory;
    }

    private String toSlug(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private class CategoryRow {

        private final int categoryId;
        private final String name;
        private final String slug;

        private CategoryRow(int categoryId, String name, String slug) {
            this.categoryId = categoryId;
            this.name = name;
            this.slug = slug;
        }
    }

    private class FilterOptionRow {

        private final String key;
        private final String label;
        private final List<String> values = new ArrayList<>();

        private FilterOptionRow(String key, String label) {
            this.key = key;
            this.label = label;
        }
    }
}