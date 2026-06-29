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

@WebServlet("/apply-voucher")
public class ApplyVoucherController extends HttpServlet {

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

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
