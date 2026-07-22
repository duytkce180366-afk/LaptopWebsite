<%@page import="org.jsoup.safety.Safelist"%>
<%@page import="org.jsoup.Jsoup"%>
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
    List<Product> paginatedProducts = (List<Product>) request.getAttribute("paginatedProducts");
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
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer pageStart = (Integer) request.getAttribute("pageStart");
    Integer pageEnd = (Integer) request.getAttribute("pageEnd");

    if (categories == null) {
        categories = new ArrayList<>();
    }
    if (products == null) {
        products = new ArrayList<>();
    }
    if (filteredProducts == null) {
        filteredProducts = new ArrayList<>();
    }
    if (paginatedProducts == null) {
        paginatedProducts = filteredProducts;
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
    if (currentPage == null) {
        currentPage = 1;
    }
    if (totalPages == null) {
        totalPages = 1;
    }
    if (pageStart == null) {
        pageStart = filteredProducts.isEmpty() ? 0 : 1;
    }
    if (pageEnd == null) {
        pageEnd = paginatedProducts.size();
    }

    String contextPath = request.getContextPath();
    StringBuilder paginationQuery = new StringBuilder();
    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
        if ("page".equals(entry.getKey())) {
            continue;
        }
        for (String value : entry.getValue()) {
            if (value == null || value.isBlank()) {
                continue;
            }
            if (paginationQuery.length() > 0) {
                paginationQuery.append("&");
            }
            paginationQuery.append(encode(entry.getKey())).append("=").append(encode(value));
        }
    }
    String paginationBase = contextPath + "/home" + (paginationQuery.length() > 0 ? "?" + paginationQuery + "&page=" : "?page=");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Tech Store</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <main class="app-shell" id="app">
            <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
                        <%
                            String storeNotice = (String) session.getAttribute("storeNotice");
                            if (storeNotice != null) {
                                session.removeAttribute("storeNotice");
                        %>
                        <div class="alert alert-info" role="status">
                            <%= Jsoup.clean(storeNotice, Safelist.basic())%>
                        </div>
                        <% }%>
                        <%
                            String error = request.getParameter("error");
                            if (error != null) {
                        %>
                        <div class="alert alert-danger" role="alert">
                            Error: <%= Jsoup.clean(error, Safelist.basic())%>
                        </div>
                        <% }%>
            <section class="storefront-hero" id="home" aria-label="Promotions">
                <div id="bannerCarousel" class="carousel slide hero-carousel" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <%                            int heroSlides = Math.min(3, products.size());
                            if (heroSlides == 0) {
                        %>
                        <div class="carousel-item active">
                            <div class="hero-slide hero-slide-empty">
                                <div>
                                    <h1>Back to School</h1>
                                    <span>Buy one, get three gifts</span>
                                </div>
                            </div>
                        </div>
                        <% } else {
                            for (int i = 0; i < heroSlides; i++) {
                                Product product = products.get(i);
                        %>
                        <div class="carousel-item <%= i == 0 ? "active" : ""%>">
                            <a class="hero-slide" href="<%= contextPath%>/product?id=<%= product.getId()%>">
                                <img src="<%= html(product.getThumbnail())%>" alt="<%= html(product.getName())%>" />
                                <div>
                                    <p>Tech Store</p>
                                    <h1><%= i == 0 ? "Back to School" : html(product.getBrand())%></h1>
                                    <span><%= html(product.getName())%></span>
                                </div>
                            </a>
                        </div>
                        <% }
                            }%>
                    </div>
                    <button class="carousel-control-prev" type="button" data-bs-target="#bannerCarousel" data-bs-slide="prev" aria-label="Previous promotion">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    </button>
                    <button class="carousel-control-next" type="button" data-bs-target="#bannerCarousel" data-bs-slide="next" aria-label="Next promotion">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    </button>
                </div>

                <div class="hero-side-promos">
                    <% for (int i = 0; i < 2; i++) {
                            Product promoProduct = products.size() > i + 3 ? products.get(i + 3) : (products.isEmpty() ? null : products.get(0));
                    %>
                    <a class="mini-promo" href="<%= promoProduct == null ? contextPath + "/home#products" : contextPath + "/product?id=" + promoProduct.getId()%>">
                        <% if (promoProduct != null) {%>
                        <img src="<%= html(promoProduct.getThumbnail())%>" alt="<%= html(promoProduct.getName())%>" />
                        <% }%>
                        <strong><%= i == 0 ? "Shop gaming laptops" : "Flexible payments"%></strong>
                        <span><%= i == 0 ? "Save on selected products" : "Upgrade today"%></span>
                    </a>
                    <% }%>
                </div>
            </section>

            <section class="featured-categories" id="categories" aria-labelledby="featured-title">
                <div id="categoryCarousel" class="carousel slide" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <% if (categories.isEmpty()) {%>
                        <div class="carousel-item active">
                            <div class="category-strip category-strip-empty">
                                <a class="category-card" href="<%= contextPath%>/home#products">
                                    <span class="category-icon" aria-hidden="true">&#128187;</span>
                                    <strong>All Products</strong>
                                    <small>Browse the catalog</small>
                                </a>
                            </div>
                        </div>
                        <% } %>
                        <% for (int start = 0; start < categories.size(); start += 6) {%>
                        <div class="carousel-item <%= start == 0 ? "active" : ""%>">
                            <div class="category-strip">
                                <% for (int i = start; i < categories.size() && i < start + 6; i++) {
                                        Category category = categories.get(i);
                                %>
                                <a class="category-card<%= active(selectedCategoryId, category.getId())%>"
                                   href="<%= contextPath%>/home?category=<%= encode(category.getId())%>#products">
                                    <strong><%= html(category.getName())%></strong>
                                    <small><%= countProducts(products, category.getId())%> products</small>
                                </a>
                                <% } %>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <button class="carousel-control-prev category-control" type="button" data-bs-target="#categoryCarousel" data-bs-slide="prev" aria-label="Previous categories">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    </button>
                    <button class="carousel-control-next category-control" type="button" data-bs-target="#categoryCarousel" data-bs-slide="next" aria-label="Next categories">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    </button>
                </div>
            </section>

            <section class="best-seller-section" aria-labelledby="best-seller-title">
                <h2 id="best-seller-title">BEST SELLERS</h2>
                <div id="bestSellerCarousel" class="carousel slide" data-bs-ride="carousel">
                    <div class="carousel-inner">
                        <% if (products.isEmpty()) { %>
                        <div class="carousel-item active">
                            <div class="empty-state best-seller-empty">
                                <h3>No featured products yet</h3>
                                <p>Products will appear here when the catalog has available items.</p>
                            </div>
                        </div>
                        <% } %>
                        <% for (int start = 0; start < products.size() && start < 10; start += 5) {%>
                        <div class="carousel-item <%= start == 0 ? "active" : ""%>">
                            <div class="best-seller-grid">
                                <% for (int i = start; i < products.size() && i < start + 5; i++) {
                                        Product product = products.get(i);
                                %>
                                <article class="product-card best-seller-card">
                                    <a href="<%= contextPath%>/product?id=<%= product.getId()%>">
                                        <img src="<%= html(product.getThumbnail())%>" alt="<%= html(product.getName())%>" />
                                    </a>
                                    <div class="product-content">
                                        <h3><%= html(product.getName())%></h3>
                                        <div class="product-price-row">
                                            <strong><%= formatPrice(product.getPrice())%></strong>
                                            <small><%= html(product.getBadge())%></small>
                                        </div>
                                        <a class="buy-button" href="<%= contextPath%>/product?id=<%= product.getId()%>">BUY NOW</a>
                                    </div>
                                </article>
                                <% } %>
                            </div>
                        </div>
                        <% }%>
                    </div>
                    <button class="carousel-control-prev best-seller-control" type="button" data-bs-target="#bestSellerCarousel" data-bs-slide="prev" aria-label="Previous best sellers">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    </button>
                    <button class="carousel-control-next best-seller-control" type="button" data-bs-target="#bestSellerCarousel" data-bs-slide="next" aria-label="Next best sellers">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    </button>
                </div>
            </section>

            <section class="catalog-section" id="products" aria-labelledby="catalog-title">
                <div class="catalog-header">
                    <div>
                        <p class="eyebrow">Product catalog</p>
                        <h2 id="catalog-title">Shop products</h2>
                    </div>
                    <p class="result-count">
                        Showing <%= pageStart%>-<%= pageEnd%> of <%= filteredProducts.size()%> products
                    </p>
                </div>

                <div class="catalog-layout">
                    <aside class="filter-sidebar" aria-label="Product filters">
                        <form class="filters" method="get" action="<%= contextPath%>/home#products">
                            <% if (!searchTerm.isBlank()) {%>
                            <input type="hidden" name="search" value="<%= html(searchTerm)%>" />
                            <% }%>

                            <div class="filter-title">Filters</div>

                            <div class="filter-group price-slider-field" data-price-slider>
                                <span>Product price</span>
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
                                        <span class="price-slider-thumb" data-price-min-thumb aria-hidden="true"></span>
                                        <span class="price-slider-thumb" data-price-max-thumb aria-hidden="true"></span>
                                        <input type="range" min="0" max="<%= priceSliderMaxPrice%>" step="100000" value="<%= selectedMinPrice%>" aria-label="Minimum price slider" data-price-min-range />
                                        <input type="range" min="0" max="<%= priceSliderMaxPrice%>" step="100000" value="<%= selectedMaxPrice%>" aria-label="Maximum price slider" data-price-max-range />
                                    </div>
                                    <div class="price-slider-actions">
                                        <button class="clear-button" type="button" data-price-close>Close</button>
                                        <button class="primary-action" type="submit">View results</button>
                                    </div>
                                </div>
                            </div>

                            <label class="filter-group">
                                <span>Product category</span>
                                <select name="category">
                                    <option value="all">All categories</option>
                                    <% for (Category category : categories) {%>
                                    <option value="<%= html(category.getId())%>" <%= selected(selectedCategoryId, category.getId())%>><%= html(category.getName())%></option>
                                    <% }%>
                                </select>
                            </label>

                            <label class="filter-group">
                                <span>Sort by</span>
                                <select name="sort">
                                    <% for (SortOption option : sortOptions) {%>
                                    <option value="<%= html(option.getValue())%>" <%= selected(sortOrder, option.getValue())%>><%= html(option.getLabel())%></option>
                                    <% }%>
                                </select>
                            </label>

                            <%
                                List<Map<String, String>> activeCategoryFilters = (List<Map<String, String>>) request.getAttribute("activeCategoryFilters");
                                if (activeCategory != null && activeCategoryFilters != null) {
                                    for (Map<String, String> filter : activeCategoryFilters) {
                                        String key = filter.get("key");
                            %>
                            <label class="filter-group">
                                <span><%= html(filter.get("label"))%></span>
                                <select name="<%= html(key)%>">
                                    <option value="all">All</option>
                                    <% for (String option : secondaryOptions(secondaryFilterOptions, activeCategory.getId(), key)) {%>
                                    <option value="<%= html(option)%>" <%= checkedFilter(secondaryFilters, key, option)%>><%= html(option)%></option>
                                    <% } %>
                                </select>
                            </label>
                            <% }
                                }%>

                            <button class="primary-action filter-submit" type="submit">Filter products</button>
                            <a class="clear-button" href="<%= contextPath%>/home#products">Clear</a>
                        </form>
                    </aside>

                    <div class="catalog-products">
                        <div class="product-grid">
                            <%
                                for (Product product : paginatedProducts) {
                            %>
                            <article class="product-card">
                                <a href="<%= contextPath%>/product?id=<%= product.getId()%>">
                                    <img src="<%= html(product.getThumbnail())%>" alt="<%= html(product.getName())%>" />
                                </a>
                                <div class="product-content">
                                    <div class="card-topline">
                                        <span><%= html(product.getBadge())%></span>
                                    </div>
                                    <h3><%= html(product.getName())%></h3>
                                    <div class="product-price-row">
                                        <strong><%= formatPrice(product.getPrice())%></strong>
                                        <small><%= product.getStock() > 0 ? product.getStock() + " in stock" : "Out of stock"%></small>
                                    </div>
                                    <a class="buy-button" href="<%= contextPath%>/product?id=<%= product.getId()%>">BUY NOW</a>
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

                        <% if (totalPages > 1) {%>
                        <nav class="pagination-nav" aria-label="Product pages">
                            <% if (currentPage > 1) {%>
                            <a href="<%= paginationBase%><%= currentPage - 1%>#products">Previous</a>
                            <% }%>
                            <% for (int i = 1; i <= totalPages; i++) {%>
                            <a class="<%= i == currentPage ? "active" : ""%>" href="<%= paginationBase%><%= i%>#products"><%= i%></a>
                            <% }%>
                            <% if (currentPage < totalPages) {%>
                            <a href="<%= paginationBase%><%= currentPage + 1%>#products">Next</a>
                            <% }%>
                        </nav>
                        <% }%>
                    </div>
                </div>
            </section>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
