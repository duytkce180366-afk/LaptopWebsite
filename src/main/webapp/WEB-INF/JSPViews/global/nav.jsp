<%@page import="java.util.Map"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="com.mycompany.techstore.services.CategoryService"%>
<%@page import="com.mycompany.techstore.Models.Objects.Category"%>
<%@page import="com.mycompany.techstore.Models.Objects.User"%>
<%@page import="java.util.List"%>
<%!
    private String nav_active(Object current, Object expected) {
        return String.valueOf(current).equals(String.valueOf(expected)) ? " active" : "";
    }

    private String nav_html(Object value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private String nav_encode(Object value) {
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }

    private String nav_filterKeyFromMenuGroup(String groupTitle) {
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
%>

<%
    CategoryService navCategoryService = new CategoryService();
    List<Category> navCategories = navCategoryService.getAll();
    String navSelectedCategoryId = request.getParameter("category");
    String navSearchTerm = request.getParameter("search");

    if (navSearchTerm == null) {
        Object searchAttribute = request.getAttribute("searchTerm");
        navSearchTerm = searchAttribute == null ? "" : String.valueOf(searchAttribute);
    }

    if (navSelectedCategoryId == null || navSelectedCategoryId.isBlank()) {
        Object selectedCategoryAttribute = request.getAttribute("selectedCategoryId");
        navSelectedCategoryId = selectedCategoryAttribute == null ? "all" : String.valueOf(selectedCategoryAttribute);
    }

    String visibleCategoryId = navSelectedCategoryId;
    boolean hasVisibleCategory = false;
    for (Category category : navCategories) {
        if (category.getId().equals(visibleCategoryId)) {
            hasVisibleCategory = true;
            break;
        }
    }

    if (("all".equals(navSelectedCategoryId) || !hasVisibleCategory) && !navCategories.isEmpty()) {
        visibleCategoryId = navCategories.get(0).getId();
    }
%>



<nav class="topbar" aria-label="Main navigation">
    <a class="brand-button" href="<%= request.getContextPath()%>/home">
        <span class="brand-mark">Tech Store</span>
        <span class="brand-subtitle">Computer store</span>
    </a>

    <div class="category-menu">
        <button class="category-menu-button" type="button" data-action="toggle-category-menu" aria-expanded="false">
            Categories
            <span aria-hidden="true">v</span>
        </button>
        <div class="mega-menu">
            <div class="mega-list">
                <% for (Category category : navCategories) {%>
                <a class="mega-category<%= nav_active(visibleCategoryId, category.getId())%>"
                   href="<%= request.getContextPath()%>/home?category=<%= nav_encode(category.getId())%>#products"
                   data-hover-category="<%= nav_html(category.getId())%>">
                    <%= nav_html(category.getName())%>
                    <span aria-hidden="true">&gt;</span>
                </a>
                <% } %>
            </div>

            <% for (Category category : navCategories) {%>
            <div class="mega-panel <%= category.getId().equals(visibleCategoryId) ? "visible" : ""%>" data-mega-panel="<%= nav_html(category.getId())%>">
                <% for (Map<String, Object> group : category.getMenuGroups()) {
                        String title = String.valueOf(group.get("title"));
                        List<String> options = (List<String>) group.get("options");
                %>
                <section>
                    <h3><%= nav_html(title)%></h3>
                    <div class="mega-tags">
                        <% for (String option : options) {
                                String filterKey = nav_filterKeyFromMenuGroup(title);
                                String href = request.getContextPath() + "/home?category=" + nav_encode(category.getId());
                                if ("Prices".equals(title)) {
                                    href += "&price=" + nav_encode(option);
                                } else if (filterKey != null) {
                                    href += "&" + nav_encode(filterKey) + "=" + nav_encode(option);
                                } else {
                                    href += "&search=" + nav_encode(option);
                                }
                        %>
                        <a href="<%= href%>#products"><%= nav_html(option)%></a>
                        <% } %>
                    </div>
                </section>
                <% } %>
            </div>
            <% }%>
        </div>
    </div>

    <form class="nav-search" method="get" action="<%= request.getContextPath()%>/home#products" role="search">
        <% if (navSelectedCategoryId != null && !navSelectedCategoryId.isBlank() && !"all".equals(navSelectedCategoryId)) {%>
        <input type="hidden" name="category" value="<%= nav_html(navSelectedCategoryId)%>" />
        <% }%>
        <input type="search" name="search" placeholder="Search products..." value="<%= nav_html(navSearchTerm)%>" aria-label="Search products" />
        <button type="submit" aria-label="Search">&#128269;</button>
    </form>

    <div class="nav-links">
        <a href="<%= request.getContextPath()%>/home#home">Home</a>
        <a href="<%= request.getContextPath()%>/home#products">Products</a>
        <a href="<%= request.getContextPath()%>/order-history">My Orders</a>

        <%
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser == null) {
        %>
        <div class="dropdown">
            <a class="btn btn-sm btn-outline-secondary dropdown-toggle" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="user-icon" aria-hidden="true">&#128100;</span>
                <span class="visually-hidden">Account</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                <li><a class="dropdown-item" href="<%= request.getContextPath()%>/auth?action=signin">Sign in</a></li>
                <li><a class="dropdown-item" href="<%= request.getContextPath()%>/auth?action=signup">Sign up</a></li>
            </ul>
        </div>
        <%
        } else {
        %>
        <div class="dropdown">
            <a class="btn btn-sm btn-outline-secondary dropdown-toggle" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span>Account</span>
                <span class="user-email"><%= nav_html(loggedUser.getEmail())%></span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                <% if (!loggedUser.isIsVerified()) { %>
                <li><a class="dropdown-item" href="<%= request.getContextPath()%>/auth?action=verify">Verify email</a></li>
                <% } %>
                <li><a class="dropdown-item" href="<%= request.getContextPath()%>/profile">Edit profile</a></li>
                <li><a class="dropdown-item" href="<%= request.getContextPath()%>/auth?action=resetpwd">Reset Password</a></li>
                <li><a class="dropdown-item" href="<%= request.getContextPath()%>/auth?action=logout">Logout</a></li>
            </ul>
        </div>
        <%
            }
        %>
    </div>
    <button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch to dark mode" aria-pressed="false">
        <span class="theme-icon theme-icon-moon" aria-hidden="true">&#9790;</span>
        <span class="theme-icon theme-icon-sun" aria-hidden="true">&#9728;</span>
    </button>
</nav>