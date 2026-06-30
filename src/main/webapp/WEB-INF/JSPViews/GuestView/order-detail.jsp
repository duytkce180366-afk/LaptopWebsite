<%@page import="com.mycompany.techstore.Models.Objects.User"%>
<%@page import="com.mycompany.techstore.Models.Objects.OrderDetail"%>
<%@page import="com.mycompany.techstore.Models.Objects.Order"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        return;
    }
    Order order = (Order) request.getAttribute("order");
    List<OrderDetail> details = (List<OrderDetail>) request.getAttribute("details");
    if (order == null) {
        response.sendRedirect(request.getContextPath() + "/order-history");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Order #<%=order.getOrderId()%> - TechStore</title>
    <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    <style>
        *, *::before, *::after { box-sizing: border-box; }
        body {
            background: #f3f4f6;
            font-family: 'Segoe UI', system-ui, sans-serif;
            color: #111827;
            margin: 0;
        }
        .detail-wrapper {
            max-width: 860px;
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
            justify-content: space-between;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 12px;
        }
        .page-header-left {
            display: flex;
            align-items: center;
            gap: 12px;
        }
        .page-header-icon {
            width: 40px; height: 40px;
            background: #1a56db;
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
        }
        .page-header h2 { font-size: 20px; font-weight: 700; margin: 0; color: #111827; }
        .page-header p { font-size: 13px; color: #6b7280; margin: 2px 0 0; }
        .btn-back {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: #fff;
            color: #374151;
            border: 1px solid #e5e7eb;
            border-radius: 8px;
            padding: 8px 16px;
            font-size: 13px;
            font-weight: 500;
            text-decoration: none;
            transition: background .15s;
        }
        .btn-back:hover { background: #f3f4f6; color: #111827; }
        .card {
            background: #fff;
            border: 1px solid #e5e7eb;
            border-radius: 12px;
            box-shadow: 0 1px 3px rgba(0,0,0,.06);
            margin-bottom: 16px;
        }
        .card-header {
            padding: 14px 20px;
            border-bottom: 1px solid #f3f4f6;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .card-header h3 {
            font-size: 13px;
            font-weight: 700;
            color: #374151;
            margin: 0;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        .card-body { padding: 18px 20px; }
        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 14px 24px;
        }
        .info-item label {
            font-size: 11px;
            font-weight: 600;
            color: #9ca3af;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            display: block;
            margin-bottom: 3px;
        }
        .info-item span { font-size: 13.5px; color: #111827; font-weight: 500; }
        .badge-status {
            display: inline-flex;
            align-items: center;
            gap: 5px;
            font-size: 12px;
            font-weight: 600;
            padding: 4px 10px;
            border-radius: 6px;
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
        .product-row {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 14px 0;
            border-bottom: 1px solid #f3f4f6;
        }
        .product-row:last-child { border-bottom: none; }
        .product-thumb {
            width: 64px; height: 64px;
            border-radius: 8px;
            object-fit: cover;
            background: #f3f4f6;
            border: 1px solid #e5e7eb;
            flex-shrink: 0;
        }
        .product-thumb-placeholder {
            width: 64px; height: 64px;
            border-radius: 8px;
            background: #f3f4f6;
            border: 1px solid #e5e7eb;
            flex-shrink: 0;
            display: flex; align-items: center; justify-content: center;
            color: #d1d5db;
            font-size: 22px;
        }
        .product-info { flex: 1; min-width: 0; }
        .product-name {
            font-size: 14px;
            font-weight: 600;
            color: #111827;
            margin: 0 0 3px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .product-sku { font-size: 11.5px; color: #9ca3af; }
        .product-qty { font-size: 13px; color: #6b7280; white-space: nowrap; text-align: center; min-width: 60px; }
        .product-price { font-size: 13px; color: #6b7280; white-space: nowrap; text-align: right; min-width: 110px; }
        .product-subtotal { font-size: 14px; font-weight: 700; color: #111827; white-space: nowrap; text-align: right; min-width: 120px; }
        .order-summary {
            border-top: 1px solid #f3f4f6;
            padding-top: 14px;
            margin-top: 4px;
            display: flex;
            flex-direction: column;
            align-items: flex-end;
            gap: 6px;
        }
        .summary-row { display: flex; gap: 40px; font-size: 13px; color: #6b7280; }
        .summary-row span:last-child { min-width: 120px; text-align: right; }
        .summary-total {
            display: flex;
            gap: 40px;
            font-size: 16px;
            font-weight: 700;
            color: #111827;
            border-top: 1px solid #e5e7eb;
            padding-top: 10px;
            margin-top: 4px;
        }
        .summary-total span:last-child { min-width: 120px; text-align: right; color: #1a56db; }
    </style>
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
                    <%
                        String status = order.getOrderStatus();
                        if ("Pending".equalsIgnoreCase(status)) {
                    %>
                    <span class="badge-status badge-pending">Pending</span>
                    <% } else if ("Shipping".equalsIgnoreCase(status)) { %>
                    <span class="badge-status badge-shipping">Shipping</span>
                    <% } else if ("Delivered".equalsIgnoreCase(status)) { %>
                    <span class="badge-status badge-delivered">Delivered</span>
                    <% } else { %>
                    <span class="badge-status badge-cancelled"
                          title="<%=order.getNote() != null ? "Reason: " + order.getNote() : ""%>">
                        Cancelled
                    </span>
                    <% } %>
                </div>

                <div class="info-item">
                    <label>Payment Method</label>
                    <span>-</span>
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

            <% for (OrderDetail d : details) { %>
            <div class="product-row">
                <% if (d.getThumbnail() != null && !d.getThumbnail().isEmpty()) { %>
                <img src="<%=d.getThumbnail()%>"
                     alt="<%=d.getProductName()%>"
                     class="product-thumb"
                     onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">
                <div class="product-thumb-placeholder" style="display:none">&#128421;</div>
                <% } else { %>
                <div class="product-thumb-placeholder">&#128421;</div>
                <% } %>

                <div class="product-info">
                    <p class="product-name"><%=d.getProductName()%></p>
                    <span class="product-sku">SKU: <%=d.getSku()%></span>
                </div>
                <div class="product-qty">x<%=d.getQuantity()%></div>
                <div class="product-price"><%=String.format("%,.0f", d.getUnitPrice())%> d</div>
                <div class="product-subtotal"><%=String.format("%,.0f", d.getSubtotal())%> d</div>
            </div>
            <% } %>

            <div class="order-summary">
                <div class="summary-row">
                    <span>Subtotal</span>
                    <span><%=String.format("%,.0f", order.getTotalAmount())%> d</span>
                </div>
                <div class="summary-row">
                    <span>Shipping fee</span>
                    <span>30,000 d</span>
                </div>
                <div class="summary-total">
                    <span>Total</span>
                    <span><%=String.format("%,.0f", order.getTotalAmount())%> d</span>
                </div>
            </div>

        </div>
    </div>

</div>

<script>
    async function resolveAddress() {
        var el = document.getElementById('deliveryAddress');
        if (!el) return;
        var raw = el.textContent.trim();
        // Format: "address, districtCode, provinceCode"
        var parts = raw.split(',').map(function(s) { return s.trim(); });
        if (parts.length < 3) return;

        var address  = parts[0];
        var distCode = parts[1];
        var provCode = parts[2];

        // Only convert if distCode and provCode are numeric
        if (isNaN(distCode) || isNaN(provCode)) return;

        try {
            var provRes  = await fetch('https://provinces.open-api.vn/api/p/' + provCode);
            var provData = await provRes.json();

            var distRes  = await fetch('https://provinces.open-api.vn/api/d/' + distCode);
            var distData = await distRes.json();

            el.textContent = address + ', ' + distData.name + ', ' + provData.name;
        } catch(e) {
            // Keep original text if API call fails
        }
    }
    resolveAddress();
</script>

</body>
</html>
