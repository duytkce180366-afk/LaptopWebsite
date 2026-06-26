/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
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

    User loggedUser = (User) request.getSession().getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        return;
    }
    int userId = loggedUser.getUser_id();

    String address      = request.getParameter("address");
    String district     = request.getParameter("district");
    String province     = request.getParameter("province");
    String phone        = request.getParameter("phone");
    String paymentMethod = request.getParameter("paymentMethod");

    OrderService orderService = new OrderService();
    boolean success = orderService.placeOrder(
            userId, paymentMethod, address, district, province, phone
    );

    if (success) {
        response.sendRedirect("order-history");
    } else {
        response.getWriter().println("Place Order Failed");
    }
}
}
