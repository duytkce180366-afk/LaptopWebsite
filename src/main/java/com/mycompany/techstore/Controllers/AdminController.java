package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.services.ProductService;
import com.mycompany.techstore.Models.Objects.Product;
import java.util.List;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminController", urlPatterns = {"/admin"})
public class AdminController extends HttpServlet {

    private final ProductService productService = new ProductService();

    /*
     * Phương thức gửi dữ liệu từ server về client
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (request.getParameter("target")) {
            case null -> {
                // Nếu target là null thì phải làm gì
            }
            case "dashboard" -> {

                request.getRequestDispatcher("/Admin/dashboard.jsp")
                        .forward(request, response);
                // Hiển thị tổng số đơn hàng, lấy từ Service

                // Set property cho từng object - (VD: đơn hàng bao gồm số lượt mua, tổng giá
                // trị, hàng trong kho). Sau đó gửi giá trị qua JSP
            }
            // Viết từng phương thức Create, Update, Edit, Delete vào trong này.
            case "product" -> {

                switch (request.getParameter("action")) {

                    case null -> {

                        List<Product> list = productService.getAll();

                        request.setAttribute("products", list);

                        request.getRequestDispatcher("/Admin/product.jsp")
                                .forward(request, response);

                    }

                    case "create" -> {

                        request.getRequestDispatcher("/Admin/createProduct.jsp")
                                .forward(request, response);

                    }

                    case "edit" -> {

                        int id = Integer.parseInt(request.getParameter("id"));

                        Product product = productService.getById(id);

                        request.setAttribute("product", product);

                        request.getRequestDispatcher("/Admin/editProduct.jsp")
                                .forward(request, response);

                    }

                    case "delete" -> {

                        int id = Integer.parseInt(request.getParameter("id"));

                        productService.deleteProduct(id);

                        response.sendRedirect(request.getContextPath()
                                + "/admin?target=product");

                    }

                    default -> {

                        response.sendRedirect(request.getContextPath()
                                + "/admin?target=product");

                    }

                }

            }
            case "cart" -> {
                // Y chang với "product"
            }
            case "user" -> {
                // Y chang với "product"
            }
            case "review" -> {
                // Y chang với "product"
            }
            default -> {
                response.sendRedirect(request.getContextPath() + "/admin?target=dashboard");
            }
        }
    }

    /**
     * Phương thức gửi dữ liệu từ
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String target = request.getParameter("target");

        String action = request.getParameter("action");

        if ("product".equals(target)) {

            if ("create".equals(action)) {

                int categoryId = Integer.parseInt(request.getParameter("categoryId"));

                int brandId = Integer.parseInt(request.getParameter("brandId"));

                String sku = request.getParameter("sku");

                String productName = request.getParameter("productName");

                String description = request.getParameter("description");

                long price = Long.parseLong(request.getParameter("price"));

                int stock = Integer.parseInt(request.getParameter("stock"));

                String thumbnail = request.getParameter("thumbnail");

                String status = request.getParameter("status");

                productService.createProduct(
                        categoryId,
                        brandId,
                        sku,
                        productName,
                        description,
                        price,
                        stock,
                        thumbnail,
                        status);

                response.sendRedirect(request.getContextPath()
                        + "/admin?target=product");

            } else if ("update".equals(action)) {

                int productId = Integer.parseInt(request.getParameter("productId"));

                int categoryId = Integer.parseInt(request.getParameter("categoryId"));

                int brandId = Integer.parseInt(request.getParameter("brandId"));

                String sku = request.getParameter("sku");

                String productName = request.getParameter("productName");

                String description = request.getParameter("description");

                long price = Long.parseLong(request.getParameter("price"));

                int stock = Integer.parseInt(request.getParameter("stock"));

                String thumbnail = request.getParameter("thumbnail");

                String status = request.getParameter("status");

                productService.updateProduct(
                        productId,
                        categoryId,
                        brandId,
                        sku,
                        productName,
                        description,
                        price,
                        stock,
                        thumbnail,
                        status);

                response.sendRedirect(request.getContextPath()
                        + "/admin?target=product");

            }

        }

    }
}
