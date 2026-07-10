
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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Checkout.css">
    </head>
    <body>
        <c:if test="${empty cartItems}">
            <script>window.location = "${pageContext.request.contextPath}/cart";</script>
        </c:if>

        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <c:if test="${not empty orderError}">
            <div class="checkout-alert-error">
                ${orderError}
            </div>
        </c:if>
        <%    session.removeAttribute("orderError");
        %>
        <form id="codForm"
              action="${pageContext.request.contextPath}/place-order"
              method="post">

            <div class="checkout-container">

                <!-- LEFT -->
                <div class="checkout-card">
                    <h2>Checkout</h2>
                    <h3>Customer Information</h3>

                    <div class="form-group">
                        <label>Name</label>
                        <input type="text" name="name" value="${user.full_name}" required>
                    </div>

                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" value="${user.email}" required>
                    </div>

                    <h3>Shipping Address</h3>

                    <div class="default-address">

                        <div class="address-name">
                            ${user.full_name}
                        </div>

                        <div class="address-phone">
                            ${defaultAddress.phone}
                        </div>

                        <div class="address-detail">
                            ${defaultAddress.homeAddress},
                            ${defaultAddress.ward},
                            ${defaultAddress.province}
                        </div>

                        <a href="${pageContext.request.contextPath}/profile?action=add_address">
                            Change address
                        </a>

                    </div>
                    <input type="hidden"
                           name="phone"
                           value="${defaultAddress.phone}">

                    <input type="hidden"
                           name="province"
                           value="${defaultAddress.province}">

                    <input type="hidden"
                           name="district"
                           value="${defaultAddress.ward}">

                    <input type="hidden"
                           name="address"
                           value="${defaultAddress.homeAddress}">
                    
                    <div class="form-group">
                        <label>Payment Method</label>
                        <div class="payment-options">
                            <label>
                                <input type="radio" name="paymentMethod" value="COD" checked>
                                Cash On Delivery (COD)
                            </label>
                            <label>
                                <input type="radio" name="paymentMethod" value="VNPAY">
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
                                    <div class="item-name">${item.productName}</div>
                                    <div class="item-qty">Quantity: ${item.quantity}</div>
                                </div>
                                <div class="item-price">
                                    <fmt:formatNumber value="${item.subtotal}" pattern="#,###" /> đ
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="voucher-box">
                        <label>Voucher</label>
                        <div class="voucher-row">
                            <input type="text" id="voucherCode" placeholder="Enter voucher code">
                            <button type="button" id="applyVoucherBtn" class="btn-apply">Apply</button>
                        </div>
                        <div class="voucher-alert-error" id="voucherErrorBox" style="display:none;">
                            <span class="alert-icon">⚠</span>
                            <span class="alert-text" id="voucherErrorText"></span>
                        </div>
                    </div>

                    <input type="hidden" name="voucherId"      id="hiddenVoucherId"      value="0">
                    <input type="hidden" name="discountAmount" id="hiddenDiscountAmount" value="0">
                    <input type="hidden" name="finalAmount"    id="hiddenFinalAmount"    value="${cartTotal}">

                    <div class="summary-footer">
                        <div class="total-row">
                            <span>Total</span>
                            <strong>${cartTotalFormatted}</strong>
                        </div>
                        <div class="total-row">
                            <span>Discount</span>
                            <strong id="discountAmount">- 0 đ</strong>
                        </div>
                        <div class="total-row final-row">
                            <span>Final Total</span>
                            <strong id="finalTotal">${cartTotalFormatted}</strong>
                        </div>

                        <button type="button" class="btn-place-order" id="placeOrderBtn">
                            Place Order
                        </button>
                    </div>
                </div>

            </div>
        </form>

        <!-- Form ẩn dành riêng cho VNPay -->
        <form id="vnpayForm"
              action="${pageContext.request.contextPath}/vnpay-pay"
              method="post"
              style="display:none;">
            <input type="hidden" name="phone"          id="vp_phone">
            <input type="hidden" name="province"       id="vp_province">
            <input type="hidden" name="district"       id="vp_district">
            <input type="hidden" name="address"        id="vp_address">
            <input type="hidden" name="amount"         id="vp_amount">
            <input type="hidden" name="voucherId"      id="vp_voucherId">
            <input type="hidden" name="discountAmount" id="vp_discountAmount">
            <input type="hidden" name="paymentMethod" value="VNPay" id="vp_paymentMethod">
        </form>

        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>

        <script>
            var currentFinalAmount = ${cartTotal};

            document.getElementById("applyVoucherBtn").addEventListener("click", function () {
                var code = document.getElementById("voucherCode").value;
                var errorBox = document.getElementById("voucherErrorBox");
                var errorText = document.getElementById("voucherErrorText");

                // Hide any previous error before trying again
                errorBox.style.display = "none";

                fetch("${pageContext.request.contextPath}/apply-voucher", {
                    method: "POST",
                    headers: {"Content-Type": "application/x-www-form-urlencoded"},
                    body: "voucherCode=" + encodeURIComponent(code)
                })
                        .then(function (r) {
                            return r.json();
                        })
                        .then(function (data) {
                            if (!data.success) {
                                errorText.innerText = data.message;
                                errorBox.style.display = "flex";
                                return;
                            }
                            document.getElementById("discountAmount").innerText =
                                    "- " + data.discount.toLocaleString("vi-VN") + " đ";
                            document.getElementById("finalTotal").innerText =
                                    data.finalTotal.toLocaleString("vi-VN") + " đ";
                            document.getElementById("hiddenVoucherId").value = data.voucherId || 0;
                            document.getElementById("hiddenDiscountAmount").value = data.discount || 0;
                            document.getElementById("hiddenFinalAmount").value = data.finalTotal || currentFinalAmount;
                            currentFinalAmount = data.finalTotal || currentFinalAmount;
                        })
                        .catch(function () {
                            errorText.innerText = "Voucher error, please try again.";
                            errorBox.style.display = "flex";
                        });

            });

            document.getElementById("placeOrderBtn").addEventListener("click", function () {
                var form = document.getElementById("codForm");
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                var method = document.querySelector('input[name="paymentMethod"]:checked').value;
                console.log("DEBUG method =", method);


                if (method === "VNPAY") {
                    document.getElementById("vp_phone").value = form.querySelector('[name="phone"]').value;
                    document.getElementById("vp_province").value = form.querySelector('[name="province"]').value;
                    document.getElementById("vp_district").value = form.querySelector('[name="district"]').value;
                    document.getElementById("vp_address").value = form.querySelector('[name="address"]').value;
                    document.getElementById("vp_amount").value = document.getElementById("hiddenFinalAmount").value;
                    document.getElementById("vp_voucherId").value = document.getElementById("hiddenVoucherId").value;
                    document.getElementById("vp_discountAmount").value = document.getElementById("hiddenDiscountAmount").value;
                    document.getElementById("vnpayForm").submit();
                } else {
                    form.submit();
                }
            });
        </script>
    </body>
</html>
