/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

import service.ProductService;

@WebServlet("/admin/product/dashboard")
public class DashboardController extends HttpServlet {

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

        System.out.println(
                productService.countActiveProducts());

        System.out.println(
                productService.countInactiveProducts());

        System.out.println(
                productService.countBrands());

        request.setAttribute(
                "activeProducts",
                productService.countActiveProducts());

        request.setAttribute(
                "inactiveProducts",
                productService.countInactiveProducts());

        request.setAttribute(
                "totalBrands",
                productService.countBrands());

        request.getRequestDispatcher(
                "/admin/product/dashboard.jsp")
                .forward(request, response);
    }
}
