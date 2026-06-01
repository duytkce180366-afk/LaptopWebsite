<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Create account - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <% String ctx = request.getContextPath(); %>
            <div class="container d-flex align-items-center justify-content-center min-vh-75 py-5">
                <div class="card auth-panel shadow-sm w-100" style="max-width:480px;">
                    <div class="card-body p-4">
                        <h2 class="card-title mb-3">Create an account</h2>
                        <form method="post" action="<%= ctx %>/auth?action=signup">
                            <div class="mb-3">
                                <label class="form-label">Full name</label>
                                <input type="text" name="name" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Password</label>
                                <input type="password" name="password" class="form-control" required />
                            </div>
                            <div class="d-flex gap-2 align-items-center">
                                <button class="btn btn-primary primary-action" type="submit">Create account</button>
                                <a class="btn btn-outline-secondary secondary-action" href="<%= ctx %>/auth?action=signin">Sign in</a>
                            </div>
                        </form>

                        <hr class="my-3" />
                        <div class="oidc-signin">
                            <p class="mb-2">Or sign up using an external provider</p>
                            <a class="btn btn-outline-primary w-100" href="<%= ctx %>/auth?action=oidc_signin">Sign up with OIDC</a>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>