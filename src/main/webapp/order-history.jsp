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
    <style>

        *, *::before, *::after { box-sizing: border-box; }

        body {
            background: #f3f4f6;
            font-family: 'Segoe UI', system-ui, sans-serif;
            color: #111827;
            margin: 0;
        }

        .orders-wrapper {
            max-width: 1200px;
            margin: 0 auto;
            padding: 36px 20px 60px;
        }

        .breadcrumb-bar {
            font-size: 12.5px;
            color: #6b7280;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .breadcrumb-bar a { color: #1a56db; text-decoration: none; }
        .breadcrumb-bar a:hover { text-decoration: underline; }
        .breadcrumb-sep { color: #d1d5db; }

        .page-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 20px;
        }
        .page-header-icon {
            width: 40px; height: 40px;
            background: #1a56db;
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
        }
        .page-header h2 {
            font-size: 20px;
            font-weight: 700;
            margin: 0;
            color: #111827;
        }
        .page-header p {
            font-size: 13px;
            color: #6b7280;
            margin: 2px 0 0;
        }

        .orders-card {
            background: #ffffff;
            border-radius: 12px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 1px 3px rgba(0,0,0,.06);
            overflow: hidden;
        }

        .orders-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13.5px;
        }
        .orders-table thead tr { background: #1e3a5f; }
        .orders-table th {
            color: #bfdbfe;
            font-weight: 600;
            font-size: 11px;
            letter-spacing: 0.07em;
            text-transform: uppercase;
            padding: 12px 16px;
            text-align: left;
            white-space: nowrap;
        }
        .orders-table tbody tr {
            border-bottom: 1px solid #f3f4f6;
            transition: background .12s;
        }
        .orders-table tbody tr:last-child { border-bottom: none; }
        .orders-table tbody tr:hover { background: #f9fafb; }
        .orders-table td {
            padding: 14px 16px;
            vertical-align: middle;
            color: #1f2937;
        }

        .order-id {
            font-weight: 700;
            color: #1a56db;
            font-size: 13px;
            font-family: 'Courier New', monospace;
            text-decoration: none;
        }
        .order-id:hover { text-decoration: underline; }

        .order-amount { font-weight: 700; color: #111827; }
        .order-date { color: #6b7280; font-size: 12.5px; }

        .badge-status {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            font-size: 11.5px;
            font-weight: 600;
            padding: 4px 10px;
            border-radius: 6px;
            white-space: nowrap;
        }
        .badge-status::before {
            content: '';
            width: 6px; height: 6px;
            border-radius: 50%;
            flex-shrink: 0;
        }
        .badge-pending   { background:transparent; color:#92400e; border:none; }
        .badge-pending::before   { background:#f59e0b; }
        .badge-shipping  { background:transparent; color:#1e40af; border:none; }
        .badge-shipping::before  { background:#3b82f6; }
        .badge-delivered { background:transparent; color:#166534; border:none; }
        .badge-delivered::before { background:#22c55e; }
        .badge-cancelled { background:transparent; color:#991b1b; border:none; }
        .badge-cancelled::before { background:#ef4444; }

        .btn-cancel-open {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            background: #fff;
            color: #dc2626;
            border: 1.5px solid #fca5a5;
            border-radius: 7px;
            padding: 6px 12px;
            font-size: 12px;
            font-weight: 600;
            cursor: pointer;
            white-space: nowrap;
            transition: background .15s, border-color .15s;
        }
        .btn-cancel-open:hover { background: #fef2f2; border-color: #ef4444; }

        .no-action { color: #d1d5db; font-size: 12px; }

        .filter-tabs {
            display: flex;
            gap: 4px;
            margin-bottom: 16px;
            background: #fff;
            border: 1px solid #e5e7eb;
            border-radius: 10px;
            padding: 4px;
            width: fit-content;
        }
        .filter-tab {
            padding: 7px 16px;
            font-size: 12.5px;
            font-weight: 600;
            border-radius: 7px;
            border: none;
            background: transparent;
            color: #6b7280;
            cursor: pointer;
            transition: background .15s, color .15s;
            white-space: nowrap;
        }
        .filter-tab:hover { background: #f3f4f6; color: #111827; }
        .filter-tab.active { background: #1e3a5f; color: #fff; }

        .empty-state {
            text-align: center;
            padding: 72px 20px;
            color: #9ca3af;
        }
        .empty-state svg { width: 52px; height: 52px; margin-bottom: 14px; opacity: .35; }
        .empty-state h4 { font-size: 15px; font-weight: 600; color: #374151; margin: 0 0 6px; }
        .empty-state p  { font-size: 13px; margin: 0; }
        .empty-state a  {
            display: inline-block;
            margin-top: 18px;
            background: #1a56db;
            color: #fff;
            border-radius: 8px;
            padding: 9px 22px;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
        }
        .empty-state a:hover { background: #1648c0; }

        .cancel-modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,.5);
            z-index: 1000;
            align-items: center;
            justify-content: center;
            padding: 16px;
        }
        .cancel-modal-overlay.active { display: flex; }

        .cancel-modal {
            background: #fff;
            border-radius: 14px;
            width: 440px;
            max-width: 100%;
            box-shadow: 0 20px 60px rgba(0,0,0,.2);
            overflow: hidden;
            animation: modalIn .16s ease;
        }
        @keyframes modalIn {
            from { transform: translateY(12px) scale(.97); opacity: 0; }
            to   { transform: translateY(0) scale(1); opacity: 1; }
        }

        .cancel-modal-header {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 18px 20px 16px;
            border-bottom: 1px solid #f3f4f6;
        }
        .modal-icon {
            width: 34px; height: 34px;
            background: #fef2f2;
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
            font-size: 16px;
            flex-shrink: 0;
        }
        .cancel-modal-header h3 {
            font-size: 15px; font-weight: 700;
            color: #111827; margin: 0; flex: 1;
        }
        .modal-close-btn {
            background: none; border: none; cursor: pointer;
            color: #9ca3af; width: 28px; height: 28px;
            border-radius: 6px; font-size: 15px;
            display: flex; align-items: center; justify-content: center;
            transition: color .15s, background .15s;
        }
        .modal-close-btn:hover { color: #111827; background: #f3f4f6; }

        .cancel-modal-body { padding: 18px 20px 4px; }
        .cancel-modal-body > p { font-size: 13px; color: #6b7280; margin: 0 0 14px; }

        .reason-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 4px; }
        .reason-item {
            display: flex; align-items: center; gap: 10px;
            padding: 10px 13px;
            border: 1.5px solid #e5e7eb; border-radius: 9px;
            cursor: pointer; font-size: 13px; color: #374151;
            user-select: none; transition: border-color .15s, background .15s;
        }
        .reason-item input[type="radio"] { display: none; }
        .reason-dot {
            width: 17px; height: 17px; border-radius: 50%;
            border: 2px solid #d1d5db; flex-shrink: 0;
            display: flex; align-items: center; justify-content: center;
            transition: border-color .15s;
        }
        .reason-dot::after {
            content: ''; width: 7px; height: 7px;
            border-radius: 50%; background: #1a56db;
            opacity: 0; transition: opacity .15s;
        }
        .reason-item.selected { border-color: #1a56db; background: #eff6ff; color: #1e40af; }
        .reason-item.selected .reason-dot { border-color: #1a56db; }
        .reason-item.selected .reason-dot::after { opacity: 1; }

        .other-note-box { display: none; margin-top: 10px; }
        .other-note-box.visible { display: block; }
        .other-note-box textarea {
            width: 100%; font-size: 13px; padding: 10px 12px;
            border: 1.5px solid #e5e7eb; border-radius: 9px;
            background: #f9fafb; color: #111827; resize: none;
            outline: none; font-family: inherit; transition: border-color .15s;
        }
        .other-note-box textarea:focus { border-color: #1a56db; background: #fff; }
        .other-note-box textarea::placeholder { color: #9ca3af; }

        .cancel-modal-footer {
            padding: 16px 20px 20px;
            display: flex; gap: 8px; justify-content: flex-end;
        }
        .btn-modal-back {
            background: #f3f4f6; color: #374151; border: none;
            border-radius: 8px; padding: 9px 20px;
            font-size: 13px; font-weight: 600; cursor: pointer;
            transition: background .15s;
        }
        .btn-modal-back:hover { background: #e5e7eb; }
        .btn-modal-confirm {
            background: #dc2626; color: #fff; border: none;
            border-radius: 8px; padding: 9px 20px;
            font-size: 13px; font-weight: 600; cursor: pointer;
            transition: background .15s;
        }
        .btn-modal-confirm:hover { background: #b91c1c; }
        .btn-modal-confirm:disabled { background: #fca5a5; cursor: not-allowed; }

    </style>
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
                        <% } else if ("Shipping".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-shipping">Shipping</span>
                        <% } else if ("Delivered".equalsIgnoreCase(status)) { %>
                            <span class="badge-status badge-delivered">Delivered</span>
                        <% } else { %>
                            <span class="badge-status badge-cancelled"
                                  title="<%=o.getNote() != null && !o.getNote().isEmpty() ? "Reason: " + o.getNote() : "No reason provided"%>"
                                  style="cursor:default">Cancelled</span>
                        <% } %>
                    </td>
                    <td>
                        <%
                            String dateStr = "-";
                            if (o.getCreatedAt() != null) {
                                java.text.SimpleDateFormat sdf =
                                    new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
                                dateStr = sdf.format(o.getCreatedAt());
                            }
                        %>
                        <span class="order-date"><%=dateStr%></span>
                    </td>
                    <td>
                        <% if ("Pending".equalsIgnoreCase(status)) { %>
                        <button type="button" class="btn-cancel-open"
                                onclick="openCancelModal('<%=o.getOrderId()%>')">
                            &#10005; Cancel Order
                        </button>
                        <% } else { %>
                        <span class="no-action">&#8212; No Action</span>
                        <% } %>
                    </td>
                    <td style="text-align:right;">
                        <% if (o.getFinalTotal() == 0) { %>
                        <span style="color:#9ca3af; font-size:13px;">-</span>
                        <% } else { %>
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

        <% } %>

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

<script src="${pageContext.request.contextPath}/lib/bootstrap/js/bootstrap.min.js"></script>
<script>
    var selectedReason = '';

    function openCancelModal(orderId) {
        document.getElementById('cancelOrderId').value = orderId;
        selectedReason = '';
        document.querySelectorAll('.reason-item').forEach(function(el) {
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
        document.querySelectorAll('.reason-item').forEach(function(item) {
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
            document.getElementById('otherNoteText').oninput = function() {
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

    document.getElementById('cancelModalOverlay').addEventListener('click', function(e) {
        if (e.target === this) closeCancelModal();
    });

    function filterOrders(status, btn) {
        document.querySelectorAll('.filter-tab').forEach(function(t) {
            t.classList.remove('active');
        });
        btn.classList.add('active');

        var total = 0;
        document.querySelectorAll('.orders-table tbody tr').forEach(function(row) {
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