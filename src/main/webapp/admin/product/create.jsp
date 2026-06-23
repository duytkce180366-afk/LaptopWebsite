<%-- 
    Document   : create
    Created on : May 30, 2026, 8:46:19 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c"
          uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">
        <title>Create Product</title>
    </head>
    <body>
        <div class="container-fluid mt-4">
            <h2>Add Product</h2>

            <p style="color:red;">
                ${error}
            </p>

            <form method="post"
                  enctype="multipart/form-data">

                Category

                <select name="categoryId" required>

                    <option value="" selected disabled>
                        Select Category
                    </option>

                    <c:forEach items="${categories}" var="c">

                        <option value="${c.categoryId}">
                            ${c.categoryName}
                        </option>

                    </c:forEach>

                </select>

                <br><br>

                Brand

                <select name="brandId" required>

                    <option value="" selected disabled>
                        Select Brand
                    </option>

                    <c:forEach items="${brands}" var="b">

                        <option value="${b.brandId}">
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

                <textarea name="description">
                    ${product.description}
                </textarea>

                <br><br>

                Price

                <input type="number"
                       step="1"
                       min="1"
                       name="price"
                       value="${product.price}"
                       required>

                <br><br>

                Stock

                <input type="number"
                       min="0"
                       name="stock"
                       value="${product.stock}"
                       required>

                <br><br>

                Upload Image

                <input type="file"
                       name="image"
                       id="imageInput"
                       accept="image/*">

                <br><br>

                <img id="preview"
                     width="200"
                     style="display:none;">



                <br><br>


                Status

                <select name="status">

                    <option value="Active">
                        Active
                    </option>

                    <option value="Out of Stock">
                        Out of Stock
                    </option>

                    <option value="Inactive">
                        Inactive
                    </option>

                    <option value="Hidden">
                        Hidden
                    </option>

                </select>

                <br><br>

                <button type="submit">
                    Save Product
                </button>

            </form>

            <br>

            <a href="${pageContext.request.contextPath}/admin/products">
                Back
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

            <script>

                document.getElementById("imageInput")
                        .addEventListener("change", function (e) {

                            const file = e.target.files[0];

                            if (file) {

                                const reader = new FileReader();

                                reader.onload = function (event) {

                                    const img =
                                            document.getElementById("preview");

                                    img.src = event.target.result;

                                    img.style.display = "block";
                                };

                                reader.readAsDataURL(file);
                            }

                        });

            </script>
        </div>
    </body>
</html>
