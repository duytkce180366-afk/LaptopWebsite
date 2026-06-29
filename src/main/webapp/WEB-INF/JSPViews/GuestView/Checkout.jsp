
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Check Out - Tech Store</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/css/Checkout.css">
    </head>

    <body>
        <c:if test="${empty cartItems}">
            <script>
                window.location =
                        "${pageContext.request.contextPath}/cart";
            </script>
        </c:if>
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>

        <form action="${pageContext.request.contextPath}/place-order"
              method="post">

            <div class="checkout-container">

                <!-- LEFT -->
                <div class="checkout-card">

                    <h2>Checkout</h2>

                    <h3>Customer Information</h3>

                    <div class="form-group">
                        <label>Name</label>
                        <input type="text"
                               name="name"
                               value="${user.full_name}"
                               required>

                    </div>

                    <div class="form-group">
                        <label>Email</label>
                        <input type="email"
                               name="email"
                               value="${user.email}"
                               required>

                    </div>

                    <div class="form-group">
                        <label>Phone</label>
                        <input type="text"
                               name="phone"
                               value="${user.phone}"
                               pattern="[0-9]{10,11}"
                               required>
                    </div>

                    <div class="form-group">
                        <label>Province</label>
                        <select id="province"
                                name="province"
                                required>
                        </select>                    
                    </div>

                    <div class="form-group">
                        <label>District</label>
                        <select id="district"
                                name="district"
                                required>
                        </select>                  
                    </div>

                    <div class="form-group">
                        <label>Address</label>
                        <textarea name="address"
                                  rows="3"
                                  required></textarea>
                    </div>
                    <div class="form-group">
                        <label>Payment Method</label>

                        <div class="payment-options">

                            <label>
                                <input type="radio"
                                       name="paymentMethod"
                                       value="COD"
                                       checked>
                                Cash On Delivery (COD)
                            </label>

                            <label>
                                <input type="radio"
                                       name="paymentMethod"
                                       value="VNPAY">
                                VNPay
                            </label>

                        </div>
                    </div>
                </div>

                <!-- RIGHT -->
                <div class="summary-card">

                    <h3>Order Summary</h3>
                    <div class="order-items">

                        <c:forEach items="${cartItems}" var="item">

                            <div class="order-item">

                                <div class="item-info">

                                    <div class="item-name">
                                        ${item.productName}
                                    </div>

                                    <div class="item-qty">
                                        Quantity: ${item.quantity}
                                    </div>

                                </div>

                                <div class="item-price">
                                    <fmt:formatNumber value="${item.subtotal}"
                                                      pattern="#,###" /> đ
                                </div>

                            </div>

                        </c:forEach>

                    </div>
                    <div class="voucher-box">

                        <label>Voucher</label>

                        <div class="voucher-row">

                            <input type="text"
                                   id="voucherCode"
                                   placeholder="Enter voucher code">
                            <button type="button"
                                    id="applyVoucherBtn"
                                    class="btn-apply">
                                Apply
                            </button>

                        </div>

                    </div>
                    <div class="summary-footer">

                        <div class="total-row">
                            <span>Total</span>
                            <strong>${cartTotalFormatted}</strong>
                        </div>

                        <div class="total-row">
                            <span>Discount</span>
                            <strong id="discountAmount">
                                - 0 đ
                            </strong>
                        </div>

                        <div class="total-row final-row">
                            <span>Final Total</span>
                            <strong id="finalTotal">
                                ${cartTotalFormatted}
                            </strong>
                        </div>

                        <button type="submit"
                                class="btn-place-order">
                            Place Order
                        </button>

                    </div> 

                </div>

        </form>

        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>

        <script>

            const province = document.getElementById("province");
            const district = document.getElementById("district");

            fetch("https://provinces.open-api.vn/api/p/")
                    .then(res => res.json())
                    .then(data => {

                        province.innerHTML =
                                '<option value="">Select Province</option>';

                        data.forEach(function (p) {

                            province.innerHTML +=
                                    "<option value='" + p.code + "'>"
                                    + p.name +
                                    "</option>";
                        });
                    });

            province.addEventListener("change", function () {

                fetch(
                        "https://provinces.open-api.vn/api/p/"
                        + this.value
                        + "?depth=2"
                        )
                        .then(res => res.json())
                        .then(data => {

                            district.innerHTML =
                                    '<option value="">Select District</option>';

                            data.districts.forEach(function (d) {

                                district.innerHTML +=
                                        "<option value='" + d.code + "'>"
                                        + d.name +
                                        "</option>";
                            });
                        });
            });

        </script>
        <script>

            document.getElementById("applyVoucherBtn")
                    .addEventListener("click", function () {

                        const code =
                                document.getElementById("voucherCode").value;

                        fetch(
                                "${pageContext.request.contextPath}/apply-voucher",
                                {
                                    method: "POST",

                                    headers: {
                                        "Content-Type":
                                                "application/x-www-form-urlencoded"
                                    },

                                    body:
                                            "voucherCode="
                                            + encodeURIComponent(code)
                                }
                        )

                                .then(response => response.json())

                                .then(data => {

                                    if (!data.success) {

                                        alert(data.message);

                                        return;
                                    }

                                    document.getElementById("discountAmount")
                                            .innerText =
                                            "- "
                                            + data.discount.toLocaleString("vi-VN")
                                            + " đ";

                                    document.getElementById("finalTotal")
                                            .innerText =
                                            data.finalTotal.toLocaleString("vi-VN")
                                            + " đ";
                                })

                                .catch(error => {

                                    console.log(error);

                                    alert("Voucher error");
                                });

                    });

        </script>
    </body>
</html>
