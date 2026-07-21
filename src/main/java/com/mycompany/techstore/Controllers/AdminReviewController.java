package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.AdminReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/admin/reviews", "/admin/reviews/*"})
public class AdminReviewController extends HttpServlet {

    private final AdminReviewService service = new AdminReviewService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String q = text(req, "q"), status = text(req, "status");
            int rating = number(req, "rating", 0), page = number(req, "page", 1);
            req.setAttribute("result", service.findAll(q, rating, status, page));
            req.setAttribute("q", q);
            req.setAttribute("selectedRating", rating);
            req.setAttribute("selectedStatus", status);
            req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/reviews.jsp").forward(req, res);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("loggedUser");
        try {
            service.setStatus(number(req, "id", 0), text(req, "status"), admin.getUser_id());
            req.getSession().setAttribute("adminMessage", "Review visibility was updated.");
        } catch (IllegalArgumentException ex) {
            req.getSession().setAttribute("adminError", ex.getMessage());
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
        res.sendRedirect(req.getContextPath() + "/admin/reviews");
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
