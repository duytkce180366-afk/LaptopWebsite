<%-- 
    Document   : import
    Created on : Jun 16, 2026, 9:00:05 AM
    Author     : Admin
--%>

<%@page contentType="text/html"
        pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <title>Import Inventory</title>
    </head>
    <body>

        <h2>Import Inventory</h2>

        <form method="post">

    <input type="hidden"
           name="productId"
           value="${product.productId}">

    Product:
    ${product.productName}

    <br><br>

    Current Stock:
    ${product.stock}

    <br><br>

    Quantity Import:

    <input type="number"
           name="quantity"
           min="1"
           required>

    <br><br>

    <button type="submit">
        Import Stock
    </button>

</form>
        
        

    </body>
</html>
