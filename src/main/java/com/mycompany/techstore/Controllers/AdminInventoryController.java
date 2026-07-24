package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.AdminProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/inventory")
public class AdminInventoryController extends HttpServlet {

  private final AdminProductService service = new AdminProductService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      String q = text(req, "q");
      int page = number(req, "page", 1);
      req.setAttribute("result", service.findAll(q, 0, 0, "", page));
      req.setAttribute("receipts", service.recentReceipts());
      req.setAttribute("q", q);
      req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/inventory.jsp").forward(req, res);
    } catch (SQLException ex) {
      throw new ServletException(ex);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    User admin = (User) req.getSession().getAttribute("loggedUser");
    try {
      service.receiveStock(
          number(req, "productId", 0),
          number(req, "quantity", 0),
          text(req, "note"),
          admin.getUser_id());
      req.getSession().setAttribute("adminMessage", "Stock receipt was recorded.");
    } catch (IllegalArgumentException ex) {
      req.getSession().setAttribute("adminError", ex.getMessage());
    } catch (SQLException ex) {
      throw new ServletException(ex);
    }
    res.sendRedirect(req.getContextPath() + "/admin/inventory");
  }

  private String text(HttpServletRequest r, String n) {
    String v = r.getParameter(n);
    return v == null ? "" : v.trim();
  }

  private int number(HttpServletRequest r, String n, int d) {
    try {
      return Integer.parseInt(text(r, n));
    } catch (Exception ex) {
      return d;
    }
  }
}
