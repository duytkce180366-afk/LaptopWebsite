<%@page import="com.mycompany.techstore.Models.Objects.Category"%>
<%@page import="com.mycompany.techstore.Models.Objects.Product"%>
<%@page import="com.mycompany.techstore.Models.Objects.Review"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Map"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
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
%>
<%
    Product product = (Product) request.getAttribute("product");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    Double averageRating = (Double) request.getAttribute("averageRating");
    if (categories == null) {
        categories = new ArrayList<>();
    }
    if (reviews == null) {
        reviews = new ArrayList<>();
    }
    if (averageRating == null) {
        averageRating = 0.0;
    }
    int reviewCount = reviews.size();
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title><%= product != null ? html(product.getName()) : "Product Detail"%> - Tech Store</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <main class="app-shell" id="app">
            <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
            <% if (product == null) {%>
            <section class="empty-state">
                <h3>Product not found</h3>
                <p>The requested product is not available.</p>
                <a href="<%= contextPath%>/home">Back to products</a>
            </section>
            <% } else {%>
            <div class="product-page">
                <a class="back-button" href="<%= contextPath%>/home#products">Back to products</a>

                <section class="details-section" id="details" aria-labelledby="details-title">
                    <div class="section-heading">
                        <p class="eyebrow">View Product Details</p>
                        <h2 id="details-title"><%= html(product.getName())%></h2>
                    </div>

                    <div class="details-layout">
                        <img src="<%= html(product.getThumbnail())%>" alt="<%= html(product.getName())%> product view" />
                        <div class="details-content">
                            <div class="detail-summary">
                                <span><%= html(product.getBrand())%></span>
                                <span><%= html(product.getCategory())%></span>
                                <span><%= averageRating%> / 5 ★ (<%= reviewCount%> review<%= reviewCount == 1 ? "" : "s" %>)</span>
                            </div>
                            <p><%= html(product.getDescription())%></p>
                            <h3><%= formatPrice(product.getPrice())%></h3>

                            <form action="<%= request.getContextPath()%>/cart/add"
                                  method="post">

                                <input type="hidden"
                                       name="productId"
                                       value="<%= product.getId()%>"/>

                                <input type="hidden"
                                       name="price"
                                       value="<%= product.getPrice()%>"/>

                                <input type="number"
                                       name="quantity"
                                       value="1"
                                       min="1"
                                       <%= product.getStock() <= 0 ? "disabled" : "" %>/>

                                <button type="submit" <%= product.getStock() <= 0 ? "disabled style='background-color:#9ca3af;cursor:not-allowed;border-color:#9ca3af;'" : "" %>>
                                    <%= product.getStock() <= 0 ? "Out of Stock" : "Add To Cart" %>
                                </button>
                                <c:if test="${not empty sessionScope.cartError}">
                                    <div class="alert-stock">
                                        ${sessionScope.cartError}
                                    </div>

                                    <c:remove var="cartError"
                                              scope="session"/>
                                </c:if>
                            </form>
                            <dl class="spec-table">
                                <% for (Map.Entry<String, String> spec : product.getSpecs().entrySet()) {%>
                                <div>
                                    <dt><%= html(formatSpecLabel(spec.getKey()))%></dt>
                                    <dd><%= html(spec.getValue())%></dd>
                                </div>
                                <% }%>
                                <div>
                                    <dt>Warranty</dt>
                                    <dd><%= html(product.getWarranty())%></dd>
                                </div>
                                <div>
                                    <dt>Status</dt>
                                    <dd><%= product.getStock() > 0 ? product.getStock() + " in stock" : "Out of Stock"%></dd>
                                </div>
                            </dl>
                        </div>
                    </div>
                </section>

                <section class="reviews-section" id="reviews" aria-labelledby="reviews-title">
                    <div class="section-heading">
                        <p class="eyebrow">View Reviews</p>
                        <h2 id="reviews-title">Customer reviews for <%= html(product.getName())%> (<%= reviewCount%> review<%= reviewCount == 1 ? "" : "s" %>)</h2>
                    </div>
                    <div class="reviews-grid">
                        <% if (reviews.isEmpty()) {%>
                        <p>No reviews yet for this product.</p>
                        <% } else { %>
                        <% for (Review review : reviews) {%>
                        <article class="review-card">
                            <div>
                                <strong><%= html(review.getUser())%></strong>
                                <span><%= html(review.getDate())%></span>
                            </div>
                            <p class="stars" aria-label="<%= review.getRating()%> out of 5 ★">Rating: <%= review.getRating()%> / 5 ★</p>
                            <p><%= html(review.getComment())%></p>
                        </article>
                        <% } %>
                        <% } %>
                    </div>
                </section>
            </div>
            <% }%>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
