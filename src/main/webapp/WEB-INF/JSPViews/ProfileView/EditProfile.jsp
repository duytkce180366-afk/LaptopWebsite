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
                        <h2 class="card-title mb-3">Edit Profile</h2>
                        <form method="post" action="<%= ctx %>/profile?action=edit_profile">
                            <div class="mb-3">
                                <label class="form-label">Full name</label>
                                <input type="text" name="full_name" class="form-control" value="<%= ((com.mycompany.techstore.Models.Objects.User)request.getAttribute("user")).getFull_name() %>" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Phone</label>
                                <input type="text" name="phone" class="form-control" value="<%= ((com.mycompany.techstore.Models.Objects.User)request.getAttribute("user")).getPhone() == null ? "" : ((com.mycompany.techstore.Models.Objects.User)request.getAttribute("user")).getPhone() %>" />
                            </div>
                            <div class="d-flex gap-2 align-items-center">
                                <button class="btn btn-primary primary-action" type="submit">Save changes</button>
                                <a class="btn btn-outline-secondary secondary-action" href="<%= ctx %>/profile">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
