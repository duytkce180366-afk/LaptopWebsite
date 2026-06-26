/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.Order;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.OrderRepository;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author Nguyen Lam Khang
 */
@WebServlet(name = "OrderHistoryController", urlPatterns = {"/order-history"})
public class OrderHistoryController
        extends HttpServlet {

    private OrderRepository repo =
            new OrderRepository();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

User loggedUser = (User) request.getSession().getAttribute("loggedUser");
if (loggedUser == null) {
    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
    return;
}
int userId = loggedUser.getUser_id();
        List<Order> orders =
                repo.getOrdersByUser(userId);

        request.setAttribute(
                "orders",
                orders);

        request.getRequestDispatcher(
                "order-history.jsp")
                .forward(request, response);
    }
}