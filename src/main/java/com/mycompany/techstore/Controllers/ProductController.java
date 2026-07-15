package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Repositories.PriceRangeRepository;
import com.mycompany.techstore.Repositories.SortOptionRepository;
import com.mycompany.techstore.services.CategoryService;
import com.mycompany.techstore.services.ProductService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mycompany.techstore.Models.Objects.Category;
import com.mycompany.techstore.Models.Objects.PriceRange;
import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Models.Objects.Review;

@WebServlet(name = "ProductController", urlPatterns = {"/product", "/index", "/home", ""})
public class ProductController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String NUMBER_REGEX = "^[0-9]+$";
    private static final String ALL_CATEGORIES = "all";
    private static final int PRODUCTS_PER_PAGE = 12;

    private transient final CategoryService categoryService;
    private transient final ProductService productService;

    public ProductController() {
        this.categoryService = new CategoryService();
        this.productService = new ProductService();
    }

    private void setCatalogAttributes(HttpServletRequest request) {
        List<Category> categories = this.categoryService.getAll();
        List<Product> products = this.productService.getAll();
        List<PriceRange> priceRanges = PriceRangeRepository.getAll();
        String searchTerm = getParameterOrDefault(request, "search", "");
        String selectedCategoryId = getParameterOrDefault(request, "category", ALL_CATEGORIES);
        String selectedPrice = getParameterOrDefault(request, "price", priceRanges.get(0).getLabel());
        String sortOrder = getParameterOrDefault(request, "sort", "recommended");
        PriceRange priceRange = getPriceRange(selectedPrice, priceRanges);
        long sliderMaxPrice = getSliderMaxPrice(products);
        long selectedMinPrice = getPriceParameter(request, "minPrice", priceRange.getMin());
        long selectedMaxPrice = getPriceParameter(request, "maxPrice", priceRange.getMax() == Long.MAX_VALUE ? sliderMaxPrice : priceRange.getMax());
        selectedMinPrice = clampPrice(selectedMinPrice, 0, sliderMaxPrice);
        selectedMaxPrice = clampPrice(selectedMaxPrice, 0, sliderMaxPrice);
        if (selectedMinPrice > selectedMaxPrice) {
            selectedMinPrice = selectedMaxPrice;
        }
        long effectiveMaxPrice = selectedMaxPrice >= sliderMaxPrice ? Long.MAX_VALUE : selectedMaxPrice + 1;
        Category activeCategory = ALL_CATEGORIES.equals(selectedCategoryId) ? null : this.categoryService.getById(selectedCategoryId);
        Map<String, List<Map<String, Object>>> categoryMenuGroups = this.categoryService.getMenuGroupsByCategory();
        Map<String, List<Map<String, String>>> categoryFilters = this.categoryService.getFiltersByCategory();
        List<Map<String, String>> activeCategoryFilters = activeCategory == null
                ? List.of()
                : categoryFilters.getOrDefault(activeCategory.getId(), List.of());
        Map<String, String> secondaryFilters = getSecondaryFilters(request, activeCategoryFilters);
        List<Product> filteredProducts = this.productService.search(
                searchTerm,
                selectedCategoryId,
                selectedMinPrice,
                effectiveMaxPrice,
                secondaryFilters,
                sortOrder
        );
        int totalFilteredProducts = filteredProducts.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalFilteredProducts / PRODUCTS_PER_PAGE));
        int currentPage = getPageParameter(request, totalPages);
        int pageStart = Math.min((currentPage - 1) * PRODUCTS_PER_PAGE, totalFilteredProducts);
        int pageEnd = Math.min(pageStart + PRODUCTS_PER_PAGE, totalFilteredProducts);
        List<Product> paginatedProducts = new ArrayList<>(filteredProducts.subList(pageStart, pageEnd));

        request.setAttribute("categories", categories);
        request.setAttribute("products", products);
        request.setAttribute("filteredProducts", filteredProducts);
        request.setAttribute("categoryMenuGroups", categoryMenuGroups);
        request.setAttribute("categoryFilters", categoryFilters);
        request.setAttribute("activeCategoryFilters", activeCategoryFilters);
        request.setAttribute("paginatedProducts", paginatedProducts);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("productsPerPage", PRODUCTS_PER_PAGE);
        request.setAttribute("pageStart", totalFilteredProducts == 0 ? 0 : pageStart + 1);
        request.setAttribute("pageEnd", pageEnd);
        request.setAttribute("activeCategory", activeCategory);
        request.setAttribute("priceRanges", priceRanges);
        request.setAttribute("sortOptions", SortOptionRepository.getAll());
        request.setAttribute("secondaryFilterOptions", this.categoryService.getSecondaryFilterOptions());
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("selectedCategoryId", selectedCategoryId);
        request.setAttribute("selectedPrice", selectedPrice);
        request.setAttribute("selectedMinPrice", selectedMinPrice);
        request.setAttribute("selectedMaxPrice", selectedMaxPrice);
        request.setAttribute("priceSliderMaxPrice", sliderMaxPrice);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("secondaryFilters", secondaryFilters);
    }

    private String getParameterOrDefault(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        return value == null ? defaultValue : value.trim();
    }

    private PriceRange getPriceRange(String selectedPrice, List<PriceRange> priceRanges) {
        for (PriceRange priceRange : priceRanges) {
            if (priceRange.getLabel().equals(selectedPrice)) {
                return priceRange;
            }
        }
        return priceRanges.get(0);
    }

    private long getPriceParameter(HttpServletRequest request, String name, long defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private long clampPrice(long price, long min, long max) {
        return Math.max(min, Math.min(price, max));
    }

    private int getPageParameter(HttpServletRequest request, int totalPages) {
        String value = request.getParameter("page");
        if (value == null || value.isBlank()) {
            return 1;
        }

        try {
            int page = Integer.parseInt(value.trim());
            return Math.max(1, Math.min(page, totalPages));
        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private long getSliderMaxPrice(List<Product> products) {
        long maxPrice = 0;
        for (Product product : products) {
            maxPrice = Math.max(maxPrice, product.getPrice());
        }

        long roundedMax = ((maxPrice + 999999) / 1000000) * 1000000;
        return Math.max(200000000L, roundedMax);
    }

    private double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }

        double total = 0;
        for (Review review : reviews) {
            total += review.getRating();
        }
        return Math.round((total / reviews.size()) * 10.0) / 10.0;
    }

    private Map<String, String> getSecondaryFilters(HttpServletRequest request, List<Map<String, String>> activeCategoryFilters) {
        Map<String, String> filters = new HashMap<>();
        if (activeCategoryFilters == null) {
            return filters;
        }

        for (Map<String, String> filter : activeCategoryFilters) {
            String key = filter.get("key");
            String value = request.getParameter(key);
            if (value != null && !value.isBlank() && !"all".equals(value)) {
                filters.put(key, value);
            }
        }
        return filters;
    }

    private void getProductHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCatalogAttributes(request);
        request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/HomePage.jsp").forward(request, response);
    }

    private void getProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || !id.matches(NUMBER_REGEX)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        Product product = this.productService.getById(Integer.parseInt(id));
        if (product == null) {
            response.sendError(404, "Product not found");
            return;
        }

        List<Review> reviews = this.productService.getReviewsByProductId(Integer.parseInt(id));
        setCatalogAttributes(request);
        request.setAttribute("product", product);
        request.setAttribute("reviews", reviews);
        request.setAttribute("averageRating", calculateAverageRating(reviews));
        request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/ProductPage.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path == null || path.isEmpty() || "/index".equals(path)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        if ("/home".equals(path)) {
            getProductHome(request, response);
            return;
        }

        getProductDetail(request, response);
    }
}
