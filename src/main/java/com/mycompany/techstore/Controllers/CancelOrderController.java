/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Repositories.OrderRepository;
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
@WebServlet(name = "CancelOrderController", urlPatterns = {"/cancel-order"})
public class CancelOrderController
        extends HttpServlet {

    OrderRepository repo =
            new OrderRepository();

 @Override
protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    String id = request.getParameter("id");
    String note = request.getParameter("note");

    System.out.println("ID = " + id);
    System.out.println("NOTE = " + note);

    int orderId = Integer.parseInt(id);

    boolean success =
            repo.cancelOrder(orderId, note);

    System.out.println("SUCCESS = " + success);

    response.sendRedirect("order-history");
}
}