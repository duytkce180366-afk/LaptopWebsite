<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Reports" />
<%@ include file="_start.jsp" %>

<div id="pdf-header" style="display: none; text-align: center; margin-bottom: 30px;">
    <h2 style="margin: 0; color: #0d6efd;">TECHSTORE SYSTEM</h2>
    <h3 style="margin: 5px 0 10px; text-transform: uppercase;">
        <c:choose>
            <c:when test="${type == 'orders'}">Orders</c:when>
            <c:when test="${type == 'products'}">Top Products</c:when>
            <c:otherwise>Revenue</c:otherwise>
        </c:choose>
        REPORT
    </h3>
    <p style="margin: 0; color: #6b7280;">
        <strong>Period:</strong> ${from} to ${to}
    </p>
</div>

<div class="admin-card">
    <form class="admin-filters" method="get">
        <div>
            <label class="form-label">Report</label>
            <select class="form-select" name="type">
                <option value="sales" ${type == 'sales' ? 'selected' : ''}>Revenue</option>
                <option value="orders" ${type == 'orders' ? 'selected' : ''}>Orders</option>
                <option value="products" ${type == 'products' ? 'selected' : ''}>Top products</option>
            </select>
        </div>

        <div>
            <label class="form-label">From</label>
            <input class="form-control" type="date" name="from" value="${from}">
        </div>

        <div>
            <label class="form-label">To</label>
            <input class="form-control" type="date" name="to" value="${to}">
        </div>

        <div class="admin-actions">
            <button class="btn btn-primary">Generate</button>
            <button class="btn btn-outline-success" name="format" value="csv">Export CSV</button>
            <button type="button" class="btn btn-outline-danger" onclick="downloadPDF()">Export PDF</button>
        </div>
    </form>
</div>

<div class="admin-card">
    <c:choose>
        <c:when test="${type == 'orders'}">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Customer</th>
                        <th>Email</th>
                        <th>Payment</th>
                        <th>Status</th>
                        <th>Total</th>
                        <th>Date</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="r" items="${rows}">
                        <tr>
                            <td>${r.order_id}</td>
                            <td><c:out value="${r.customer}" /></td>
                            <td><c:out value="${r.email}" /></td>
                            <td><c:out value="${r.payment_method}" /></td>
                            <td><c:out value="${r.order_status}" /></td>
                            <td>${r.total}</td>
                            <td><fmt:formatDate value="${r.created_at}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:when test="${type == 'products'}">
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>SKU</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Revenue</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="r" items="${rows}">
                        <tr>
                            <td><c:out value="${r.sku}" /></td>
                            <td><c:out value="${r.product_name}" /></td>
                            <td>${r.quantity}</td>
                            <td>${r.revenue}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:otherwise>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Delivered orders</th>
                        <th>Revenue</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach var="r" items="${rows}">
                        <tr>
                            <td><fmt:formatDate value="${r.report_date}" pattern="dd/MM/yyyy" /></td>
                            <td>${r.orders}</td>
                            <td>${r.revenue}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <c:if test="${empty rows}">
        <div class="empty-state">No data for the selected period.</div>
    </c:if>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
<script>
function downloadPDF() {
    var filters = document.querySelector('.admin-filters');
    var userProfile = document.querySelector('.admin-user');
    var topbar = document.querySelector('.admin-topbar');
    var pdfHeader = document.getElementById('pdf-header');
    
    // Temporarily hide elements we don't want in the PDF
    if (filters) filters.style.display = 'none';
    if (userProfile) userProfile.style.display = 'none';
    if (topbar) topbar.style.display = 'none'; // Hide generic admin topbar
    
    // Show formal report header
    if (pdfHeader) pdfHeader.style.display = 'block';
    
    var element = document.querySelector('.admin-main');
    
    var opt = {
      margin:       0.5,
      filename:     'techstore-report.pdf',
      image:        { type: 'jpeg', quality: 1 },
      html2canvas:  { scale: 2, useCORS: true },
      jsPDF:        { unit: 'in', format: 'a4', orientation: 'landscape' }
    };
    
    html2pdf().set(opt).from(element).save().then(function() {
        // Restore elements
        if (filters) filters.style.display = '';
        if (userProfile) userProfile.style.display = '';
        if (topbar) topbar.style.display = '';
        if (pdfHeader) pdfHeader.style.display = 'none';
    });
}
</script>

<%@ include file="_end.jsp" %>
