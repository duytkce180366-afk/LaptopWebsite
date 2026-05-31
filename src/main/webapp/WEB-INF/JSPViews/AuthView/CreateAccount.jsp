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
            <section class="auth-panel">
                <h2>Create an account</h2>
                <% String ctx = request.getContextPath(); %>
                <form class="auth-form" method="post" action="<%= ctx %>/auth?action=signup">
                    <label>
                        <span>Full name</span>
                        <input type="text" name="name" required />
                    </label>
                    <label>
                        <span>Email</span>
                        <input type="email" name="email" required />
                    </label>
                    <label>
                        <span>Password</span>
                        <input type="password" name="password" required />
                    </label>
                    <div class="auth-actions">
                        <button class="primary-action" type="submit">Create account</button>
                        <a class="secondary-action" href="<%= ctx %>/auth?action=signin">Sign in</a>
                    </div>
                </form>

                <div class="oidc-signin">
                    <p>Or sign up using an external provider</p>
                    <a class="oidc-button" href="<%= ctx %>/auth?action=oidc_signin">Sign up with OIDC</a>
                </div>
            </section>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>