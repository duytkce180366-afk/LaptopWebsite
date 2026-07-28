package com.mycompany.techstore.Controllers;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet(
        name = "ConfirmDeliveryController",
        urlPatterns = {"/confirm-delivery"})
public class ConfirmDeliveryController extends HttpServlet {
    OrderService orderService = new OrderService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        int orderId = Integer.parseInt(id);
        User user = (User) request.getSession().getAttribute("loggedUser");
        if (user == null) {
            response.sendError(401);
            return;
        }
        boolean success = orderService.confirmDelivery(orderId, user.getUser_id());
        System.out.println("CONFIRM DELIVERY SUCCESS = " + success);
        response.sendRedirect(request.getContextPath() + "/order-history");
    }
}