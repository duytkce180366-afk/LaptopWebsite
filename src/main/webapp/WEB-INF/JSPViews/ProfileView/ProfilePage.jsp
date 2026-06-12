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
                        <% com.mycompany.techstore.Models.Objects.User user = (com.mycompany.techstore.Models.Objects.User) request.getAttribute("user"); %>
                        <dl class="row">
                            <dt class="col-sm-3">Full name</dt>
                            <dd class="col-sm-9"><%= user.getFull_name() %></dd>

                            <dt class="col-sm-3">Email</dt>
                            <dd class="col-sm-9"><%= user.getEmail() %></dd>

                            <dt class="col-sm-3">Phone</dt>
                            <dd class="col-sm-9"><%= user.getPhone() == null ? "" : user.getPhone() %></dd>
                        </dl>

                        <div class="d-flex gap-2">
                            <a class="btn btn-primary" href="<%= ctx %>/profile?action=edit">Edit profile</a>
                            <a class="btn btn-outline-secondary" href="<%= ctx %>/">Back to home</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
