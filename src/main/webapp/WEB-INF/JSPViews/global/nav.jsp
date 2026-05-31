<%@page import="java.util.Map"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="com.mycompany.techstore.Models.Objects.Category"%>
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
    List<Category> navCategories = (List<Category>) request.getAttribute("categories");
    String navSelectedCategoryId = (String) request.getAttribute("selectedCategoryId");
    String visibleCategoryId = "all".equals(navSelectedCategoryId) && navCategories != null && !navCategories.isEmpty() ? navCategories.get(0).getId() : navSelectedCategoryId;
%>

<nav class="topbar" aria-label="Main navigation">
    <a class="brand-button" href="<%= request.getContextPath()%>/home">
        <span class="brand-mark">Tech Store</span>
        <span class="brand-subtitle">Computer store</span>
    </a>
    <div class="nav-links">
        <a href="<%= request.getContextPath()%>/home#home">Home</a>
        <a href="<%= request.getContextPath()%>/home#products">Products</a>
    </div>
    <button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch to dark mode" aria-pressed="false">
        <span class="theme-icon theme-icon-moon" aria-hidden="true">?</span>
        <span class="theme-icon theme-icon-sun" aria-hidden="true">?</span>
    </button>
</nav>