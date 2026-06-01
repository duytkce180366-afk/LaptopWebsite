<%@page import="java.util.Map"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.nio.charset.StandardCharsets"%>
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
    List<Category> navCategories = (List<Category>) request.getAttribute("categories");
    String navSelectedCategoryId = (String) request.getAttribute("selectedCategoryId");
    String visibleCategoryId = "all".equals(navSelectedCategoryId) && navCategories != null && !navCategories.isEmpty() ? navCategories.get(0).getId() : navSelectedCategoryId;
%>

<nav class="topbar" aria-label="Main navigation">
    <a class="brand-button" href="<%= request.getContextPath()%>/home">
        <span class="brand-mark">Tech Store</span>
        <span class="brand-subtitle">Computer store</span>
    </a>

    <%--    <div class="category-menu">
            <button class="category-menu-button" type="button" data-action="toggle-category-menu" aria-expanded="false">
                Categories
                <span aria-hidden="true">v</span>
            </button>
            <div class="mega-menu">

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
    --%>

    <div class="nav-links">
        <a href="<%= request.getContextPath()%>/home#home">Home</a>
        <a href="<%= request.getContextPath()%>/home#products">Products</a>

        <%
            User loggedUser = (User) session.getAttribute("loggedUser");
            if (loggedUser == null) {
        %>
        <div class="user-menu">
            <button class="user-button" type="button" data-action="toggle-user-menu" aria-expanded="false" aria-haspopup="true">
                <span class="user-icon" aria-hidden="true">👤</span>
                <span class="visually-hidden">Account</span>
            </button>
            <div class="user-dropdown" role="menu" hidden>
                <a href="<%= request.getContextPath()%>/auth?action=signin" role="menuitem">Sign in</a>
                <a href="<%= request.getContextPath()%>/auth?action=signup" role="menuitem">Sign up</a>
            </div>
        </div>
        <%
            } else {
        %>
        <div class="user-menu">
            <button class="user-button" type="button" data-action="toggle-user-menu" aria-expanded="false" aria-haspopup="true">
                <span class="user-icon" aria-hidden="true">👤</span>
                <span class="user-email"><%= nav_html(loggedUser.getEmail())%></span>
            </button>
            <div class="user-dropdown" role="menu" hidden>
                <a href="<%= request.getContextPath()%>/profile" role="menuitem">Edit profile</a>
                <a href="<%= request.getContextPath()%>/auth?action=logout" role="menuitem">Logout</a>
            </div>
        </div>
        <%
            }
        %>
    </div>
    <button class="theme-toggle" type="button" data-theme-toggle aria-label="Switch to dark mode" aria-pressed="false">
        <span class="theme-icon theme-icon-moon" aria-hidden="true">?</span>
        <span class="theme-icon theme-icon-sun" aria-hidden="true">?</span>
    </button>
</nav>
<script>
    (function () {
        function closeAll() {
            document.querySelectorAll('.user-dropdown').forEach(function (d) { d.hidden = true; });
            document.querySelectorAll('.user-button').forEach(function (b) { b.setAttribute('aria-expanded', 'false'); });
        }

        document.addEventListener('click', function (e) {
            var btn = e.target.closest && e.target.closest('.user-button');
            if (btn) {
                var menu = btn.parentNode.querySelector('.user-dropdown');
                var isOpen = menu && !menu.hidden;
                // close other menus first
                closeAll();
                if (!isOpen && menu) {
                    menu.hidden = false;
                    btn.setAttribute('aria-expanded', 'true');
                }
                // keep focus on button
                e.preventDefault();
                return;
            }

            // clicking outside any user-menu closes all
            if (!e.target.closest || !e.target.closest('.user-menu')) {
                closeAll();
            }
        });

        // Close on Escape
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' || e.key === 'Esc') {
                closeAll();
            }
        });
    })();
</script>