<%@page import="org.jsoup.safety.Safelist"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Reset password - Tech Store</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <% String ctx = request.getContextPath();%>
            <div class="container d-flex align-items-center justify-content-center min-vh-75 py-5">
                <div class="card auth-panel shadow-sm w-100" style="max-width:480px;">
                    <div class="card-body p-4">
                        <h2 class="card-title mb-3">Reset password</h2>
                        <%
                            String error = request.getParameter("error");
                            if (error != null) {
                        %>
                        <div class="alert alert-danger" role="alert">
                            Error: <%= Jsoup.clean(error, Safelist.basic())%>
                        </div>
                        <% }%>                         
                        <form method="post" action="<%= ctx%>/auth?action=resetpwd">
                            <div class="mb-3">
                                <label class="form-label">OTP (Check via email)</label>
                                <input type="text" name="otp" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">New password</label>
                                <input type="password" name="newpwd" class="form-control" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Repeat password</label>
                                <input type="password" name="repeatPwd" class="form-control" required />
                            </div>
                            <div class="d-flex gap-2 align-items-center">
                                <button class="btn btn-primary primary-action" type="submit">Change password</button>
                                <a class="btn btn-outline-secondary secondary-action" href="<%= ctx%>/auth?action=signin">Back to sign in</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>