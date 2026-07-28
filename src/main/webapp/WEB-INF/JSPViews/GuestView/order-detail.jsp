<%@page import="com.mycompany.techstore.Models.Objects.User"%>
<%@page import="com.mycompany.techstore.Models.Objects.OrderDetail"%>
<%@page import="com.mycompany.techstore.Models.Objects.Order"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    User currentUser = (User) session.getAttribute("loggedUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        return;
    }
    Order order = (Order) request.getAttribute("order");
    List<OrderDetail> details = (List<OrderDetail>) request.getAttribute("details");
    if (order == null) {
        response.sendRedirect(request.getContextPath() + "/order-history");
        return;
    }
    String status = order.getOrderStatus();
    java.util.List<Integer> reviewedProductIds
            = (java.util.List<Integer>) request.getAttribute("reviewedProductIds");
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Order #<%=order.getOrderId()%> - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/OrderDetail.css">
    </head>
    <body>

        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>

        <div class="detail-wrapper">

            <div class="breadcrumb-bar">
                <a href="home">Home</a>
                <span class="breadcrumb-sep">›</span>
                <a href="order-history">My Orders</a>
                <span class="breadcrumb-sep">›</span>
                <span>Order #<%=order.getOrderId()%></span>
            </div>

            <div class="page-header">
                <div class="page-header-left">
                    <div class="page-header-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                             viewBox="0 0 24 24" fill="none" stroke="#fff"
                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/>
                        <rect x="9" y="3" width="6" height="4" rx="1"/>
                        <path d="M9 12h6M9 16h4"/>
                        </svg>
                    </div>
                    <div>
                        <h2>Order #<%=order.getOrderId()%></h2>
                        <p>Placed on <%=order.getCreatedAt()%></p>
                    </div>
                </div>
                <a href="order-history" class="btn-back">← Back to Orders</a>
            </div>

            <div class="card">
                <div class="card-header">
                    <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15"
                         viewBox="0 0 24 24" fill="none" stroke="#6b7280"
                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <path d="M12 8v4l3 3"/>
                    </svg>
                    <h3>Order Information</h3>
                </div>
                <div class="card-body">
                    <div class="info-grid">

                        <div class="info-item">
                            <label>Status</label>
                            <% if ("Pending".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-pending">Pending</span>
                            <% } else if ("Confirmed".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-confirmed">Confirmed</span>
                            <% } else if ("Shipping".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-shipping">Shipping</span>
                            <% } else if ("Delivered".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-delivered">Delivered</span>
                            <% } else if ("Completed".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-completed">Completed</span>
                            <% } else if ("Return Requested".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-return">Return Requested</span>
                            <% } else if ("Returned".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-returned">Returned</span>
                            <% } else if ("Return Rejected".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-return-rejected">Return Rejected</span>
                            <% } else if ("Payment Failed".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-payment-failed">Payment Failed</span>
                            <% } else {%>
                            <span class="badge-status badge-cancelled"
                                  title="<%=order.getNote() != null ? "Reason: " + order.getNote() : ""%>">
                                Cancelled
                            </span>
                            <% }%>
                        </div>

                        <div class="info-item">
                            <label>Payment Method</label>
                            <span><%=order.getPaymentMethod() != null ? order.getPaymentMethod() : "-"%></span>
                        </div>

                        <div class="info-item">
                            <label>Delivery Address</label>
                            <span id="deliveryAddress"><%=order.getAddressInfo() != null ? order.getAddressInfo() : "-"%></span>
                        </div>

                        <div class="info-item">
                            <label>Phone</label>
                            <span><%=order.getPhone() != null ? order.getPhone() : "-"%></span>
                        </div>

                    </div>



                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15"
                         viewBox="0 0 24 24" fill="none" stroke="#6b7280"
                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="3" width="20" height="14" rx="2"/>
                    <path d="M8 21h8M12 17v4"/>
                    </svg>
                    <h3>Products (<%=details.size()%> item<%=details.size() != 1 ? "s" : ""%>)</h3>
                </div>
                <div class="card-body">
                    <%
                        java.util.Map<Integer, com.mycompany.techstore.Models.Objects.Review> reviewMap
                                = (java.util.Map<Integer, com.mycompany.techstore.Models.Objects.Review>) request.getAttribute("reviewMap");
                    %>
                    <% for (OrderDetail d : details) { %>

                    <div class="product-row">
                        <% if (d.getThumbnail() != null && !d.getThumbnail().isEmpty()) {%>
                        <img src="<%=d.getThumbnail()%>"
                             alt="<%=d.getProductName()%>"
                             class="product-thumb"
                             onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">
                        <div class="product-thumb-placeholder" style="display:none">&#128421;</div>
                        <% } else { %>
                        <div class="product-thumb-placeholder">&#128421;</div>
                        <% }%>

                        <div class="product-info">
                            <p class="product-name"><%=d.getProductName()%></p>
                            <span class="product-sku">SKU: <%=d.getSku()%></span>
                        </div>
                        <div class="product-qty">x<%=d.getQuantity()%></div>
                        <div class="product-price"><%=String.format("%,.0f", d.getUnitPrice())%> d</div>
                        <div class="product-subtotal"><%=String.format("%,.0f", d.getSubtotal())%> d</div>
                    </div>
                    <%
                        com.mycompany.techstore.Models.Objects.Review existingReview
                                = (reviewMap != null) ? reviewMap.get(d.getProductId()) : null;
                        boolean isReviewed = reviewedProductIds != null && reviewedProductIds.contains(d.getProductId());
                        boolean canReview = "Completed".equalsIgnoreCase(status) || "Return Requested".equalsIgnoreCase(status);
                    %>

                    <% if (existingReview != null) { %>
                    <div class="review-display">
                        <div class="review-stars">
                            <% for (int i = 1; i <= 5; i++) {%>
                            <span style="color: <%= i <= existingReview.getRating() ? "#f59e0b" : "#d1d5db"%>; font-size:18px;">&#9733;</span>
                            <% }%>
                        </div>
                        <p style="font-size:13px; color:#374151; margin:6px 0 0;"><%=existingReview.getComment()%></p>
                    </div>
                    <% } else if (canReview && !isReviewed) {%>
                    <a href="<%=request.getContextPath()%>/review?orderId=<%=order.getOrderId()%>&productId=<%=d.getProductId()%>"
                       class="btn-write-review">
                        Write Review
                    </a>
                    <% } %>

                    <% }%>

                    <div class="order-summary">
                        <div class="summary-row">
                            <span style="color:#6b7280;">Subtotal</span>
                            <span style="color:#ef4444; font-weight:600;"><%=String.format("%,.0f", order.getTotalAmount())%> d</span>
                        </div>
                        <% if (order.getShippingFee() > 0) {%>
                        <div class="summary-row">
                            <span style="color:#6b7280;">Shipping Fee</span>
                            <span style="color:#ef4444; font-weight:600;"><%=String.format("%,.0f", order.getShippingFee())%> d</span>
                        </div>
                        <% } %>
                        <% if (order.getDiscountAmount() > 0) { %>
                        <div class="summary-row">
                            <span style="color:#6b7280;">Discount
                                <% if (order.getVoucherCode() != null) {%>
                                (<%=order.getVoucherCode()%>)
                                <% }%>
                            </span>
                            <span style="color:#ef4444; font-weight:600;">-<%=String.format("%,.0f", order.getDiscountAmount())%> d</span>
                        </div>
                        <% }%>
                        <div class="summary-total">
                            <span style="font-weight:700;">Total</span>
                            <span style="color:#1a56db; font-weight:700; font-size:18px;"><%=String.format("%,.0f", order.getFinalTotal())%> d</span>
                        </div>
                    </div>

                </div>
            </div>

        </div>



        <script>
            async function resolveAddress() {
                var el = document.getElementById('deliveryAddress');
                if (!el)
                    return;
                var raw = el.textContent.trim();
                var parts = raw.split(',').map(function (s) {
                    return s.trim();
                });
                if (parts.length < 3)
                    return;

                var address = parts[0];
                var distCode = parts[1];
                var provCode = parts[2];

                if (isNaN(distCode) || isNaN(provCode))
                    return;

                try {
                    var provRes = await fetch('https://provinces.open-api.vn/api/p/' + provCode);
                    var provData = await provRes.json();
                    var distRes = await fetch('https://provinces.open-api.vn/api/d/' + distCode);
                    var distData = await distRes.json();
                    el.textContent = address + ', ' + distData.name + ', ' + provData.name;
                } catch (e) {
                    // Keep original text if API call fails
                }
            }
            resolveAddress();

            var selectedReturnReason = '';


            function openReturnModal(orderId) {
                document.getElementById('returnOrderId').value = orderId;
                selectedReturnReason = '';
                document.querySelectorAll('.reason-item').forEach(function (el) {
                    el.classList.remove('selected');
                    el.querySelector('input').checked = false;
                });
                document.getElementById('returnOtherNoteBox').classList.remove('visible');
                document.getElementById('returnOtherNoteText').value = '';
                document.getElementById('confirmReturnBtn').disabled = true;
                document.getElementById('returnModalOverlay').classList.add('active');
            }

            function closeReturnModal() {
                document.getElementById('returnModalOverlay').classList.remove('active');
            }

            function selectReturnReason(el, value) {
                document.querySelectorAll('.reason-item').forEach(function (item) {
                    item.classList.remove('selected');
                });
                el.classList.add('selected');
                el.querySelector('input').checked = true;
                selectedReturnReason = value;
                var noteBox = document.getElementById('returnOtherNoteBox');
                if (value === 'other') {
                    noteBox.classList.add('visible');
                    document.getElementById('returnOtherNoteText').focus();
                    document.getElementById('confirmReturnBtn').disabled = true;
                    document.getElementById('returnOtherNoteText').oninput = function () {
                        document.getElementById('confirmReturnBtn').disabled = this.value.trim().length === 0;
                    };
                } else {
                    noteBox.classList.remove('visible');
                    document.getElementById('confirmReturnBtn').disabled = false;
                }
            }

            function submitReturn() {
                var reason = selectedReturnReason === 'other'
                        ? document.getElementById('returnOtherNoteText').value.trim()
                        : selectedReturnReason;
                document.getElementById('returnReasonHidden').value = reason;
                document.getElementById('returnForm').submit();
            }

            var returnOverlay = document.getElementById('returnModalOverlay');
            if (returnOverlay) {
                returnOverlay.addEventListener('click', function (e) {
                    if (e.target === this)
                        closeReturnModal();
                });
            }
        </script>

    </body>
</html>
