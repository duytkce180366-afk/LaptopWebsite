<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <c:set var="pageTitle" value="Dashboard" />
    <%@ include file="_start.jsp" %>

        <div class="admin-card">
            <form class="admin-filters" method="get">
                <div>
                    <label class="form-label">From</label>
                    <input class="form-control" type="date" name="from" value="${from}">
                </div>

                <div>
                    <label class="form-label">To</label>
                    <input class="form-control" type="date" name="to" value="${to}">
                </div>

                <div class="admin-actions" style="grid-column: span 3;">
                    <button class="btn btn-primary">Apply period</button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/dashboard?period=30">
                        Last 30 days
                    </a>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/dashboard">
                        All time
                    </a>
                    <a class="btn btn-outline-success"
                        href="${pageContext.request.contextPath}/admin/reports?from=${from}&to=${to}">
                        Open report
                    </a>
                </div>
            </form>
        </div>

        <div class="metric-grid">
            <div class="metric">
                <small>Delivered revenue</small>
                <strong>${stats.revenue}</strong>
            </div>

            <div class="metric">
                <small>Orders</small>
                <strong>${stats.orders}</strong>
            </div>

            <div class="metric">
                <small>Customers</small>
                <strong>${stats.users}</strong>
            </div>

            <div class="metric">
                <small>Products</small>
                <strong>${stats.products}</strong>
            </div>

            <div class="metric">
                <small>Reviews</small>
                <strong>${stats.reviews}</strong>
            </div>
        </div>

        <div class="dashboard-grid">
            <section class="admin-card">
                <h2 class="h5">Orders by status</h2>
                <table class="admin-table">
                    <tbody>
                        <c:forEach var="r" items="${stats.orderStatuses}">
                            <tr>
                                <td>
                                    <span class="status-pill status-${r.label}">
                                        <c:out value="${r.label}" />
                                    </span>
                                </td>
                                <td class="text-end">${r.value}</td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty stats.orderStatuses}">
                            <tr>
                                <td class="empty-state">No order data yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>

            <section class="admin-card">
                <h2 class="h5">Top selling products</h2>
                <table class="admin-table">
                    <tbody>
                        <c:forEach var="r" items="${stats.topProducts}">
                            <tr>
                                <td>
                                    <c:out value="${r.label}" />
                                </td>
                                <td class="text-end">${r.value} sold</td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty stats.topProducts}">
                            <tr>
                                <td class="empty-state">No delivered sales yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>

            <section class="admin-card">
                <h2 class="h5">Low stock</h2>
                <table class="admin-table">
                    <tbody>
                        <c:forEach var="r" items="${stats.lowStock}">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${isAdmin}">
                                            <a href="${pageContext.request.contextPath}/admin/products/edit?id=${r.id}">
                                                <c:out value="${r.label}" />
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${r.label}" />
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">${r.value} left</td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty stats.lowStock}">
                            <tr>
                                <td class="empty-state">Stock levels are healthy.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>

            <section class="admin-card">
                <h2 class="h5">Recent orders</h2>
                <table class="admin-table">
                    <tbody>
                        <c:forEach var="r" items="${stats.recentOrders}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${r.id}">
                                        #${r.id}
                                    </a>
                                    &middot;
                                    <c:out value="${r.label}" />
                                </td>
                                <td>
                                    <c:out value="${r.status}" />
                                </td>
                                <td>${r.value}</td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty stats.recentOrders}">
                            <tr>
                                <td class="empty-state">No orders yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>

            <section class="admin-card">
                <h2 class="h5">Revenue by day</h2>
                <table class="admin-table">
                    <tbody>
                        <c:forEach var="r" items="${stats.dailyRevenue}">
                            <tr>
                                <td>${r.label}</td>
                                <td class="text-end">${r.value}</td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty stats.dailyRevenue}">
                            <tr>
                                <td class="empty-state">No delivered revenue yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>

            <section class="admin-card">
                <h2 class="h5">Recent admin activity</h2>
                <table class="admin-table">
                    <tbody>
                        <c:forEach var="r" items="${stats.recentAudits}">
                            <tr>
                                <td>
                                    <c:out value="${r.label}" />
                                </td>
                                <td>
                                    <c:out value="${r.status}" />
                                </td>
                                <td>
                                    <c:out value="${r.detail}" />
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty stats.recentAudits}">
                            <tr>
                                <td class="empty-state">No admin actions recorded yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>
        </div>

        <%@ include file="_end.jsp" %>