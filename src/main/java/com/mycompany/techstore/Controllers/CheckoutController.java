/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.CartItem;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.CartService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author DuyTran
 */
@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {

    private CartService cartService
            = new CartService();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CheckoutController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CheckoutController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/auth?action=signin");
            return;
        }

        User user
                = (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/auth?action=signin");
            return;
        }
        List<CartItem> cartItems
                = cartService.getCartItems(
                        user.getUser_id());

        if (cartItems == null || cartItems.isEmpty()) {

            session.setAttribute(
                    "errorMessage",
                    "Your cart is empty");

            response.sendRedirect(
                    request.getContextPath()
                    + "/cart");

            return;
        }

        double total = 0;

        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        NumberFormat vn
                = NumberFormat.getCurrencyInstance(
                        new Locale("vi", "VN"));
        Double discount
                = (Double) session.getAttribute(
                        "discountAmount");

        Double finalTotal
                = (Double) session.getAttribute(
                        "finalTotal");
        if (discount == null) {
            discount = 0.0;
        }

        if (finalTotal == null) {
            finalTotal = total;
        }
        request.setAttribute(
                "cartTotalFormatted",
                vn.format(total));

        request.setAttribute(
                "cartItems",
                cartItems);

        request.setAttribute(
                "cartTotal",
                total);

        request.setAttribute(
                "user",
                user);
        request.setAttribute(
                "discountFormatted",
                vn.format(discount));

        request.setAttribute(
                "finalTotalFormatted",
                vn.format(finalTotal));

        request.getRequestDispatcher(
                "/WEB-INF/JSPViews/GuestView/Checkout.jsp")
                .forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
