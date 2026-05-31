<%@page import="java.util.Map"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="com.mycompany.techstore.Models.Objects.Category"%>
<%@page import="java.util.List"%>
<%!
    private String active(Object current, Object expected) {
        return String.valueOf(current).equals(String.valueOf(expected)) ? " active" : "";
    }

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

    private String encode(Object value) {
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
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
%>

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String selectedCategoryId = (String) request.getAttribute("selectedCategoryId");
    String visibleCategoryId = "all".equals(selectedCategoryId) && !categories.isEmpty() ? categories.get(0).getId() : selectedCategoryId;
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
                <% for (Category category : categories) {%>
                <a class="mega-category<%= active(selectedCategoryId, category.getId())%>"
                   href="<%= request.getContextPath()%>/home?category=<%= encode(category.getId())%>#products"
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
                                String href = request.getContextPath() + "/home?category=" + encode(category.getId());
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
        <a href="<%= request.getContextPath()%>/home#home">Home</a>
        <a href="<%= request.getContextPath()%>/home#products">Products</a>
    </div>
    <button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch to dark mode" aria-pressed="false">
        <span class="theme-icon theme-icon-moon" aria-hidden="true">?</span>
        <span class="theme-icon theme-icon-sun" aria-hidden="true">?</span>
    </button>
</nav>