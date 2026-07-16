<%@page import="com.mycompany.techstore.Models.Objects.User"%>
<%@page import="com.mycompany.techstore.Models.Objects.Order"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    User currentUser = (User) session.getAttribute("loggedUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        return;
    }
    List<Order> orders = (List<Order>) request.getAttribute("orders");
%>

<!DOCTYPE html>
<html>
    <head>
        <title>My Orders - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/OrderHistory.css">
    </head>
    <body>

        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>

        <div class="orders-wrapper">

            <div class="breadcrumb-bar">
                <a href="home">Home</a>
                <span class="breadcrumb-sep">&#8250;</span>
                <span>My Orders</span>
            </div>

            <div class="page-header">
                <div class="page-header-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                         viewBox="0 0 24 24" fill="none" stroke="#fff"
                         stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M5 7h14l-1.5 9H6.5L5 7z"/>
                    <path d="M5 7L4 3H2"/>
                    <circle cx="9" cy="20" r="1"/><circle cx="15" cy="20" r="1"/>
                    </svg>
                </div>
                <div>
                    <h2>My Orders</h2>
                    <p>Track and manage your purchases</p>
                </div>
            </div>

            <div class="filter-tabs">
                <button class="filter-tab active" onclick="filterOrders('all', this)">All</button>
                <button class="filter-tab" onclick="filterOrders('pending', this)">Pending</button>
                <button class="filter-tab" onclick="filterOrders('delivered', this)">Delivered</button>
                <button class="filter-tab" onclick="filterOrders('cancelled', this)">Cancelled</button>
            </div>

            <div class="orders-card">

                <% if (orders == null || orders.isEmpty()) { %>
                <div class="empty-state">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                         fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="2" y="3" width="20" height="14" rx="2"/>
                    <path d="M8 21h8M12 17v4"/>
                    </svg>
                    <h4>No orders yet</h4>
                    <p>Your order history will appear here.</p>
                    <a href="home">Browse Products</a>
                </div>

                <% } else { %>

                <table class="orders-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Status</th>
                            <th>Date</th>
                            <th>Action</th>
                            <th style="text-align:right;">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Order o : orders) {
                                String status = o.getOrderStatus();
                        %>
                        <tr data-status="<%=status.toLowerCase()%>">
                            <td>
                                <a href="order-detail?id=<%=o.getOrderId()%>" class="order-id">
                                    #<%=o.getOrderId()%>
                                </a>
                            </td>
                            <td>
                                <% if ("Pending".equalsIgnoreCase(status)) { %>
                                <span class="badge-status badge-pending">Pending</span>
                                <% } else if ("Completed".equalsIgnoreCase(status)) { %>
                                <span class="badge-status badge-completed">Completed</span>

                                <% } else if ("Payment Failed".equalsIgnoreCase(status)) { %>
                                <span class="badge-status badge-payment-failed">Payment Failed</span>
                                <% } else { %>
                                <span class="badge-status badge-cancelled">Cancelled</span>
                                <% } %>
                            </td>
                            <td>
                                <%
                                    String dateStr = "-";
                                    if (o.getCreatedAt() != null) {
                                        java.text.SimpleDateFormat sdf
                                                = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");

                                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

                                        dateStr = sdf.format(o.getCreatedAt());
                                    }
                                %>
                                <span class="order-date"><%=dateStr%></span>
                            </td>
                            <td>
                                <% if ("Pending".equalsIgnoreCase(status)) {%>
                                <button type="button" class="btn-cancel-open"
                                        onclick="openCancelModal('<%=o.getOrderId()%>')">
                                    &#10005; Cancel Order
                                </button>
                                <% } else if ("Payment Failed".equalsIgnoreCase(status)) {%>
                                <a href="<%=request.getContextPath()%>/checkout" class="btn-retry-checkout">
                                    &#8635; Retry Checkout
                                </a>
                                <% } else { %>
                                <span class="no-action">&#8212; No Action</span>
                                <% } %>
                            </td>
                            <td style="text-align:right;">
                                <% if (o.getFinalTotal() == 0) { %>
                                <span style="color:#9ca3af; font-size:13px;">-</span>
                                <% } else {%>
                                <span class="order-amount">
                                    <%=String.format("%,.0f", o.getFinalTotal())%> &#8363;
                                </span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>

                <table style="width:100%; border-top:2px solid #e5e7eb;">
                    <tr>
                        <td colspan="4" style="padding:14px 16px; font-size:13px; font-weight:600; color:#6b7280; text-align:right;">
                            Total amount
                        </td>
                        <td id="totalAmountValue" style="padding:14px 16px; font-size:16px; font-weight:700; color:#1a56db; text-align:right;">
                            <%
                                double grandTotal = 0;
                                if (orders != null) {
                                    for (Order o2 : orders) {
                                        grandTotal += o2.getFinalTotal();
                                    }
                                }
                            %>
                            <%=String.format("%,.0f", grandTotal)%> &#8363;
                        </td>
                    </tr>
                </table>

                <% }%>
            </div>
        </div>

        <!-- Cancel Modal -->
        <div class="cancel-modal-overlay" id="cancelModalOverlay">
            <div class="cancel-modal">
                <div class="cancel-modal-header">
                    <div class="modal-icon">&#128683;</div>
                    <h3>Cancel Order</h3>
                    <button class="modal-close-btn" onclick="closeCancelModal()">&#10005;</button>
                </div>
                <div class="cancel-modal-body">
                    <p>Please select a reason for cancellation:</p>
                    <div class="reason-list">
                        <label class="reason-item" onclick="selectReason(this, 'I changed my mind')">
                            <input type="radio" name="cancelReason">
                            <span class="reason-dot"></span>
                            I changed my mind
                        </label>
                        <label class="reason-item" onclick="selectReason(this, 'Found a better price elsewhere')">
                            <input type="radio" name="cancelReason">
                            <span class="reason-dot"></span>
                            Found a better price elsewhere
                        </label>
                        <label class="reason-item" onclick="selectReason(this, 'Ordered by mistake')">
                            <input type="radio" name="cancelReason">
                            <span class="reason-dot"></span>
                            Ordered by mistake
                        </label>
                        <label class="reason-item" onclick="selectReason(this, 'Delivery time too long')">
                            <input type="radio" name="cancelReason">
                            <span class="reason-dot"></span>
                            Delivery time too long
                        </label>
                        <label class="reason-item" onclick="selectReason(this, 'other')">
                            <input type="radio" name="cancelReason">
                            <span class="reason-dot"></span>
                            Other reason
                        </label>
                    </div>
                    <div class="other-note-box" id="otherNoteBox">
                        <textarea id="otherNoteText" rows="3"
                                  placeholder="Please describe your reason..."></textarea>
                    </div>
                </div>
                <div class="cancel-modal-footer">
                    <button class="btn-modal-back" onclick="closeCancelModal()">Go Back</button>
                    <form id="cancelForm" action="cancel-order" method="post" style="display:inline;">
                        <input type="hidden" name="id" id="cancelOrderId">
                        <input type="hidden" name="note" id="cancelNoteHidden">
                        <button type="button" class="btn-modal-confirm"
                                id="confirmCancelBtn" disabled
                                onclick="submitCancel()">
                            Confirm Cancel
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            var selectedReason = '';

            function openCancelModal(orderId) {
                document.getElementById('cancelOrderId').value = orderId;
                selectedReason = '';
                document.querySelectorAll('.reason-item').forEach(function (el) {
                    el.classList.remove('selected');
                    el.querySelector('input').checked = false;
                });
                document.getElementById('otherNoteBox').classList.remove('visible');
                document.getElementById('otherNoteText').value = '';
                document.getElementById('confirmCancelBtn').disabled = true;
                document.getElementById('cancelModalOverlay').classList.add('active');
            }

            function closeCancelModal() {
                document.getElementById('cancelModalOverlay').classList.remove('active');
            }

            function selectReason(el, value) {
                document.querySelectorAll('.reason-item').forEach(function (item) {
                    item.classList.remove('selected');
                });
                el.classList.add('selected');
                el.querySelector('input').checked = true;
                selectedReason = value;
                var noteBox = document.getElementById('otherNoteBox');
                if (value === 'other') {
                    noteBox.classList.add('visible');
                    document.getElementById('otherNoteText').focus();
                    document.getElementById('confirmCancelBtn').disabled = true;
                    document.getElementById('otherNoteText').oninput = function () {
                        document.getElementById('confirmCancelBtn').disabled = this.value.trim().length === 0;
                    };
                } else {
                    noteBox.classList.remove('visible');
                    document.getElementById('confirmCancelBtn').disabled = false;
                }
            }

            function submitCancel() {
                var note = selectedReason === 'other'
                        ? document.getElementById('otherNoteText').value.trim()
                        : selectedReason;
                document.getElementById('cancelNoteHidden').value = note;
                document.getElementById('cancelForm').submit();
            }

            document.getElementById('cancelModalOverlay').addEventListener('click', function (e) {
                if (e.target === this)
                    closeCancelModal();
            });

            function filterOrders(status, btn) {
                document.querySelectorAll('.filter-tab').forEach(function (t) {
                    t.classList.remove('active');
                });
                btn.classList.add('active');

                var total = 0;
                document.querySelectorAll('.orders-table tbody tr').forEach(function (row) {
                    if (status === 'all' || row.dataset.status === status) {
                        row.style.display = '';
                        var amountEl = row.querySelector('.order-amount');
                        if (amountEl) {
                            var raw = amountEl.textContent.replace(/[^0-9]/g, '');
                            total += parseInt(raw) || 0;
                        }
                    } else {
                        row.style.display = 'none';
                    }
                });

                var totalEl = document.getElementById('totalAmountValue');
                if (totalEl) {
                    totalEl.textContent = total.toLocaleString('vi-VN') + ' d';
                }
            }
        </script>

    </body>
</html>
