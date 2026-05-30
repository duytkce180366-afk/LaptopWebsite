package Controllers;

import Repositories.CategoryRepository;
import Repositories.PriceRangeRepository;
import Repositories.ProductRepository;
import Repositories.SortOptionRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Models.Objects.Category;
import Models.Objects.PriceRange;
import Models.Objects.Product;

@WebServlet(name = "ProductController", urlPatterns = {"/product", "/index", "/home", ""})
public class ProductController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String NUMBER_REGEX = "^[0-9]+$";
    private static final String ALL_CATEGORIES = "all";

    private void setCatalogAttributes(HttpServletRequest request) {
        List<Category> categories = CategoryRepository.getAll();
        List<PriceRange> priceRanges = PriceRangeRepository.getAll();
        String searchTerm = getParameterOrDefault(request, "search", "");
        String selectedCategoryId = getParameterOrDefault(request, "category", ALL_CATEGORIES);
        String selectedPrice = getParameterOrDefault(request, "price", priceRanges.get(0).getLabel());
        String sortOrder = getParameterOrDefault(request, "sort", "recommended");
        PriceRange priceRange = getPriceRange(selectedPrice, priceRanges);
        Category activeCategory = ALL_CATEGORIES.equals(selectedCategoryId) ? null : CategoryRepository.getById(selectedCategoryId);
        Map<String, String> secondaryFilters = getSecondaryFilters(request, activeCategory);
        List<Product> filteredProducts = ProductRepository.search(
                searchTerm,
                selectedCategoryId,
                priceRange.getMin(),
                priceRange.getMax(),
                secondaryFilters,
                sortOrder
        );

        request.setAttribute("categories", categories);
        request.setAttribute("products", ProductRepository.getAll());
        request.setAttribute("filteredProducts", filteredProducts);
        request.setAttribute("activeCategory", activeCategory);
        request.setAttribute("priceRanges", priceRanges);
        request.setAttribute("sortOptions", SortOptionRepository.getAll());
        request.setAttribute("secondaryFilterOptions", CategoryRepository.getSecondaryFilterOptions());
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("selectedCategoryId", selectedCategoryId);
        request.setAttribute("selectedPrice", selectedPrice);
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

    private Map<String, String> getSecondaryFilters(HttpServletRequest request, Category activeCategory) {
        Map<String, String> filters = new HashMap<>();
        if (activeCategory == null) {
            return filters;
        }

        for (Map<String, String> filter : activeCategory.getFilters()) {
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

        Product product = ProductRepository.getById(Integer.parseInt(id));
        if (product == null) {
            response.sendError(404, "Product not found");
            return;
        }

        setCatalogAttributes(request);
        request.setAttribute("product", product);
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
