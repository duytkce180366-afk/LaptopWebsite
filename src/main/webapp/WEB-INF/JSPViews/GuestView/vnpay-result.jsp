<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Kết quả thanh toán - TechStore</title>
    <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    <style>
        body { background:#f3f4f6; font-family:'Segoe UI',system-ui,sans-serif; margin:0; }
        .result-wrapper {
            max-width: 480px;
            margin: 80px auto;
            background: #fff;
            border-radius: 14px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 4px 20px rgba(0,0,0,.08);
            padding: 48px 36px;
            text-align: center;
        }
        .result-icon { font-size: 56px; margin-bottom: 16px; }
        .result-title { font-size: 22px; font-weight: 700; margin: 0 0 8px; }
        .result-sub   { font-size: 14px; color: #6b7280; margin: 0 0 28px; }
        .btn-home {
            display: inline-block;
            background: #1a56db;
            color: #fff;
            border-radius: 8px;
            padding: 11px 28px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
        }
        .btn-home:hover { background: #1648c0; }
        .btn-orders {
            display: inline-block;
            background: #f3f4f6;
            color: #374151;
            border-radius: 8px;
            padding: 11px 28px;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            margin-left: 10px;
        }
        .btn-orders:hover { background: #e5e7eb; }
    </style>
</head>
<body>
<%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>

<div class="result-wrapper">
    <% String status = (String) request.getAttribute("vnpayStatus"); %>

    <% if ("success".equals(status)) { %>
        <div class="result-icon">✅</div>
        <h2 class="result-title" style="color:#166534;">Thanh toán thành công!</h2>
        <p class="result-sub">Đơn hàng của bạn đã được đặt. Cảm ơn bạn đã mua hàng tại TechStore.</p>
        <a href="<%= request.getContextPath()%>/order-history" class="btn-home">Xem đơn hàng</a>
        <a href="<%= request.getContextPath()%>/home" class="btn-orders">Về trang chủ</a>
    <% } else if ("failed".equals(status)) { %>
        <div class="result-icon">❌</div>
        <h2 class="result-title" style="color:#991b1b;">Thanh toán thất bại</h2>
        <p class="result-sub">Giao dịch không thành công (mã: <%= request.getAttribute("vnpayCode")%>). Vui lòng thử lại.</p>
        <a href="<%= request.getContextPath()%>/home" class="btn-home">Về trang chủ</a>
    <% } else { %>
        <div class="result-icon">⚠️</div>
        <h2 class="result-title" style="color:#92400e;">Có lỗi xảy ra</h2>
        <p class="result-sub">Không thể xác minh giao dịch. Vui lòng liên hệ hỗ trợ.</p>
        <a href="<%= request.getContextPath()%>/home" class="btn-home">Về trang chủ</a>
    <% } %>
</div>

</body>
</html>