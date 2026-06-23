package controller.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import service.ProductService;
import model.Product;

@WebServlet("/staff/product/import")
public class ProductImportController
        extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {

        productService
                = new ProductService();

    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int id
                = Integer.parseInt(
                        request.getParameter("id"));

        Product p
                = productService.getProductById(id);

        request.setAttribute(
                "product",
                p);

        request.getRequestDispatcher(
                "/staff/product/import.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int productId
                = Integer.parseInt(
                        request.getParameter(
                                "productId"));

        int quantity
                = Integer.parseInt(
                        request.getParameter(
                                "quantity"));

        productService.importStock(
                productId,
                quantity);

        response.sendRedirect(
                request.getContextPath()
                + "/admin/products");
    }
}
