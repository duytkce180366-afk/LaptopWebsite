<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                <c:if test="${not empty sessionScope.cartError}">
                    <div class="alert-stock">
                        ${sessionScope.cartError}
                    </div>

                    <c:remove var="cartError"
                              scope="session"/>
                </c:if>
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
                                    <fmt:formatNumber value="${item.unitPrice}"
                                                      pattern="#,###"/> ₫
                                </td>

                                <td>
                                    ${item.quantity}
                                </td>

                                <td class="cart-subtotal">
                                    <fmt:formatNumber value="${item.subtotal}"
                                                      pattern="#,###"/> ₫
                                </td>


                                <td>

                                    <div class="cart-actions">

                                        <div class="qty-box">

                                            <form action="${pageContext.request.contextPath}/cart/update"
                                                  method="post">

                                                <input type="hidden"
                                                       name="cartItemId"
                                                       value="${item.cartItemId}"/>

                                                <button type="submit"
                                                        name="quantity"
                                                        value="${item.quantity - 1}">
                                                    -
                                                </button>

                                            </form>

                                            <span>${item.quantity}</span>

                                            <form action="${pageContext.request.contextPath}/cart/update"
                                                  method="post">

                                                <input type="hidden"
                                                       name="cartItemId"
                                                       value="${item.cartItemId}"/>

                                                <button type="submit"
                                                        name="quantity"
                                                        value="${item.quantity + 1}">
                                                    +
                                                </button>

                                            </form>

                                        </div>


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
                <div class="cart-summary">

                    <div class="summary-box">

                        <h3>Total:
                            <span>
                                <fmt:formatNumber
                                    value="${cartTotal}"
                                    type="number"
                                    groupingUsed="true"/>
                                ₫
                            </span>
                        </h3>

                        <div class="summary-actions">

                            <a href="${pageContext.request.contextPath}/home"
                               class="btn-continue">
                                Continue Shopping
                            </a>

                            <a href="${pageContext.request.contextPath}/checkout"
                               class="btn-checkout">
                                Checkout
                            </a>

                        </div>

                    </div>

                </div>
            </section>

        </main>

        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>

    </body>
</html>