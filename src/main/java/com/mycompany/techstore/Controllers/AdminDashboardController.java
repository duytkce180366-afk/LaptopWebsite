package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.services.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet(urlPatterns = {"/admin", "/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

  private final DashboardService service = new DashboardService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String period = req.getParameter("period");
    LocalDate from = date(req, "from");
    LocalDate to = date(req, "to");

    if ("30".equals(period)) {
      to = LocalDate.now();
      from = to.minusDays(29);
    } else if (from != null && to != null && from.isAfter(to)) {
      req.setAttribute("error", "From date must not be after To date.");
      from = null;
      to = null;
    }
    try {
      req.setAttribute("stats", service.load(from, to));
      req.setAttribute("from", from);
      req.setAttribute("to", to);
      req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/dashboard.jsp").forward(req, res);
    } catch (SQLException ex) {
      throw new ServletException(ex);
    }
  }

  private LocalDate date(HttpServletRequest req, String name) {
    try {
      return LocalDate.parse(req.getParameter(name));
    } catch (Exception ex) {
      return null;
    }
  }
}
