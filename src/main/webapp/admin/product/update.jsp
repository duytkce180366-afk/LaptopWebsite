<%-- 
    Document   : update
    Created on : May 30, 2026, 8:46:30 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">
        <title>Update Product</title>
    </head>
    <body>
        <div class="container-fluid mt-4">

            <h2>Update Product</h2>

            <p style="color:red;">
                ${error}
            </p>

            <form method="post"
                  enctype="multipart/form-data">

                <input type="hidden"
                       name="productId"
                       value="${product.productId}">

                Category

                <select name="categoryId">

                    <c:forEach items="${categories}" var="c">

                        <option value="${c.categoryId}"
                                ${c.categoryId == product.categoryId ? 'selected' : ''}>

                            ${c.categoryName}

                        </option>

                    </c:forEach>

                </select>

                <br><br>

                Brand

                <select name="brandId">

                    <c:forEach items="${brands}" var="b">

                        <option value="${b.brandId}"
                                ${b.brandId == product.brandId ? 'selected' : ''}>

                            ${b.brandName}

                        </option>

                    </c:forEach>

                </select>

                <br><br>

                SKU

                <input type="text"
                       name="sku"
                       value="${product.sku}"
                       required>

                <br><br>

                Product Name

                <input type="text"
                       name="productName"
                       value="${product.productName}"
                       required>

                <br><br>

                Description

                <textarea name="description">${product.description}</textarea>

                <br><br>

                Price

                <input type="number"
                       step="0.01"
                       name="price"
                       value="${product.price}"
                       required>

                <br><br>

                Stock

                <input type="number"
                       name="stock"
                       value="${product.stock}"
                       required>

                <br><br>            

                Current Image

                <br>

                <img src="${product.thumbnail}"
                     width="200">

                <br><br>

                Change Image

                <input type="file"
                       name="image">

                <br><br>

                <img id="preview"
                     width="200"
                     style="display:none;">

                <br><br>

                Status

                <select name="status">

                    <option value="Active"
                            ${product.status=='Active'?'selected':''}>
                        Active
                    </option>

                    <option value="Out of Stock"
                            ${product.status=='Out of Stock'?'selected':''}>
                        Out of Stock
                    </option>

                    <option value="Inactive"
                            ${product.status=='Inactive'?'selected':''}>
                        Inactive
                    </option>

                    <option value="Hidden"
                            ${product.status=='Hidden'?'selected':''}>
                        Hidden
                    </option>

                </select>

                <br><br>

                <button type="submit">
                    Update Product
                </button>

            </form>

            <br>

            <a href="${pageContext.request.contextPath}/admin/products">
                Back To List
            </a>
            <script>

                document.querySelector(
                        'input[name="image"]')
                        .addEventListener(
                                'change',
                                function (e) {

                                    const file =
                                            e.target.files[0];

                                    if (file) {

                                        const reader =
                                                new FileReader();

                                        reader.onload =
                                                function (event) {

                                                    const img =
                                                            document.getElementById(
                                                                    "preview");

                                                    img.src =
                                                            event.target.result;

                                                    img.style.display =
                                                            "block";
                                                };

                                        reader.readAsDataURL(file);
                                    }
                                });

            </script>
        </div>
    </body>
</html>
