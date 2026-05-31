<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Reset password - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <section class="auth-panel">
                <h2>Reset password</h2>
                <% String ctx = request.getContextPath(); %>
                <form class="auth-form" method="post" action="<%= ctx %>/auth?action=resetpwd">
                    <label>
                        <span>New password</span>
                        <input type="password" name="newpwd" required />
                    </label>
                    <label>
                        <span>Repeat password</span>
                        <input type="password" name="repeatPwd" required />
                    </label>
                    <div class="auth-actions">
                        <button class="primary-action" type="submit">Change password</button>
                        <a class="secondary-action" href="<%= ctx %>/auth?action=signin">Back to sign in</a>
                    </div>
                </form>
            </section>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>