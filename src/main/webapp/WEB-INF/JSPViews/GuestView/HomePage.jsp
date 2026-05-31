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

    private String formatSpecLabel(String key) {
        String label = key.replaceAll("([A-Z])", " $1");
        label = label.substring(0, 1).toUpperCase() + label.substring(1);
        return label.replace("Gpu", "GPU")
                .replace("Cpu", "CPU")
                .replace("Dpi", "DPI")
                .replace("Tdp", "TDP")
                .replace("Vram", "VRAM");
    }

    private String filterKeyFromMenuGroup(String groupTitle) {
        switch (groupTitle) {
            case "Brands":
                return "brand";
            case "Purpose":
                return "purpose";
            case "CPU":
                return "cpu";
            case "GPU":
                return "gpu";
            case "Screen":
                return "display";
            case "Sensor":
                return "sensor";
            case "Connection":
                return "connection";
            case "Switch":
                return "switchType";
            case "Layout":
                return "layout";
            case "Resolution":
                return "resolution";
            case "Refresh rate":
                return "refreshRate";
            case "Capacity":
                return "capacity";
            case "Interface":
                return "interfaceType";
            case "Type":
                return "memoryType";
            case "Bus RAM":
                return "bus";
            case "Socket":
                return "socket";
            case "Cores":
                return "cores";
            case "Chipset":
                return "chipset";
            case "VRAM":
                return "vram";
            case "Motherboard":
                return "motherboardSupport";
            case "Color":
                return "color";
            case "Size":
                return "size";
            default:
                return null;
        }
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
    String sortOrder = (String) request.getAttribute("sortOrder");
    String searchTerm = (String) request.getAttribute("searchTerm");

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
    if (sortOrder == null) {
        sortOrder = "recommended";
    }
    if (searchTerm == null) {
        searchTerm = "";
    }

    String contextPath = request.getContextPath();
    String visibleCategoryId = "all".equals(selectedCategoryId) && !categories.isEmpty() ? categories.get(0).getId() : selectedCategoryId;
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <main class="app-shell" id="app">
            <nav class="topbar" aria-label="Main navigation">
                <a class="brand-button" href="<%= contextPath%>/home">
                    <span class="brand-mark">TechStore</span>
                    <span class="brand-subtitle">Computer store</span>
                </a>

                <div class="category-menu">
                    <button class="category-menu-button" type="button" data-action="toggle-category-menu" aria-expanded="false">
                        Categories
                        <span aria-hidden="true">v</span>
                    </button>
                    <div class="mega-menu">
                        <div class="mega-list">
                            <% for (Category category : categories) {%>
                            <a class="mega-category<%= active(selectedCategoryId, category.getId())%>"
                               href="<%= contextPath%>/home?category=<%= encode(category.getId())%>#products"
                               data-hover-category="<%= html(category.getId())%>">
                                <%= html(category.getName())%>
                                <span aria-hidden="true">&gt;</span>
                            </a>
                            <% } %>
                        </div>

                        <% for (Category category : categories) {%>
                        <div class="mega-panel <%= category.getId().equals(visibleCategoryId) ? "visible" : ""%>" data-mega-panel="<%= html(category.getId())%>">
                            <% for (Map<String, Object> group : category.getMenuGroups()) {
                                    String title = String.valueOf(group.get("title"));
                                    List<String> options = (List<String>) group.get("options");
                            %>
                            <section>
                                <h3><%= html(title)%></h3>
                                <div class="mega-tags">
                                    <% for (String option : options) {
                                            String filterKey = filterKeyFromMenuGroup(title);
                                            String href = contextPath + "/home?category=" + encode(category.getId());
                                            if ("Prices".equals(title)) {
                                                href += "&price=" + encode(option);
                                            } else if (filterKey != null) {
                                                href += "&" + encode(filterKey) + "=" + encode(option);
                                            } else {
                                                href += "&search=" + encode(option);
                                            }
                                    %>
                                    <a href="<%= href%>#products"><%= html(option)%></a>
                                    <% } %>
                                </div>
                            </section>
                            <% } %>
                        </div>
                        <% }%>
                    </div>
                </div>

                <div class="nav-links">
                    <a href="<%= contextPath%>/home#home">Home</a>
                    <a href="<%= contextPath%>/home#products">Products</a>
                </div>
                <button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch to dark mode" aria-pressed="false">
                    <span class="theme-icon theme-icon-moon" aria-hidden="true">☾</span>
                    <span class="theme-icon theme-icon-sun" aria-hidden="true">☀</span>
                </button>
            </nav>

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

                    <label>
                        <span>Price</span>
                        <select name="price">
                            <% for (PriceRange range : priceRanges) {%>
                            <option value="<%= html(range.getLabel())%>" <%= selected(selectedPrice, range.getLabel())%>><%= html(range.getLabel())%></option>
                            <% } %>
                        </select>
                    </label>

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
                            <% for (Map<String, String> filter : activeCategory.getFilters()) {
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
                    <% for (Product product : filteredProducts) {
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
