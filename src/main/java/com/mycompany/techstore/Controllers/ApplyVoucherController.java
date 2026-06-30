/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.services.CartService;
import com.mycompany.techstore.services.VoucherService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author DuyTran
 */
@WebServlet("/apply-voucher")
public class ApplyVoucherController extends HttpServlet {

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
            out.println("<title>Servlet ApplyVoucherController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ApplyVoucherController at " + request.getContextPath() + "</h1>");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private VoucherService voucherService
            = new VoucherService();

    private CartService cartService
            = new CartService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        User user
                = (User) session.getAttribute(
                        "loggedUser");

        String code
                = request.getParameter(
                        "voucherCode");

        Voucher voucher
                = voucherService.validateVoucher(
                        code);

        response.setContentType(
                "application/json");

        response.setCharacterEncoding(
                "UTF-8");

        if (voucher == null) {

            response.getWriter().write(
                    """
                {
                  "success": false,
                  "message": "Invalid voucher"
                }
                """
            );

            return;
        }

        double total
                = cartService.getCartTotal(
                        user.getUser_id());

        double discount
                = total
                * voucher.getDiscountPercent()
                / 100.0;

        double finalTotal
                = total - discount;

        session.setAttribute(
                "voucher",
                voucher);

        session.setAttribute(
                "discountAmount",
                discount);

        session.setAttribute(
                "finalTotal",
                finalTotal);

        response.getWriter().write(
                "{"
                + "\"success\":true,"
                + "\"discount\":" + discount + ","
                + "\"finalTotal\":" + finalTotal
                + "}"
        );
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