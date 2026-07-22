<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="${isAdmin ? 'Manage Users and Staff' : 'Manage Customers'}" />
<%@ include file="_start.jsp" %>

<div class="admin-card">
    <form class="admin-filters" method="get">
        <div>
            <label class="form-label" for="searchQuery">Search</label>
            <input
                id="searchQuery"
                class="form-control"
                name="q"
                value="<c:out value='${q}' />"
                placeholder="Name, email, phone">
        </div>

        <c:if test="${isAdmin}">
            <div>
                <label class="form-label" for="role">Role</label>
                <select id="role" class="form-select" name="role">
                    <option value="0">All</option>

                    <c:forEach var="r" items="${roles}">
                        <option value="${r.id}" ${selectedRole == r.id ? 'selected' : ''}>
                            <c:out value="${r.name}" />
                        </option>
                    </c:forEach>
                </select>
            </div>
        </c:if>

        <div>
            <label class="form-label" for="status">Status</label>
            <select id="status" class="form-select" name="status">
                <option value="">All</option>

                <c:forEach var="s" items="${['Active','Blocked','Inactive','Pending']}">
                    <option ${selectedStatus == s ? 'selected' : ''}>
                        <c:out value="${s}" />
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="admin-actions">
            <button class="btn btn-primary">Filter</button>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">
                Reset
            </a>

            <c:if test="${isAdmin}">
                <a class="btn btn-success" href="${pageContext.request.contextPath}/admin/users/new">
                    Create Staff
                </a>
            </c:if>
        </div>
    </form>
</div>

<div class="admin-card">
    <strong>${result.totalItems} accounts</strong>

    <table class="admin-table mt-3">
        <thead>
            <tr>
                <th>User</th>
                <th>Contact</th>
                <th>Verified</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>

        <tbody>
            <c:forEach var="u" items="${result.items}">
                <tr>
                    <td>
                        <strong><c:out value="${u.fullName}" /></strong>
                        <br>
                        <small>#${u.userId} &middot; <fmt:formatDate value="${u.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" /></small>
                    </td>

                    <td>
                        <c:out value="${u.email}" />
                        <br>
                        <small><c:out value="${u.phone}" /></small>
                    </td>

                    <td>${u.verified ? 'Yes' : 'No'}</td>

                    <td>
                        <c:choose>
                            <c:when test="${isAdmin}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/role" class="admin-actions">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
                                    <input type="hidden" name="id" value="${u.userId}">

                                    <label for="roleSelect_${u.userId}" class="visually-hidden">Role</label>
                                    <select id="roleSelect_${u.userId}" class="form-select form-select-sm" name="roleId">
                                        <c:forEach var="r" items="${roles}">
                                            <option value="${r.id}" ${u.roleId == r.id ? 'selected' : ''}>
                                                <c:out value="${r.name}" />
                                            </option>
                                        </c:forEach>
                                    </select>

                                    <button class="btn btn-sm btn-outline-primary">Save</button>
                                </form>
                            </c:when>

                            <c:otherwise>
                                <c:out value="${u.roleName}" />
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td>
                        <span class="status-pill status-${u.status}">
                            <c:out value="${u.status}" />
                        </span>
                    </td>

                    <td>
                        <div class="admin-actions">
                            <c:if test="${u.userId != sessionScope.loggedUser.user_id}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/status">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
                                    <input type="hidden" name="id" value="${u.userId}">
                                    <input type="hidden" name="status" value="${u.status == 'Blocked' ? 'Active' : 'Blocked'}">

                                    <button class="btn btn-sm ${u.status == 'Blocked' ? 'btn-outline-success' : 'btn-outline-danger'}">
                                        ${u.status == 'Blocked' ? 'Unblock' : 'Block'}
                                    </button>
                                </form>
                            </c:if>

                            <c:if test="${isAdmin and u.roleName == 'Staff'}">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${pageContext.request.contextPath}/admin/users/edit?id=${u.userId}">
                                    Edit
                                </a>

                                <button type="button" class="btn btn-sm btn-outline-danger"
                                        onclick="confirmDelete(${u.userId})">
                                    Delete
                                </button>
                            </c:if>
                        </div>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty result.items}">
                <tr>
                    <td colspan="6" class="empty-state">No users found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <div class="pagination-row">
        <span>Page ${result.page} / ${result.totalPages}</span>

        <div>
            <c:if test="${result.page > 1}">
                <c:url var="prevUrl" value="">
                    <c:param name="q" value="${q}"/>
                    <c:param name="role" value="${selectedRole}"/>
                    <c:param name="status" value="${selectedStatus}"/>
                    <c:param name="page" value="${result.page - 1}"/>
                </c:url>
                <a class="btn btn-sm btn-outline-secondary" href="<c:out value='${prevUrl}'/>">
                    Previous
                </a>
            </c:if>

            <c:if test="${result.page < result.totalPages}">
                <c:url var="nextUrl" value="">
                    <c:param name="q" value="${q}"/>
                    <c:param name="role" value="${selectedRole}"/>
                    <c:param name="status" value="${selectedStatus}"/>
                    <c:param name="page" value="${result.page + 1}"/>
                </c:url>
                <a class="btn btn-sm btn-outline-secondary" href="<c:out value='${nextUrl}'/>">
                    Next
                </a>
            </c:if>
        </div>
    </div>
</div>

<div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirm Deactivation</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to deactivate this staff account?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/users/delete">
                    <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">
                    <input type="hidden" name="id" id="deleteUserId" value="">
                    <button type="submit" class="btn btn-danger">Deactivate</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function confirmDelete(userId) {
        document.getElementById('deleteUserId').value = userId;
        var myModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
        myModal.show();
    }
</script>

<%@ include file="_end.jsp" %>