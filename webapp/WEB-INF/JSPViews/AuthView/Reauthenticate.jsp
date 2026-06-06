<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reauthenticate</title>
</head>
<body>
<h2>Reauthenticate</h2>
<p>Please enter your current password to continue with sensitive operations.</p>
<% if (request.getAttribute("error") != null) { %>
    <div style="color: red;"><%= request.getAttribute("error") %></div>
<% } %>
<form method="post" action="${pageContext.request.contextPath}/auth?action=reauth">
    <label for="current_password">Current password:</label>
    <input type="password" id="current_password" name="current_password" required />
    <button type="submit">Verify</button>
</form>
</body>
</html>