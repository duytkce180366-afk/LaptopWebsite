<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>

    <head>

        <title>Voucher Management</title>

        <%@include file="/WEB-INF/JSPViews/global/header.jsp"%>

        <style>

            body{
                background:#f4f6fb;
            }

            .page-title{
                font-weight:700;
                color:#1e293b;
            }

            .card{
                border:none;
                border-radius:15px;
                box-shadow:0 5px 20px rgba(0,0,0,.08);
            }

            .search-box{
                background:white;
                border-radius:15px;
                padding:20px;
                margin-bottom:25px;
            }

            .table{
                background:white;
                border-radius:12px;
                overflow:hidden;
            }

            .table thead{
                background:#0d6efd;
                color:white;
            }

            .table td,
            .table th{
                vertical-align:middle;
            }

            .badge-active{
                background:#198754;
            }

            .badge-inactive{
                background:#dc3545;
            }

            .btn-add{
                margin-bottom:20px;
            }

        </style>

    </head>

    <body>

        <div class="container mt-4">

            <div class="d-flex justify-content-between align-items-center mb-3">

                <h2 class="page-title">
                    Voucher Management
                </h2>

                <a href="${pageContext.request.contextPath}/admin/voucher?action=create"
                   class="btn btn-success btn-add">

                    + Add Voucher

                </a>

            </div>

            <div class="card search-box">

                <form action="${pageContext.request.contextPath}/admin/voucher"
                      method="get"
                      class="row g-3">

                    <input type="hidden"
                           name="action"
                           value="list">

                    <div class="col-md-3">

                        <input type="text"
                               class="form-control"
                               name="keyword"
                               placeholder="Voucher code"
                               value="${param.keyword}">

                    </div>

                    <div class="col-md-2">

                        <select class="form-select"
                                name="status">

                            <option value="">
                                All Status
                            </option>

                            <option value="Active"
                                    ${param.status=="Active"?"selected":""}>
                                Active
                            </option>

                            <option value="Inactive"
                                    ${param.status=="Inactive"?"selected":""}>
                                Inactive
                            </option>

                        </select>

                    </div>

                    <div class="col-md-2">

                        <input type="number"
                               class="form-control"
                               name="discountPercent"
                               placeholder="Discount %"
                               value="${param.discountPercent}"
                               min="1"
                               max="100">

                    </div>

                    <div class="col-md-3">

                        <input type="date"
                               class="form-control"
                               name="expiredDate"
                               value="${param.expiredDate}">

                    </div>

                    <div class="col-md-2 d-grid">

                        <button class="btn btn-primary"
                                type="submit">

                            Search

                        </button>

                    </div>

                    <div class="col-md-2 d-grid">

                        <a href="${pageContext.request.contextPath}/admin/voucher"
                           class="btn btn-secondary">

                            Reset

                        </a>

                    </div>

                </form>

            </div>

            <div class="card p-3">

                <table class="table table-hover table-bordered">

                    <thead>

                        <tr>

                            <th>ID</th>
                            <th>Code</th>
                            <th>Discount</th>
                            <th>Quantity</th>
                            <th>Expired Date</th>
                            <th>Status</th>
                            <th>Created At</th>
                            <th width="160">
                                Action
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        <c:forEach items="${voucherList}" var="v">

                            <tr>

                                <td>${v.voucherId}</td>

                                <td>${v.code}</td>

                                <td>${v.discountPercent}%</td>

                                <td>${v.quantity}</td>

                                <td>${v.expiredDate}</td>

                                <td>

                                    <c:choose>

                                        <c:when test="${v.status=='Active'}">

                                            <span class="badge badge-active">

                                                Active

                                            </span>

                                        </c:when>

                                        <c:otherwise>

                                            <span class="badge badge-inactive">

                                                Inactive

                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </td>

                                <td>${v.createdAt}</td>

                                <td>

                                    <a href="${pageContext.request.contextPath}/admin/voucher?action=edit&id=${v.voucherId}"
                                       class="btn btn-warning btn-sm">

                                        Edit

                                    </a>

                                    <a href="${pageContext.request.contextPath}/admin/voucher?action=delete&id=${v.voucherId}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Delete this voucher?')">

                                        Delete

                                    </a>

                                </td>

                            </tr>

                        </c:forEach>

                        <c:if test="${empty voucherList}">

                            <tr>

                                <td colspan="8"
                                    class="text-center text-muted">

                                    No voucher found.

                                </td>

                            </tr>

                        </c:if>

                    </tbody>

                </table>

            </div>

        </div>

    </body>

</html>