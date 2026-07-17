<%@page import="com.mycompany.techstore.Models.Objects.Product"%>
<%@page import="com.mycompany.techstore.Models.Objects.Order"%>
<%@page import="com.mycompany.techstore.Models.Objects.Review"%>
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
%>
<%
    Order order = (Order) request.getAttribute("order");
    Product product = (Product) request.getAttribute("product");
    Review review = (Review) request.getAttribute("review");
    String error = (String) request.getAttribute("error");
    Integer submittedRating = (Integer) request.getAttribute("submittedRating");
    String submittedComment = (String) request.getAttribute("submittedComment");
    if (order == null || product == null) {
        response.sendRedirect(request.getContextPath() + "/order-history");
        return;
    }
    int selectedRating = submittedRating != null ? submittedRating : (review != null ? review.getRating() : 0);
    String selectedComment = submittedComment != null ? submittedComment : (review != null ? review.getComment() : "");
%>
<!DOCTYPE html>
<html>
    <head>
        <title><%= review != null ? "Edit Review" : "Write Review"%> - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
        <style>
            .review-form-wrap{max-width:880px;margin:40px auto;padding:24px}
            .review-form-card{background:#fff;border-radius:18px;box-shadow:0 20px 50px rgba(15,23,42,.08);padding:28px}
            .rating-stars{display:flex;flex-direction:row-reverse;justify-content:flex-end;gap:8px}
            .rating-stars input{display:none}
            .rating-stars label{font-size:2rem;color:#d1d5db;cursor:pointer;transition:transform .15s ease,color .15s ease}
            .rating-stars input:checked ~ label,
            .rating-stars label:hover,
            .rating-stars label:hover ~ label{color:#f59e0b;transform:translateY(-1px)}
            .review-textarea{width:100%;min-height:160px;border:1px solid #d1d5db;border-radius:14px;padding:14px;font:inherit;resize:vertical}
            .review-actions{display:flex;gap:12px;justify-content:flex-end;margin-top:18px}
            .review-actions a,.review-actions button{border:none;border-radius:12px;padding:12px 18px;font-weight:700;text-decoration:none}
            .review-actions a{background:#e5e7eb;color:#111827}
            .review-actions button{background:#2563eb;color:#fff}
            .review-meta{color:#6b7280;margin-bottom:16px}
            .alert-error{background:#fef2f2;color:#991b1b;padding:12px 14px;border-radius:12px;margin-bottom:16px}
        </style>
    </head>
    <body>
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <div class="review-form-wrap">
            <div class="review-form-card">
                <p class="review-meta">Order #<%= order.getOrderId()%> - <%= product.getName()%></p>
                <h2><%= review != null ? "Edit your review" : "Write a review"%></h2>
                <p class="review-meta">You can review this item only once per order, and update your own review later.</p>
                <% if (error != null && !error.isBlank()) { %>
                <div class="alert-error"><%= error %></div>
                <% } %>
                <form action="<%= request.getContextPath()%>/review" method="post">
                    <input type="hidden" name="orderId" value="<%= order.getOrderId()%>">
                    <input type="hidden" name="productId" value="<%= product.getId()%>">
                    <div style="margin-bottom:18px;">
                        <label style="display:block;font-weight:700;margin-bottom:10px;">Rating</label>
                        <div class="rating-stars" aria-label="Star rating">
                            <% for (int star = 5; star >= 1; star--) { %>
                            <input type="radio" id="star-<%= star %>" name="rating" value="<%= star %>" <%= selectedRating == star ? "checked" : "" %>>
                            <label for="star-<%= star %>" title="<%= star %> stars">&#9733;</label>
                            <% } %>
                        </div>
                    </div>
                    <div style="margin-bottom:18px;">
                        <label for="comment" style="display:block;font-weight:700;margin-bottom:10px;">Comment</label>
                        <textarea id="comment" name="comment" class="review-textarea" placeholder="Share your experience with this product"><%= html(selectedComment) %></textarea>
                    </div>
                    <div class="review-actions">
                        <a href="<%= request.getContextPath()%>/order-detail?id=<%= order.getOrderId()%>">Cancel</a>
                        <button type="submit"><%= review != null ? "Update Review" : "Submit Review"%></button>
                    </div>
                </form>
            </div>
        </div>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
