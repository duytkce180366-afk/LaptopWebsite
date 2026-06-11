/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Repositories.OrderRepository;
import com.mycompany.techstore.services.OrderService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Nguyen Lam Khang
 */
@WebServlet(name = "PlaceOrderController", urlPatterns = {"/place-order"})
public class PlaceOrderController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int userId = 1;

        String address
                = request.getParameter("address");

        String phone
                = request.getParameter("phone");

        String paymentMethod
                = request.getParameter("paymentMethod");

        

        OrderService orderService = new OrderService();

        boolean success = orderService.placeOrder(
                userId,
                paymentMethod,
                address,
                phone
                );

        if (success) {
            response.sendRedirect("order-history");
        } else {
            response.getWriter().println(
                    "Place Order Failed");
        }
    }
}
