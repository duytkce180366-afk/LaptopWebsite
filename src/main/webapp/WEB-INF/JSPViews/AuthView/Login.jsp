<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Tech Store</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <% String ctx = request.getContextPath();%>
            <div class="container d-flex align-items-center justify-content-center min-vh-75 py-5">
                <div class="card auth-panel shadow-sm w-100" style="max-width:480px;">
                    <div class="card-body p-4">
                        <h2 class="card-title mb-3">Sign in to Tech Store</h2>
                        <%
                            String error = request.getParameter("error");
                            if (error != null) {
                        %>
                        <div class="alert alert-danger" role="alert">
                            Error: <%= error%>
                        </div>
                        <% }%>
                        <form method="post" action="<%= ctx%>/auth?action=signin">
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Password</label>
                                <input type="password" name="password" class="form-control" required />
                            </div>
                            <div class="d-flex gap-2 align-items-center">
                                <button class="btn btn-primary primary-action" type="submit">Sign in</button>
                                <a class="btn btn-outline-secondary secondary-action" href="<%= ctx%>/auth?action=signup">Create account</a>
                            </div>
                        </form>
                        <hr class="my-3" />
                        <div class="oidc-signin mb-2">
                            <p class="mb-2">Or sign in with an external provider</p>
                            <a class="btn btn-outline-primary w-100" href="<%= ctx%>/auth?action=oidc_signin">Sign in with SSO</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
