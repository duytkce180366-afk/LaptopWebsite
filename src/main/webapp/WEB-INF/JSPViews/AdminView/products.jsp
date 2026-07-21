<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Products" />
<%@ include file="_start.jsp" %>

<div class="admin-card">
    <form class="admin-filters" method="get">
        <div>
            <label class="form-label">Search</label>
            <input
                class="form-control"
                name="q"
                value="<c:out value='${q}' />"
                placeholder="Name or SKU">
        </div>

        <div>
            <label class="form-label">Category</label>
            <select class="form-select" name="category">
                <option value="0">All</option>

                <c:forEach var="x" items="${categories}">
                    <option value="${x.id}" ${selectedCategory == x.id ? 'selected' : ''}>
                        <c:out value="${x.name}" />
                    </option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label class="form-label">Brand</label>
            <select class="form-select" name="brand">
                <option value="0">All</option>

                <c:forEach var="x" items="${brands}">
                    <option value="${x.id}" ${selectedBrand == x.id ? 'selected' : ''}>
                        <c:out value="${x.name}" />
                    </option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label class="form-label">Status</label>
            <select class="form-select" name="status">
                <option value="">All</option>

                <c:forEach var="s" items="${['Active','Out of Stock','Hidden','Inactive']}">
                    <option ${selectedStatus == s ? 'selected' : ''}>
                        <c:out value="${s}" />
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="admin-actions">
            <button class="btn btn-primary">Filter</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">
                Reset
            </a>
        </div>
    </form>
</div>

<div class="admin-card">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <strong>${result.totalItems} products</strong>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/products/new">
            Add product
        </a>
    </div>

    <table class="admin-table">
        <thead>
            <tr>
                <th>SKU</th>
                <th>Product</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Status</th>
                <th></th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="p" items="${result.items}">
                <tr>
                    <td><c:out value="${p.sku}" /></td>

                    <td>
                        <strong><c:out value="${p.productName}" /></strong>
                        <br>
                        <small><c:out value="${p.brandName}" /></small>
                    </td>

                    <td><c:out value="${p.categoryName}" /></td>
                    <td>${p.price}</td>
                    <td>${p.stock}</td>

                    <td>
                        <span class="status-pill status-${p.status.replace(' ', '-')}">
                            <c:out value="${p.status}" />
                        </span>
                    </td>

                    <td>
                        <div class="admin-actions">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/admin/products/edit?id=${p.productId}">
                                Edit
                            </a>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/products/deactivate"
                                  onsubmit="return confirm('Deactivate this product?')">
                                <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
                                <input type="hidden" name="id" value="${p.productId}">
                                <button class="btn btn-sm btn-outline-danger">Deactivate</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty result.items}">
                <tr>
                    <td colspan="7" class="empty-state">No products found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <div class="pagination-row">
        <span>Page ${result.page} / ${result.totalPages}</span>

        <div>
            <c:if test="${result.page > 1}">
                <a class="btn btn-sm btn-outline-secondary"
                   href="?page=${result.page - 1}&q=${q}&category=${selectedCategory}&brand=${selectedBrand}&status=${selectedStatus}">
                    Previous
                </a>
            </c:if>

            <c:if test="${result.page < result.totalPages}">
                <a class="btn btn-sm btn-outline-secondary"
                   href="?page=${result.page + 1}&q=${q}&category=${selectedCategory}&brand=${selectedBrand}&status=${selectedStatus}">
                    Next
                </a>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="_end.jsp" %>