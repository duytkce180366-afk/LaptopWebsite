package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.OrderRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
    name = "CancelOrderController",
    urlPatterns = {"/cancel-order"})
public class CancelOrderController extends HttpServlet {

  OrderRepository repo = new OrderRepository();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String id = request.getParameter("id");
    String note = request.getParameter("note");

    System.out.println("ID = " + id);
    System.out.println("NOTE = " + note);

    int orderId = Integer.parseInt(id);

    User user = (User) request.getSession().getAttribute("loggedUser");
    if (user == null) {
      response.sendError(401);
      return;
    }
    boolean success = repo.cancelOrder(orderId, user.getUser_id(), note);

    System.out.println("SUCCESS = " + success);

    response.sendRedirect(request.getContextPath() + "/order-history");
  }
}
