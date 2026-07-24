<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}" /> - TechStore Admin</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/lib/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>

<body>
    <div class="admin-layout">
        <aside class="admin-sidebar">
            <a class="admin-brand" href="${pageContext.request.contextPath}/admin/dashboard">
                TechStore <span><c:out value="${backOfficeRole}" /></span>
            </a>

            <nav>
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>

                <c:if test="${isAdmin}">
                    <a href="${pageContext.request.contextPath}/admin/products">Products</a>
                </c:if>

                <a href="${pageContext.request.contextPath}/admin/inventory">Inventory</a>
                <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
                <a href="${pageContext.request.contextPath}/admin/users">Users</a>
                <a href="${pageContext.request.contextPath}/admin/reviews">Reviews</a>
                <a href="${pageContext.request.contextPath}/admin/reports">Reports</a>
            </nav>

            <div class="sidebar-bottom">
                <a href="${pageContext.request.contextPath}/home">View store</a>
                <a href="${pageContext.request.contextPath}/auth?action=logout">Sign out</a>
            </div>
        </aside>

        <main class="admin-main">
            <header class="admin-topbar">
                <div>
                    <small><c:out value="${backOfficeRole}" /> workspace</small>
                    <h1><c:out value="${pageTitle}" /></h1>
                </div>

                <div class="admin-user">
                    <c:out value="${sessionScope.loggedUser.full_name}" />
                </div>
            </header>

            <c:if test="${not empty sessionScope.adminMessage}">
                <div class="alert alert-success">
                    <c:out value="${sessionScope.adminMessage}" />
                </div>
                <c:remove var="adminMessage" scope="session" />
            </c:if>

            <c:if test="${not empty sessionScope.adminError}">
                <div class="alert alert-danger">
                    <c:out value="${sessionScope.adminError}" />
                </div>
                <c:remove var="adminError" scope="session" />
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">
                    <c:out value="${error}" />
                </div>
            </c:if>