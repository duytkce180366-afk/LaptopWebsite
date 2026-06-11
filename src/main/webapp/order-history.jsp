<%@page import="com.mycompany.techstore.Models.Objects.Order"%>
<%@page import="com.mycompany.techstore.Repositories.OrderRepository"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Orders – TechStore</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/lib/bootstrap/css/bootstrap.min.css">

        <style>

            /* ═══════════════════════════════════════
               THEME — synced with TechStore main site
               Primary blue : #1a56db  (matches hero btn)
               Dark navy    : #111827
               Surface      : #f9fafb
               Border       : #e5e7eb
            ═══════════════════════════════════════ */

            *, *::before, *::after { box-sizing: border-box; }

            body {
                background: #f3f4f6;
                font-family: 'Segoe UI', system-ui, sans-serif;
                color: #111827;
                margin: 0;
            }

            /* ── Layout ── */
            .orders-wrapper {
                max-width: 1200px;
                margin: 0 auto;
                padding: 36px 20px 60px;
            }

            /* ── Breadcrumb ── */
            .breadcrumb-bar {
                font-size: 12.5px;
                color: #6b7280;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 6px;
            }
            .breadcrumb-bar a {
                color: #1a56db;
                text-decoration: none;
            }
            .breadcrumb-bar a:hover { text-decoration: underline; }
            .breadcrumb-sep { color: #d1d5db; }

            /* ── Page header ── */
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

            /* ── Summary chips ── */
            .order-summary-bar {
                display: flex;
                gap: 10px;
                margin-bottom: 18px;
                flex-wrap: wrap;
            }
            .summary-chip {
                display: flex;
                align-items: center;
                gap: 6px;
                background: #fff;
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                padding: 7px 14px;
                font-size: 12.5px;
                color: #374151;
            }
            .summary-chip strong { color: #111827; font-size: 14px; }

            /* ── Card wrapper ── */
            .orders-card {
                background: #ffffff;
                border-radius: 12px;
                border: 1px solid #e5e7eb;
                box-shadow: 0 1px 3px rgba(0,0,0,.06);
                overflow: hidden;
            }

            /* ── Table ── */
            .orders-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 13.5px;
            }
            .orders-table thead tr {
                background: #1e3a5f;
            }
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

            /* ── Order ID ── */
            .order-id {
                font-weight: 700;
                color: #1a56db;
                font-size: 13px;
                font-family: 'Courier New', monospace;
            }

            /* ── Amount ── */
            .order-amount {
                font-weight: 700;
                color: #111827;
                font-size: 13.5px;
            }
            .order-amount-sub {
                font-size: 11px;
                color: #9ca3af;
                display: block;
                margin-top: 1px;
            }

            /* ── Date & address ── */
            .order-date { color: #6b7280; font-size: 12.5px; }
            .order-addr {
                color: #6b7280;
                font-size: 12.5px;
                max-width: 170px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                display: block;
            }
            .order-note {
                color: #9ca3af;
                font-size: 12px;
                font-style: italic;
            }

            /* ── Status badges ── */
            .badge-status {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                font-size: 11.5px;
                font-weight: 600;
                padding: 4px 10px;
                border-radius: 6px;
                white-space: nowrap;
                letter-spacing: 0.01em;
            }
            .badge-status::before {
                content: '';
                width: 6px; height: 6px;
                border-radius: 50%;
                flex-shrink: 0;
            }
            .badge-pending  { background: #fffbeb; color: #92400e; border: 1px solid #fde68a; }
            .badge-pending::before  { background: #f59e0b; }
            .badge-shipping { background: #eff6ff; color: #1e40af; border: 1px solid #bfdbfe; }
            .badge-shipping::before { background: #3b82f6; }
            .badge-delivered{ background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }
            .badge-delivered::before{ background: #22c55e; }
            .badge-cancelled{ background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }
            .badge-cancelled::before{ background: #ef4444; }

            /* ── Cancel button ── */
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
            .btn-cancel-open:hover {
                background: #fef2f2;
                border-color: #ef4444;
            }


            /* ── Expand row ── */
            .expand-btn {
                background: none;
                border: none;
                cursor: pointer;
                color: #1a56db;
                font-size: 12px;
                font-weight: 600;
                padding: 0;
                display: flex;
                align-items: center;
                gap: 4px;
                transition: color .15s;
            }
            .expand-btn:hover { color: #1648c0; }
            .expand-btn .arrow {
                display: inline-block;
                transition: transform .2s;
                font-style: normal;
            }
            .expand-btn.open .arrow { transform: rotate(180deg); }

            .detail-row { display: none; }
            .detail-row.open { display: table-row; }
            .detail-row td {
                padding: 0 !important;
                background: #f9fafb;
                border-bottom: 1px solid #e5e7eb !important;
            }
            .detail-inner {
                padding: 14px 20px 16px 48px;
            }
            .detail-inner table {
                width: 100%;
                border-collapse: collapse;
                font-size: 12.5px;
            }
            .detail-inner th {
                color: #6b7280;
                font-weight: 500;
                font-size: 11px;
                letter-spacing: 0.05em;
                text-transform: uppercase;
                padding: 6px 10px;
                text-align: left;
                border-bottom: 1px solid #e5e7eb;
                background: none;
            }
            .detail-inner td {
                padding: 8px 10px;
                color: #374151;
                border-bottom: 1px solid #f3f4f6;
                background: none !important;
            }
            .detail-inner tr:last-child td { border-bottom: none; }
            .detail-product-name { font-weight: 600; color: #111827; }
            .detail-price { color: #6b7280; }
            .detail-subtotal { font-weight: 700; color: #1a56db; font-size: 13px; }
            .detail-empty { color: #9ca3af; font-size: 12px; font-style: italic; padding: 10px; }

            /* ── No action ── */
            .no-action { color: #d1d5db; font-size: 12px; }

            /* ── Empty state ── */
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
                transition: background .15s;
            }
            .empty-state a:hover { background: #1648c0; }

            /* ════════════════════════════════════
               CANCEL MODAL
            ════════════════════════════════════ */
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
                to   { transform: translateY(0)    scale(1);   opacity: 1; }
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
                flex-shrink: 0;
                font-size: 16px;
            }
            .cancel-modal-header h3 {
                font-size: 15px;
                font-weight: 700;
                color: #111827;
                margin: 0;
                flex: 1;
            }
            .modal-close-btn {
                background: none;
                border: none;
                cursor: pointer;
                color: #9ca3af;
                width: 28px; height: 28px;
                border-radius: 6px;
                font-size: 15px;
                display: flex; align-items: center; justify-content: center;
                transition: color .15s, background .15s;
            }
            .modal-close-btn:hover { color: #111827; background: #f3f4f6; }

            .cancel-modal-body { padding: 18px 20px 4px; }
            .cancel-modal-body > p {
                font-size: 13px;
                color: #6b7280;
                margin: 0 0 14px;
            }

            /* Reason list */
            .reason-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 4px; }
            .reason-item {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 10px 13px;
                border: 1.5px solid #e5e7eb;
                border-radius: 9px;
                cursor: pointer;
                font-size: 13px;
                color: #374151;
                user-select: none;
                transition: border-color .15s, background .15s;
            }
            .reason-item input[type="radio"] { display: none; }
            .reason-dot {
                width: 17px; height: 17px;
                border-radius: 50%;
                border: 2px solid #d1d5db;
                flex-shrink: 0;
                display: flex; align-items: center; justify-content: center;
                transition: border-color .15s;
            }
            .reason-dot::after {
                content: '';
                width: 7px; height: 7px;
                border-radius: 50%;
                background: #1a56db;
                opacity: 0;
                transition: opacity .15s;
            }
            .reason-item.selected {
                border-color: #1a56db;
                background: #eff6ff;
                color: #1e40af;
            }
            .reason-item.selected .reason-dot { border-color: #1a56db; }
            .reason-item.selected .reason-dot::after { opacity: 1; }

            /* Other textarea */
            .other-note-box { display: none; margin-top: 10px; }
            .other-note-box.visible { display: block; }
            .other-note-box textarea {
                width: 100%;
                font-size: 13px;
                padding: 10px 12px;
                border: 1.5px solid #e5e7eb;
                border-radius: 9px;
                background: #f9fafb;
                color: #111827;
                resize: none;
                outline: none;
                font-family: inherit;
                transition: border-color .15s;
            }
            .other-note-box textarea:focus { border-color: #1a56db; background: #fff; }
            .other-note-box textarea::placeholder { color: #9ca3af; }

            /* Modal footer */
            .cancel-modal-footer {
                padding: 16px 20px 20px;
                display: flex;
                gap: 8px;
                justify-content: flex-end;
            }
            .btn-modal-back {
                background: #f3f4f6;
                color: #374151;
                border: none;
                border-radius: 8px;
                padding: 9px 20px;
                font-size: 13px;
                font-weight: 600;
                cursor: pointer;
                transition: background .15s;
            }
            .btn-modal-back:hover { background: #e5e7eb; }
            .btn-modal-confirm {
                background: #dc2626;
                color: #fff;
                border: none;
                border-radius: 8px;
                padding: 9px 20px;
                font-size: 13px;
                font-weight: 600;
                cursor: pointer;
                transition: background .15s;
            }
            .btn-modal-confirm:hover { background: #b91c1c; }
            .btn-modal-confirm:disabled { background: #fca5a5; cursor: not-allowed; }


            /* ── Expand row ── */
            .expand-btn {
                background: none;
                border: none;
                cursor: pointer;
                color: #1a56db;
                font-size: 12px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 4px;
                padding: 0;
                transition: color .15s;
            }
            .expand-btn:hover { color: #1648c0; }
            .expand-btn .chevron {
                display: inline-block;
                transition: transform .2s;
                font-size: 10px;
            }
            .expand-btn.open .chevron { transform: rotate(180deg); }

            .detail-row { display: none; }
            .detail-row.open { display: table-row; }
            .detail-row > td {
                padding: 0 !important;
                background: #f9fafb;
                border-bottom: 1px solid #e5e7eb;
            }

            .detail-inner {
                padding: 14px 20px 16px 48px;
            }
            .detail-title {
                font-size: 11px;
                font-weight: 700;
                letter-spacing: 0.06em;
                text-transform: uppercase;
                color: #9ca3af;
                margin-bottom: 10px;
            }
            .detail-items {
                display: flex;
                flex-direction: column;
                gap: 8px;
            }
            .detail-item {
                display: flex;
                align-items: center;
                gap: 12px;
                background: #fff;
                border: 1px solid #e5e7eb;
                border-radius: 10px;
                padding: 10px 14px;
            }
            .detail-item img {
                width: 44px;
                height: 44px;
                object-fit: cover;
                border-radius: 7px;
                background: #f3f4f6;
                flex-shrink: 0;
            }
            .detail-item-info { flex: 1; min-width: 0; }
            .detail-item-name {
                font-size: 13px;
                font-weight: 600;
                color: #111827;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .detail-item-meta {
                font-size: 12px;
                color: #6b7280;
                margin-top: 2px;
            }
            .detail-item-price {
                font-size: 13px;
                font-weight: 700;
                color: #1a56db;
                white-space: nowrap;
                flex-shrink: 0;
            }


            /* ── Detail inner table ── */
            .detail-inner { padding: 14px 20px 16px 20px; }
            .detail-title {
                font-size: 11px; font-weight: 700;
                letter-spacing: 0.06em; text-transform: uppercase;
                color: #9ca3af; margin-bottom: 10px;
            }
            .detail-table { width: 100%; border-collapse: collapse; font-size: 13px; }
            .detail-table th {
                font-size: 11px; color: #9ca3af; font-weight: 600;
                text-transform: uppercase; letter-spacing: 0.04em;
                padding: 8px 10px; border-bottom: 1px solid #f3f4f6;
                text-align: left; background: #f9fafb;
            }
            .detail-table td {
                padding: 10px 10px; border-bottom: 1px solid #f3f4f6;
                vertical-align: middle; color: #374151;
            }
            .detail-table tr:last-child td { border-bottom: none; }
            .detail-product-name { font-weight: 600; color: #111827; font-size: 13px; }
            .detail-price { color: #6b7280; }
            .detail-subtotal { font-weight: 700; color: #1a56db; font-size: 13px; }
            .detail-empty { text-align: center; color: #9ca3af; font-size: 13px; padding: 16px !important; }
            .expand-btn .arrow { font-size: 9px; display: inline-block; transition: transform .2s; }
            .expand-btn.open .arrow { transform: rotate(180deg); }

        </style>
    </head>

    <body>

        <div class="orders-wrapper">

            <!-- Breadcrumb -->
            <div class="breadcrumb-bar">
                <a href="home">Home</a>
                <span class="breadcrumb-sep">&#8250;</span>
                <span>My Orders</span>
            </div>

            <!-- Page header -->
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

            <!-- Orders card -->
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
                    <a href="product-list">Browse Products</a>
                </div>

                <% } else { %>

                <table class="orders-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Date</th>
                            <th>Address</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>

                        <% for (Order o : orders) {
                               String status = o.getOrderStatus();
                        %>

                        <tr>

                            <td>
                                <span class="order-id">#<%=o.getOrderId()%></span><br>
                                <button class="expand-btn" onclick="toggleDetail(this, <%=o.getOrderId()%>)">
                                    <i class="arrow">&#9660;</i> Details
                                </button>
                            </td>

                            <td>
                                <span class="order-amount">
                                    <%=String.format("%,.0f", o.getTotalAmount())%> ₫
                                </span>
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
                                <span class="order-date"><%=o.getCreatedAt()%></span>
                            </td>

                            <td>
                                <span class="order-addr" title="<%=o.getAddressInfo()%>">
                                    <%=o.getAddressInfo()%>
                                </span>
                            </td>

                            <td>
                                <% if ("Pending".equalsIgnoreCase(status)) { %>

                                <button type="button"
                                        class="btn-cancel-open"
                                        onclick="openCancelModal('<%=o.getOrderId()%>')">
                                    ✕ Cancel Order
                                </button>

                                <% } else { %>
                                    <span class="no-action">— No Action</span>
                                <% } %>
                            </td>

                        </tr>

                        <!-- Detail expand row -->
                        <tr class="detail-row" id="detail-row-<%=o.getOrderId()%>">
                            <td colspan="6">
                                <div class="detail-inner">
                                    <p class="detail-title">Items in this order</p>
                                    <table class="detail-table">
                                        <thead>
                                            <tr>
                                                <th>Product</th>
                                                <th>Unit Price</th>
                                                <th>Qty</th>
                                                <th>Subtotal</th>
                                            </tr>
                                        </thead>
                                        <tbody id="detail-body-<%=o.getOrderId()%>">
                                            <tr><td colspan="4" class="detail-empty">Click to load items...</td></tr>
                                        </tbody>
                                    </table>
                                </div>
                            </td>
                        </tr>

                        <% } %>

                    </tbody>
                </table>

                <% } %>

            </div>

        </div>

        <!-- ── Cancel Order Modal ── -->
        <div class="cancel-modal-overlay" id="cancelModalOverlay">
            <div class="cancel-modal">

                <div class="cancel-modal-header">
                    <div class="modal-icon">🚫</div>
                    <h3>Cancel Order</h3>
                    <button class="modal-close-btn" onclick="closeCancelModal()">✕</button>
                </div>

                <div class="cancel-modal-body">
                    <p>Please select a reason for cancellation:</p>

                    <div class="reason-list" id="reasonList">

                        <label class="reason-item" onclick="selectReason(this, 'I changed my mind')">
                            <input type="radio" name="cancelReason" value="I changed my mind">
                            <span class="reason-dot"></span>
                            I changed my mind
                        </label>

                        <label class="reason-item" onclick="selectReason(this, 'Found a better price elsewhere')">
                            <input type="radio" name="cancelReason" value="Found a better price elsewhere">
                            <span class="reason-dot"></span>
                            Found a better price elsewhere
                        </label>

                        <label class="reason-item" onclick="selectReason(this, 'Ordered by mistake')">
                            <input type="radio" name="cancelReason" value="Ordered by mistake">
                            <span class="reason-dot"></span>
                            Ordered by mistake
                        </label>

                        <label class="reason-item" onclick="selectReason(this, 'Delivery time too long')">
                            <input type="radio" name="cancelReason" value="Delivery time too long">
                            <span class="reason-dot"></span>
                            Delivery time too long
                        </label>

                        <label class="reason-item" id="otherReasonItem" onclick="selectReason(this, 'other')">
                            <input type="radio" name="cancelReason" value="other">
                            <span class="reason-dot"></span>
                            Other reason
                        </label>

                    </div>

                    <div class="other-note-box" id="otherNoteBox">
                        <textarea id="otherNoteText"
                                  rows="3"
                                  placeholder="Please describe your reason..."></textarea>
                    </div>
                </div>

                <div class="cancel-modal-footer">
                    <button class="btn-modal-back" onclick="closeCancelModal()">Go Back</button>
                    <form id="cancelForm" action="cancel-order" method="post" style="display:inline;">
                        <input type="hidden" name="id" id="cancelOrderId">
                        <input type="hidden" name="note" id="cancelNoteHidden">
                        <button type="button"
                                class="btn-modal-confirm"
                                id="confirmCancelBtn"
                                disabled
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
                // reset state
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
                        var txt = this.value.trim();
                        document.getElementById('confirmCancelBtn').disabled = txt.length === 0;
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

            // Close when clicking overlay background
            document.getElementById('cancelModalOverlay').addEventListener('click', function(e) {
                if (e.target === this) closeCancelModal();
            });
        </script>

        <script>
            function toggleDetail(btn, orderId) {
                var row = document.getElementById('detail-row-' + orderId);
                var isOpen = row.classList.contains('open');

                // Close all others
                document.querySelectorAll('.detail-row.open').forEach(function(r) {
                    r.classList.remove('open');
                });
                document.querySelectorAll('.expand-btn.open').forEach(function(b) {
                    b.classList.remove('open');
                });

                if (!isOpen) {
                    row.classList.add('open');
                    btn.classList.add('open');
                    loadDetail(orderId);
                }
            }

            function loadDetail(orderId) {
                var tbody = document.getElementById('detail-body-' + orderId);
                if (tbody.dataset.loaded === 'true') return;

                fetch('order-detail?id=' + orderId)
                    .then(function(res) { return res.json(); })
                    .then(function(items) {
                        if (!items || items.length === 0) {
                            tbody.innerHTML = '<tr><td colspan="4" class="detail-empty">No items found.</td></tr>';
                            return;
                        }
                        var html = '';
                        items.forEach(function(item) {
                            var subtotal = (item.unitPrice * item.quantity).toLocaleString('vi-VN');
                            var unitPrice = item.unitPrice.toLocaleString('vi-VN');
                            html += '<tr>'
                                + '<td><span class="detail-product-name">' + item.productName + '</span></td>'
                                + '<td><span class="detail-price">' + unitPrice + ' &#8363;</span></td>'
                                + '<td>x' + item.quantity + '</td>'
                                + '<td><span class="detail-subtotal">' + subtotal + ' &#8363;</span></td>'
                                + '</tr>';
                        });
                        tbody.innerHTML = html;
                        tbody.dataset.loaded = 'true';
                    })
                    .catch(function() {
                        tbody.innerHTML = '<tr><td colspan="4" class="detail-empty">Failed to load.</td></tr>';
                    });
            }
        </script>

    </body>
</html>
