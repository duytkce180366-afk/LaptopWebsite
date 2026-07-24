<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage Reviews" />
<%@ include file="_start.jsp" %>

<div class="admin-card">
    <form class="admin-filters" method="get">
        <div>
            <label class="form-label">Search</label>
            <input
                class="form-control"
                name="q"
                value="<c:out value='${q}' />"
                placeholder="User, product, comment">
        </div>

        <div>
            <label class="form-label">Rating</label>
            <select class="form-select" name="rating">
                <option value="0">All</option>

                <c:forEach var="n" items="${[1,2,3,4,5]}">
                    <option value="${n}" ${selectedRating == n ? 'selected' : ''}>
                        ${n} stars
                    </option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label class="form-label">Visibility</label>
            <select class="form-select" name="status">
                <option value="">All</option>
                <option ${selectedStatus == 'Visible' ? 'selected' : ''}>Visible</option>
                <option ${selectedStatus == 'Hidden' ? 'selected' : ''}>Hidden</option>
            </select>
        </div>

        <div class="admin-actions">
            <button class="btn btn-primary">Filter</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/reviews">
                Reset
            </a>
        </div>
    </form>
</div>

<div class="admin-card">
    <strong>${result.totalItems} reviews</strong>

    <table class="admin-table mt-3">
        <thead>
            <tr>
                <th>Customer</th>
                <th>Product</th>
                <th>Rating</th>
                <th>Comment</th>
                <th>Status</th>
                <th></th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="r" items="${result.items}">
                <tr>
                    <td>
                        <strong><c:out value="${r.userName}" /></strong>
                        <br>
                        <small><c:out value="${r.userEmail}" /></small>
                    </td>

                    <td>
                        <a href="${pageContext.request.contextPath}/product?id=${r.productId}">
                            <c:out value="${r.productName}" />
                        </a>
                    </td>

                    <td>
                        ${r.rating}/5
                        <br>
                        <small><fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" /></small>
                    </td>

                    <td>
                        <div class="text-truncate-wide" title="<c:out value='${r.comment}' />">
                            <c:out value="${r.comment}" />
                        </div>
                    </td>

                    <td>
                        <span class="status-pill status-${r.status}">
                            <c:out value="${r.status}" />
                        </span>
                    </td>

                    <td>
                        <form method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
                            <input type="hidden" name="id" value="${r.reviewId}">
                            <input type="hidden" name="status" value="${r.status == 'Visible' ? 'Hidden' : 'Visible'}">

                            <button class="btn btn-sm ${r.status == 'Visible' ? 'btn-outline-danger' : 'btn-outline-success'}">
                                ${r.status == 'Visible' ? 'Hide' : 'Show'}
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty result.items}">
                <tr>
                    <td colspan="6" class="empty-state">No reviews found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <div class="pagination-row">
        <span>Page ${result.page} / ${result.totalPages}</span>

        <div>
            <c:if test="${result.page > 1}">
                <a class="btn btn-sm btn-outline-secondary" href="?page=${result.page - 1}">
                    Previous
                </a>
            </c:if>

            <c:if test="${result.page < result.totalPages}">
                <a class="btn btn-sm btn-outline-secondary" href="?page=${result.page + 1}">
                    Next
                </a>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="_end.jsp" %>