<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="editing" value="${not empty staff}" />
<c:set var="pageTitle" value="${editing ? 'Edit Staff' : 'Create Staff'}" />

<%@ include file="_start.jsp" %>

<div class="admin-card">
    <form method="post" action="${pageContext.request.contextPath}/admin/users/${editing ? 'update' : 'create'}">
        <input type="hidden" name="csrfToken" value="${sessionScope.adminCsrfToken}">

        <c:if test="${editing}">
            <input type="hidden" name="id" value="${staff.userId}">
        </c:if>

        <div class="mb-3">
            <label class="form-label">Full name</label>
            <input
                class="form-control"
                name="fullName"
                required
                minlength="2"
                value="<c:out value='${staff.fullName}' />">
        </div>

        <div class="mb-3">
            <label class="form-label">Email</label>
            <input
                class="form-control"
                type="email"
                name="email"
                required
                value="<c:out value='${staff.email}' />">
        </div>

        <div class="mb-3">
            <label class="form-label">Phone</label>
            <input
                class="form-control"
                name="phone"
                value="<c:out value='${staff.phone}' />">
        </div>

        <c:if test="${not editing}">
            <div class="mb-3">
                <label class="form-label">Initial password</label>
                <input
                    class="form-control"
                    type="password"
                    name="password"
                    required
                    minlength="8">
            </div>
        </c:if>

        <div class="admin-actions">
            <button class="btn btn-primary">
                ${editing ? 'Save changes' : 'Create Staff'}
            </button>

            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">
                Cancel
            </a>
        </div>
    </form>
</div>

<%@ include file="_end.jsp" %>