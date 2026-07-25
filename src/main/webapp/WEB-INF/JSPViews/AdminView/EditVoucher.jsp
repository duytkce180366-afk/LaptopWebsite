<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c"
          uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>

    <head>

        <title>Edit Voucher</title>

        <%@include file="/WEB-INF/JSPViews/global/header.jsp"%>

        <style>

            body{
                background:#f4f6fb;
            }

            .card{
                max-width:700px;
                margin:40px auto;
                border:none;
                border-radius:15px;
                box-shadow:0 5px 20px rgba(0,0,0,.08);
            }

            .card-header{
                background:#ffc107;
                color:#212529;
                font-size:24px;
                font-weight:bold;
            }

        </style>

    </head>

    <body>

        <div class="container">

            <div class="card">

                <div class="card-header">
                    Edit Voucher
                </div>

                <div class="card-body">

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            ${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/admin/voucher"
                          method="post">

                        <input type="hidden"
                               name="action"
                               value="update">

                        <input type="hidden"
                               name="voucherId"
                               value="${voucher.voucherId}">

                        <div class="mb-3">

                            <label class="form-label">
                                Voucher Code
                            </label>

                            <input
                                class="form-control"
                                type="text"
                                name="code"
                                value="${voucher.code}"
                                maxlength="20"
                                pattern="[A-Z0-9]{5,20}"
                                required>

                        </div>

                        <div class="mb-3">

                            <label class="form-label">
                                Discount (%)
                            </label>

                            <input
                                class="form-control"
                                type="number"
                                name="discountPercent"
                                value="${voucher.discountPercent}"
                                min="1"
                                max="100"
                                required>

                        </div>

                        <div class="mb-3">

                            <label class="form-label">
                                Quantity
                            </label>

                            <input
                                class="form-control"
                                type="number"
                                name="quantity"
                                value="${voucher.quantity}"
                                min="1"
                                required>

                        </div>

                        <div class="mb-3">

                            <label class="form-label">
                                Expired Date
                            </label>

                            <input
                                class="form-control"
                                type="date"
                                name="expiredDate"
                                value="${expiredDate}"
                                min="<%= java.time.LocalDate.now().plusDays(1)%>"
                                required>

                        </div>

                        <div class="mb-4">

                            <label class="form-label">
                                Status
                            </label>

                            <select
                                class="form-select"
                                name="status">

                                <option value="Active"
                                        ${voucher.status=="Active"?"selected":""}>
                                    Active
                                </option>

                                <option value="Inactive"
                                        ${voucher.status=="Inactive"?"selected":""}>
                                    Inactive
                                </option>

                            </select>

                        </div>

                        <div class="d-flex gap-2">

                            <button
                                class="btn btn-warning flex-fill"
                                type="submit">

                                Update Voucher

                            </button>

                            <a href="${pageContext.request.contextPath}/admin/voucher"
                               class="btn btn-secondary flex-fill">

                                Cancel

                            </a>

                        </div>

                    </form>

                </div>

            </div>

        </div>

    </body>

</html>