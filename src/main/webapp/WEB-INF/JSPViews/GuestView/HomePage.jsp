<%@page import="com.mycompany.techstore.Models.Objects.Category"%>
<%@page import="com.mycompany.techstore.Models.Objects.PriceRange"%>
<%@page import="com.mycompany.techstore.Models.Objects.Product"%>
<%@page import="com.mycompany.techstore.Models.Objects.SortOption"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!
    private String html(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String selected(Object current, Object expected) {
        return String.valueOf(current).equals(String.valueOf(expected)) ? "selected" : "";
    }

    private String active(Object current, Object expected) {
        return String.valueOf(current).equals(String.valueOf(expected)) ? " active" : "";
    }

    private String checkedFilter(Map<String, String> filters, String key, String value) {
        return value.equals(filters.get(key)) ? "selected" : "";
    }

    private String encode(Object value) {
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }

    private String formatPrice(long price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(price);
    }

    private int countProducts(List<Product> products, String categoryId) {
        int count = 0;
        for (Product product : products) {
            if (categoryId.equals(product.getCategoryId())) {
                count++;
            }
        }
        return count;
    }

    private String[] secondaryOptions(Map<String, List<Map<String, String>>> options, String categoryId, String key) {
        List<Map<String, String>> categoryOptions = options.get(categoryId);
        if (categoryOptions == null) {
            return new String[0];
        }

        for (Map<String, String> option : categoryOptions) {
            if (key.equals(option.get("key"))) {
                return option.get("values").split(",");
            }
        }
        return new String[0];
    }
%>

<%

    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Product> products = (List<Product>) request.getAttribute("products");
    List<Product> filteredProducts = (List<Product>) request.getAttribute("filteredProducts");
    List<PriceRange> priceRanges = (List<PriceRange>) request.getAttribute("priceRanges");
    List<SortOption> sortOptions = (List<SortOption>) request.getAttribute("sortOptions");
    Map<String, List<Map<String, String>>> secondaryFilterOptions = (Map<String, List<Map<String, String>>>) request.getAttribute("secondaryFilterOptions");
    Map<String, String> secondaryFilters = (Map<String, String>) request.getAttribute("secondaryFilters");
    Category activeCategory = (Category) request.getAttribute("activeCategory");
    String selectedCategoryId = (String) request.getAttribute("selectedCategoryId");
    String selectedPrice = (String) request.getAttribute("selectedPrice");
    Long selectedMinPrice = (Long) request.getAttribute("selectedMinPrice");
    Long selectedMaxPrice = (Long) request.getAttribute("selectedMaxPrice");
    Long priceSliderMaxPrice = (Long) request.getAttribute("priceSliderMaxPrice");
    String sortOrder = (String) request.getAttribute("sortOrder");
    String searchTerm = (String) request.getAttribute("searchTerm");

    // Checking if null
    if (categories == null) {
        categories = new ArrayList<>();
    }
    if (products == null) {
        products = new ArrayList<>();
    }
    if (filteredProducts == null) {
        filteredProducts = new ArrayList<>();
    }
    if (priceRanges == null) {
        priceRanges = new ArrayList<>();
    }
    if (sortOptions == null) {
        sortOptions = new ArrayList<>();
    }
    if (secondaryFilters == null) {
        secondaryFilters = Map.of();
    }
    if (secondaryFilterOptions == null) {
        secondaryFilterOptions = Map.of();
    }
    if (selectedCategoryId == null) {
        selectedCategoryId = "all";
    }
    if (selectedPrice == null) {
        selectedPrice = "All prices";
    }
    if (priceSliderMaxPrice == null) {
        priceSliderMaxPrice = 200000000L;
    }
    if (selectedMinPrice == null) {
        selectedMinPrice = 0L;
    }
    if (selectedMaxPrice == null) {
        selectedMaxPrice = priceSliderMaxPrice;
    }
    if (sortOrder == null) {
        sortOrder = "recommended";
    }
    if (searchTerm == null) {
        searchTerm = "";
    }

    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <main class="app-shell" id="app">

            <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
            <section class="hero" id="home">
                <div class="hero-copy">
                    <p class="eyebrow">Shop for all kinds of computer's hardware</p>
                    <h1>Find laptops, PC parts, and accessories with category-specific filters.</h1>
                    <p>
                        This demo implements the assigned browsing features with an expanded product
                        catalog: search, category menu, filters, sorting, product detail pages, and
                        customer reviews.
                    </p>
                    <div class="hero-actions">
                        <a class="primary-action" href="#products">Browse products</a>
                        <a class="secondary-action" href="#categories">View categories</a>
                    </div>
                </div>

                <div class="hero-panel" aria-label="Featured promotion">
                    <span class="promo-label">Catalog</span>
                    <h2>Computer store products in one searchable page</h2>
                    <p>Compare specs, price, stock status, and real customer feedback.</p>
                    <div class="promo-stats">
                        <span><strong><%= products.size()%></strong>Products</span>
                        <span><strong><%= categories.size()%></strong>Categories</span>
                    </div>
                </div>
            </section>

            <section class="section-heading" id="categories" aria-labelledby="featured-title">
                <p class="eyebrow">Featured categories</p>
                <h2 id="featured-title">Computer store departments</h2>
                <div class="category-grid category-grid-wide">
                    <% for (Category category : categories) {%>
                    <a class="category-card<%= active(selectedCategoryId, category.getId())%>"
                       href="<%= contextPath%>/home?category=<%= encode(category.getId())%>#products">
                        <span><%= html(category.getName())%></span>
                        <small><%= countProducts(products, category.getId())%> products</small>
                    </a>
                    <% }%>
                </div>
            </section>

            <section class="catalog-section" id="products" aria-labelledby="catalog-title">
                <div class="section-heading">
                    <p class="eyebrow">Search and Filter Products</p>
                    <h2 id="catalog-title">Product catalog</h2>
                </div>

                <form class="filters" method="get" action="<%= contextPath%>/home#products" aria-label="Product filters">
                    <label class="search-field">
                        <span>Search</span>
                        <input type="search" name="search" placeholder="Search by product, brand, category, or specs" value="<%= html(searchTerm)%>" />
                    </label>

                    <label>
                        <span>Category</span>
                        <select name="category">
                            <option value="all">All categories</option>
                            <% for (Category category : categories) {%>
                            <option value="<%= html(category.getId())%>" <%= selected(selectedCategoryId, category.getId())%>><%= html(category.getName())%></option>
                            <% } %>
                        </select>
                    </label>

                    <div class="price-slider-field" data-price-slider>
                        <span>Price</span>
                        <button class="price-filter-button" type="button" data-price-toggle aria-expanded="false">
                            <span data-price-button-label>Price: <%= formatPrice(selectedMinPrice)%> - <%= formatPrice(selectedMaxPrice)%></span>
                            <span aria-hidden="true">v</span>
                        </button>
                        <input type="hidden" name="price" value="<%= html(selectedPrice)%>" />
                        <div class="price-slider-panel" data-price-panel hidden>
                            <div class="price-slider-heading">
                                <strong>Choose your price range</strong>
                                <output data-price-output><%= formatPrice(selectedMinPrice)%> - <%= formatPrice(selectedMaxPrice)%></output>
                            </div>
                            <div class="price-slider-values">
                                <input type="number" name="minPrice" min="0" max="<%= priceSliderMaxPrice%>" step="100000" value="<%= selectedMinPrice%>" aria-label="Minimum price" data-price-min-input />
                                <span aria-hidden="true">-</span>
                                <input type="number" name="maxPrice" min="0" max="<%= priceSliderMaxPrice%>" step="100000" value="<%= selectedMaxPrice%>" aria-label="Maximum price" data-price-max-input />
                            </div>
                            <div class="price-slider-track">
                                <input type="range" min="0" max="<%= priceSliderMaxPrice%>" step="100000" value="<%= selectedMinPrice%>" aria-label="Minimum price slider" data-price-min-range />
                                <input type="range" min="0" max="<%= priceSliderMaxPrice%>" step="100000" value="<%= selectedMaxPrice%>" aria-label="Maximum price slider" data-price-max-range />
                            </div>
                            <div class="price-slider-actions">
                                <button class="clear-button" type="button" data-price-close>Close</button>
                                <button class="primary-action" type="submit">View results</button>
                            </div>
                        </div>
                    </div>

                    <label>
                        <span>Sort</span>
                        <select name="sort">
                            <% for (SortOption option : sortOptions) {%>
                            <option value="<%= html(option.getValue())%>" <%= selected(sortOrder, option.getValue())%>><%= html(option.getLabel())%></option>
                            <% }%>
                        </select>
                    </label>

                    <button class="primary-action filter-submit" type="submit">Apply</button>
                    <a class="clear-button" href="<%= contextPath%>/home#products">Clear</a>

                    <% if (activeCategory != null) {%>
                    <div class="secondary-filter-panel">
                        <div>
                            <p class="eyebrow">Secondary filters</p>
                            <h3><%= html(activeCategory.getName())%></h3>
                        </div>
                        <div class="secondary-filter-grid">
                            <% 
                                for (Map<String, String> filter : activeCategory.getFilters()) {
                                    String key = filter.get("key");
                            %>
                            <label>
                                <span><%= html(filter.get("label"))%></span>
                                <select name="<%= html(key)%>">
                                    <option value="all">All</option>
                                    <% for (String option : secondaryOptions(secondaryFilterOptions, activeCategory.getId(), key)) {%>
                                    <option value="<%= html(option)%>" <%= checkedFilter(secondaryFilters, key, option)%>><%= html(option)%></option>
                                    <% } %>
                                </select>
                            </label>
                            <% } %>
                        </div>
                    </div>
                    <% }%>
                </form>

                <p class="result-count">Showing <%= filteredProducts.size()%> of <%= products.size()%> products</p>

                <div class="product-grid">
                    <%
                        for (Product product : filteredProducts) {
                            List<Map.Entry<String, String>> specs = new ArrayList<>(product.getSpecs().entrySet());
                    %>
                    <article class="product-card">
                        <img src="<%= html(product.getImage())%>" alt="<%= html(product.getName())%>" />
                        <div class="product-content">
                            <div class="card-topline">
                                <span><%= html(product.getBadge())%></span>
                                <span><%= product.getStock() > 0 ? product.getStock() + " in stock" : "Out of stock"%></span>
                            </div>
                            <h3><%= html(product.getName())%></h3>
                            <p><%= html(product.getCategory())%></p>
                            <div class="spec-pills">
                                <% for (int i = 0; i < specs.size() && i < 3; i++) {%>
                                <span><%= html(specs.get(i).getValue())%></span>
                                <% }%>
                            </div>
                            <div class="product-footer">
                                <strong><%= formatPrice(product.getPrice())%></strong>
                                <a href="<%= contextPath%>/product?id=<%= product.getId()%>">View details</a>
                            </div>
                        </div>
                    </article>
                    <% } %>
                </div>

                <% if (filteredProducts.isEmpty()) {%>
                <div class="empty-state">
                    <h3>No products found</h3>
                    <p>Try another keyword, category, price range, sort option, or secondary filter.</p>
                    <a href="<%= contextPath%>/home#products">Reset filters</a>
                </div>
                <% }%>
            </section>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
