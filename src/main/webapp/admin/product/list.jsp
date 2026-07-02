
<%-- 
    Document   : list
    Created on : May 30, 2026, 8:46:04 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c"
          uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
    <head>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet">    
        <title>Product List</title>

    <div class="row mb-4">

        <div class="col-md-3">

            <div class="card">

                <div class="card-body">

                    <h5>Total Products</h5>

                    <h2>${totalProducts}</h2>

                </div>

            </div>

        </div>

    </div>


</head>
<body>
    <div class="container-fluid">

        <div class="row">

            <div class="col-md-2">

                <%@ include file="/admin/layout/sidebar.jsp" %>

            </div>

            <div class="col-md-10">
                <h2>Product Management</h2>


                <form method="get">

                    <input type="text"
                           name="keyword"
                           placeholder="Search name or SKU"
                           value="${keyword}">

                    <select name="status">

                        <option value=""
                                ${empty param.status ? 'selected' : ''}>
                            All Status
                        </option>

                        <option value="Active"
                                ${param.status == 'Active' ? 'selected' : ''}>
                            Active
                        </option>

                        <option value="Inactive"
                                ${param.status == 'Inactive' ? 'selected' : ''}>
                            Inactive
                        </option>

                        <option value="Hidden"
                                ${param.status == 'Hidden' ? 'selected' : ''}>
                            Hidden
                        </option>

                        <option value="Out of Stock"
                                ${param.status == 'Out of Stock' ? 'selected' : ''}>
                            Out Of Stock
                        </option>

                    </select>

                    <select name="categoryId">

                        <option value="">
                            All Categories
                        </option>

                        <c:forEach items="${categories}"
                                   var="c">

                            <option value="${c.categoryId}"
                                    ${param.categoryId == c.categoryId.toString()
                                      ? 'selected' : ''}>

                                ${c.categoryName}

                            </option>

                        </c:forEach>

                    </select>

                    <select name="brandId">

                        <option value="">
                            All Brands
                        </option>

                        <c:forEach items="${brands}" var="b">

                            <option value="${b.brandId}"
                                    ${param.brandId == b.brandId.toString()
                                      ? 'selected' : ''}>

                                ${b.brandName}

                            </option>
                        </c:forEach>

                    </select>

                    <button type="submit">
                        Search
                    </button>

                </form>

                <br>

                <c:if test="${sessionScope.role == 'Admin'}">

                    <a href="${pageContext.request.contextPath}/admin/product/create">
                        Add Product
                    </a>

                </c:if>

                <br><br>

                <table border="1">

                    <tr>
                        <th>ID</th>
                        <th>SKU</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Brand</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Status</th>
                        <th>Image</th>
                        <th>Action</th>


                    </tr>

                    <c:forEach items="${products}" var="p">

                        <tr>

                            <td>${p.productId}</td>
                            <td>${p.sku}</td>
                            <td>${p.productName}</td>

                            <td>${p.categoryName}</td>

                            <td>${p.brandName}</td>

                            <td>
                                <fmt:formatNumber
                                    value="${p.price}"
                                    type="number"/>
                                VNĐ
                            </td>
                            <td>${p.stock}</td>
                            <td>

                                <span class="status">
                                    ${p.status}
                                </span>

                            </td>

                            <td>
                                <img src="${p.thumbnail}"
                                     width="120"
                                     height="80">
                            </td>



                            <td>

                                <c:if test="${sessionScope.role == 'Admin'}">

                                    <a href="${pageContext.request.contextPath}/admin/product/update?id=${p.productId}">
                                        Edit
                                    </a>

                                    |

                                    <a href="${pageContext.request.contextPath}/admin/product/delete?id=${p.productId}">
                                        Delete
                                    </a>

                                    |

                                </c:if>

                                <a href="${pageContext.request.contextPath}/staff/product/import?id=${p.productId}">
                                    Import
                                </a>

                            </td>





                        </tr>

                    </c:forEach>

                </table>
                <c:if test="${totalPage > 1}">

                    <div>

                        <c:forEach begin="1"
                                   end="${totalPage}"
                                   var="i">

                            <a href="?page=${i}">
                                ${i}
                            </a>

                        </c:forEach>

                    </div>

                </c:if>

            </div>

        </div>

    </div>
</body>
</html>
