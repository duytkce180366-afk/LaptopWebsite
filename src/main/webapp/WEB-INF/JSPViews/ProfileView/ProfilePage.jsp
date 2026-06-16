<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Profile - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <% String ctx = request.getContextPath(); %>
            <div class="container d-flex align-items-center justify-content-center min-vh-75 py-5">
                <div class="card auth-panel shadow-sm w-100" style="max-width:680px;">
                    <div class="card-body p-4">
                        <h2 class="card-title mb-3">My Profile</h2>
                        <% com.mycompany.techstore.Models.Objects.User user = (com.mycompany.techstore.Models.Objects.User) request.getAttribute("user");%>
                        <dl class="row">
                            <dt class="col-sm-3">Full name</dt>
                            <dd class="col-sm-9"><%= user.getFull_name()%></dd>

                            <dt class="col-sm-3">Email</dt>
                            <dd class="col-sm-9"><%= user.getEmail()%></dd>

                            <dt class="col-sm-3">Phone</dt>
                            <dd class="col-sm-9"><%= user.getPhone() == null ? "" : user.getPhone()%></dd>
                        </dl>

                        <div class="mb-4">
                            <h4>Delivery addresses</h4>
                            <div class="list-group">
                                <% java.util.List<com.mycompany.techstore.Models.Objects.Address> addrs = (java.util.List<com.mycompany.techstore.Models.Objects.Address>) request.getAttribute("addresses");
                                    if (addrs != null && !addrs.isEmpty()) {
                                        for (com.mycompany.techstore.Models.Objects.Address a : addrs) {
                                %>
                                <div class="list-group-item d-flex justify-content-between align-items-start">
                                    <div>
                                        <strong><%= a.getLine1()%></strong>
                                        <div class="small text-muted"><%= a.getLine2() == null ? "" : a.getLine2()%></div>
                                        <div class="small text-muted"><%= a.getCity()%> <%= a.getState() == null ? "" : a.getState()%> <%= a.getPostal_code() == null ? "" : a.getPostal_code()%></div>
                                        <div class="small text-muted"><%= a.getCountry() == null ? "" : a.getCountry()%></div>
                                    </div>
                                    <div class="btn-group">
                                        <a class="btn btn-sm btn-outline-secondary" href="<%= ctx%>/profile?action=edit_address&id=<%= a.getAddress_id()%>">Edit</a>
                                        <form method="post" action="<%= ctx%>/profile" style="display:inline">
                                            <input type="hidden" name="action" value="remove_address" />
                                            <input type="hidden" name="id" value="<%= a.getAddress_id()%>" />
                                            <button class="btn btn-sm btn-outline-danger" type="submit" onclick="return confirm('Delete this address?')">Delete</button>
                                        </form>
                                    </div>
                                </div>
                                <%   }
                                } else { %>
                                <div class="small text-muted">No addresses saved.</div>
                                <% }%>
                            </div>
                            <div class="mt-3">
                                <a class="btn btn-primary" href="<%= ctx%>/profile?action=add_address">Add address</a>
                            </div>
                        </div>

                        <div class="d-flex gap-2">
                            <a class="btn btn-primary" href="<%= ctx%>/profile?action=edit_profile">Edit profile</a>
                            <a class="btn btn-outline-secondary" href="<%= ctx%>/">Back to home</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
