<%-- 
    Document   : sidebar
    Created on : Jun 4, 2026, 8:15:31 AM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <div class="bg-dark text-white vh-100 p-3">

    <h4>Admin Panel</h4>

    <hr>

    <ul class="nav flex-column">

        <li class="nav-item">
            <a class="nav-link text-white"
               href="${pageContext.request.contextPath}/admin/products">
                Products
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link text-white"
               href="#">
                Categories
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link text-white"
               href="#">
                Brands
            </a>
        </li>

    </ul>

</div>
</html>
