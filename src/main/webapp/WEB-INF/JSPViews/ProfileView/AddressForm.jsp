<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Address - TechStore</title>
        <%@include file="/WEB-INF/JSPViews/global/header.jsp" %>
    </head>
    <body id="top">
        <%@include file="/WEB-INF/JSPViews/global/nav.jsp" %>
        <main class="auth-shell">
            <% String ctx = request.getContextPath();%>
            <div class="container d-flex align-items-center justify-content-center min-vh-75 py-5">
                <div class="card auth-panel shadow-sm w-100" style="max-width:680px;">
                    <div class="card-body p-4">
                        <h2 class="card-title mb-3">Address Form</h2>

                        <% com.mycompany.techstore.Models.Objects.Address addr = (com.mycompany.techstore.Models.Objects.Address) request.getAttribute("address");%>
                        <form method="post" action="<%= ctx%>/profile?action=<%= request.getParameter("action")%>">
                            <input type="hidden" name="address_id" value="<%= addr == null ? -1 : addr.getAddress_id()%>" />

                            <div class="mb-3">
                                <label class="form-label">Address line 1</label>
                                <input type="text" name="line1" class="form-control" value="<%= addr == null ? "" : addr.getLine1()%>" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Address line 2</label>
                                <input type="text" name="line2" class="form-control" value="<%= addr == null ? "" : addr.getLine2()%>" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">City</label>
                                <input type="text" name="city" class="form-control" value="<%= addr == null ? "" : addr.getCity()%>" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">State</label>
                                <input type="text" name="state" class="form-control" value="<%= addr == null ? "" : addr.getState()%>" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Postal code</label>
                                <input type="text" name="postal_code" class="form-control" value="<%= addr == null ? "" : addr.getPostal_code()%>" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Country</label>
                                <input type="text" name="country" class="form-control" value="<%= addr == null ? "" : addr.getCountry()%>" />
                            </div>
                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" name="is_default" id="is_default" <%= (addr != null && addr.isIs_default()) ? "checked" : ""%> />
                                <label class="form-check-label" for="is_default">Set as default</label>
                            </div>
                            <div class="d-flex gap-2 align-items-center">
                                <button class="btn btn-primary primary-action" type="submit">Save</button>
                                <a class="btn btn-outline-secondary secondary-action" href="<%= ctx%>/profile">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
        <%@include file="/WEB-INF/JSPViews/global/footer.jsp" %>
    </body>
</html>
