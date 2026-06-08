<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Shopping Cart - Tech Store</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/css/Cart.css">
    </head>

    <body id="top">

        <main class="app-shell">

            <nav class="topbar">

                <a class="brand-button"
                   href="${pageContext.request.contextPath}/home">

                    <span class="brand-mark">
                        Tech Store
                    </span>

                    <span class="brand-subtitle">
                        Computer Store
                    </span>

                </a>

                <div class="nav-links">

                    <a href="${pageContext.request.contextPath}/home#home">
                        Home
                    </a>

                    <a href="${pageContext.request.contextPath}/home#products">
                        Products
                    </a>

                </div>

            </nav>

            <section class="cart-section">

                <div class="section-heading">

                    <p class="eyebrow">
                        Shopping Cart
                    </p>

                    <h2>
                        Your Cart
                    </h2>

                </div>

                <!-- TABLE HERE -->
                <table class="cart-table">

                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                            <th>Action</th>
                        </tr>
                    </thead>

                    <tbody>

                        <c:forEach items="${cartItems}" var="item">

                            <tr>

                                <td>
                                    ${item.productName}
                                </td>

                                <td class="cart-price">
                                    ${item.unitPrice}
                                </td>

                                <td>
                                    ${item.quantity}
                                </td>

                                <td class="cart-subtotal">
                                    ${item.subtotal}
                                </td>

                                <td>

                                    <div class="cart-actions">

                                        <form action="${pageContext.request.contextPath}/cart/update"
                                              method="post">

                                            <input type="hidden"
                                                   name="cartItemId"
                                                   value="${item.cartItemId}"/>

                                            <input class="qty-input"
                                                   type="number"
                                                   name="quantity"
                                                   value="${item.quantity}"
                                                   min="1"/>

                                            <button class="btn-update"
                                                    type="submit">
                                                Update
                                            </button>

                                        </form>

                                        <form action="${pageContext.request.contextPath}/cart/delete"
                                              method="post">

                                            <input type="hidden"
                                                   name="cartItemId"
                                                   value="${item.cartItemId}"/>

                                            <button class="btn-remove"
                                                    type="submit">
                                                Remove
                                            </button>

                                        </form>

                                    </div>

                                </td>

                            </tr>

                        </c:forEach>

                    </tbody>

                </table>
            </section>

        </main>

        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>

    </body>
</html>