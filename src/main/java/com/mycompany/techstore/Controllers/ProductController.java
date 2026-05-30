package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Repositories.CategoryRepository;
import com.mycompany.techstore.Repositories.PriceRangeRepository;
import com.mycompany.techstore.Repositories.ProductRepository;
import com.mycompany.techstore.Repositories.SortOptionRepository;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductController", urlPatterns = {"/product", "/index", ""})
public class ProductController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String NUMBER_REGEX = "^[0-9]+$";

    private void setCatalogAttributes(HttpServletRequest request) {
        request.setAttribute("categories", CategoryRepository.getAll());
        request.setAttribute("products", ProductRepository.getAll());
        request.setAttribute("priceRanges", PriceRangeRepository.getAll());
        request.setAttribute("sortOptions", SortOptionRepository.getAll());
        request.setAttribute("secondaryFilterOptions", CategoryRepository.getSecondaryFilterOptions());
    }

    private void getProductHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCatalogAttributes(request);
        request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/HomePage.jsp").forward(request, response);
    }

    private void getProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || !id.matches(NUMBER_REGEX)) {
            response.sendRedirect(request.getContextPath() + "/product");
            return;
        }

        com.mycompany.techstore.Models.Objects.Product product = ProductRepository.getById(Integer.parseInt(id));
        if (product == null) {
            response.sendError(404, "Product not found");
            return;
        }

        setCatalogAttributes(request);
        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/ProductPage.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("id") == null) {
            getProductHome(request, response);
            return;
        }

        getProductDetail(request, response);
    }
}
