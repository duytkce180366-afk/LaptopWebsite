<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>

    <head>

        <title>Create Voucher</title>

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
                background:#0d6efd;
                color:white;
                font-size:24px;
                font-weight:bold;
            }

            .btn-save{
                width:100%;
            }

        </style>

    </head>

    <body>

        <div class="container">

            <div class="card">

                <div class="card-header">
                    Create Voucher
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
                               value="create">

                        <div class="mb-3">

                            <label class="form-label">
                                Voucher Code
                            </label>

                            <input
                                class="form-control"
                                type="text"
                                name="code"
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

                                <option value="Active">
                                    Active
                                </option>

                                <option value="Inactive">
                                    Inactive
                                </option>

                            </select>

                        </div>

                        <div class="d-flex gap-2">

                            <button
                                class="btn btn-success flex-fill"
                                type="submit">

                                Save Voucher

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