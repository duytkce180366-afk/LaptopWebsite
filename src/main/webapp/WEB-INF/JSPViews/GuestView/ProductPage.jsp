<%@page import="Models.Objects.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Product product = (Product) request.getAttribute("product");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title><%= product != null ? product.getName() : "Product Detail"%> - TechHub Computer Store</title>
        <%@include file="/WEB-INF/JSPViews/global/htmlHead.jsp" %>
    </head>
    <body>
        <noscript>You need to enable JavaScript to run this website.</noscript>
        <main class="app-shell" id="app" data-page="product" data-product-id="<%= product != null ? product.getId() : 1%>"></main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
        <%@include file="/WEB-INF/JSPViews/global/htmlScripts.jsp" %>
    </body>
</html>
