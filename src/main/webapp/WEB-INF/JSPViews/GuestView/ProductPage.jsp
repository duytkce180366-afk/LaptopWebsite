<%@page import="com.mycompany.techstore.Models.Objects.Category"%>
<%@page import="com.mycompany.techstore.Models.Objects.Product"%>
<%@page import="com.mycompany.techstore.Models.Objects.Review"%>
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
    if (categories == null) {
        categories = new ArrayList<>();
    }
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title><%= product != null ? html(product.getName()) : "Product Detail"%> - Tech Storee</title>
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
                                <span><%= product.getAverageRating()%> / 5 rating</span>
                            </div>
                            <p><%= html(product.getDescription())%></p>
                            <h3><%= formatPrice(product.getPrice())%></h3>
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
                        <h2 id="reviews-title">Customer reviews for <%= html(product.getName())%></h2>
                    </div>
                    <div class="reviews-grid">
                        <% for (Review review : product.getReviews()) {%>
                        <article class="review-card">
                            <div>
                                <strong><%= html(review.getUser())%></strong>
                                <span><%= html(review.getDate())%></span>
                            </div>
                            <p class="stars" aria-label="<%= review.getRating()%> out of 5 rating">Rating: <%= review.getRating()%>/5</p>
                            <p><%= html(review.getComment())%></p>
                        </article>
                        <% } %>
                    </div>
                </section>
            </div>
            <% }%>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
