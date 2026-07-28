package com.mycompany.techstore.Controllers;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
@WebServlet(
        name = "RequestReturnController",
        urlPatterns = {"/request-return"})
public class RequestReturnController extends HttpServlet {
    OrderService orderService = new OrderService();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        String reason = request.getParameter("reason");
        int orderId = Integer.parseInt(id);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            response.sendError(401);
            return;
        }

        int result = orderService.requestReturn(orderId, user.getUser_id(), reason);

        if (result == -4) {
            session.setAttribute("orderError",
                "The return window for this order has expired (3 days after delivery).");
        } else if (result != 1) {
            session.setAttribute("orderError", "Unable to request a return for this order.");
        }

        response.sendRedirect(request.getContextPath() + "/order-history");
    }
}