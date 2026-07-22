<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Inventory Receipts" />
<%@ include file="_start.jsp" %>

<div class="admin-card">
    <form class="admin-filters" method="get">
        <div>
            <label class="form-label">Search product</label>
            <input
                class="form-control"
                name="q"
                value="<c:out value='${q}'/>"
                placeholder="SKU or product name">
        </div>

        <div class="admin-actions">
            <button class="btn btn-primary">Search</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/inventory">
                Reset
            </a>
        </div>
    </form>
</div>

<div class="admin-card">
    <h2 class="h5">Receive stock</h2>

    <table class="admin-table">
        <thead>
            <tr>
                <th>SKU</th>
                <th>Product</th>
                <th>Current stock</th>
                <th>Receive quantity</th>
                <th>Note</th>
                <th></th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="p" items="${result.items}">
                <tr>
                    <td>
                        <form id="receive-${p.productId}" method="post" action="${pageContext.request.contextPath}/admin/inventory">
                            <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
                            <input type="hidden" name="productId" value="${p.productId}">
                        </form>
                        <c:out value="${p.sku}" />
                    </td>

                    <td><c:out value="${p.productName}" /></td>
                    <td>${p.stock}</td>

                    <td>
                        <input
                            class="form-control"
                            form="receive-${p.productId}"
                            type="number"
                            name="quantity"
                            min="1"
                            max="100000"
                            required>
                    </td>

                    <td>
                        <input
                            class="form-control"
                            form="receive-${p.productId}"
                            name="note"
                            maxlength="500"
                            placeholder="Supplier / reference">
                    </td>

                    <td>
                        <button class="btn btn-sm btn-primary" form="receive-${p.productId}">
                            Receive
                        </button>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty result.items}">
                <tr>
                    <td colspan="6" class="empty-state">No products found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<div class="admin-card">
    <h2 class="h5">Recent receipts</h2>

    <table class="admin-table">
        <thead>
            <tr>
                <th>Time</th>
                <th>SKU</th>
                <th>Product</th>
                <th>Quantity</th>
                <th>Stock change</th>
                <th>Admin</th>
                <th>Note</th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="r" items="${receipts}">
                <tr>
                    <td><fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                    <td><c:out value="${r.sku}" /></td>
                    <td><c:out value="${r.productName}" /></td>
                    <td>+${r.quantity}</td>
                    <td>${r.previousStock} &rarr; ${r.resultingStock}</td>
                    <td><c:out value="${r.adminName}" /></td>
                    <td><c:out value="${r.note}" /></td>
                </tr>
            </c:forEach>

            <c:if test="${empty receipts}">
                <tr>
                    <td colspan="7" class="empty-state">No stock receipts yet.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<%@ include file="_end.jsp" %>