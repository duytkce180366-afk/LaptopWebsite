/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.CartRepository;
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
@WebServlet("/cart/update")
public class UpdateCartController extends HttpServlet {

    private CartRepository repo
            = new CartRepository();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
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

        String cartItemIdStr = request.getParameter("cartItemId");
        String quantityStr = request.getParameter("quantity");

        if (cartItemIdStr != null
                && !cartItemIdStr.isEmpty()
                && quantityStr != null
                && !quantityStr.isEmpty()) {

            int cartItemId = Integer.parseInt(cartItemIdStr);
            int quantity = Integer.parseInt(quantityStr);
            int productId
                    = repo.getProductIdByCartItemId(cartItemId);

            int stock
                    = repo.getProductStock(productId);

            if (quantity > stock) {

                request.getSession().setAttribute(
                        "cartError",
                        "Only " + stock + " items left in stock!");

                response.sendRedirect(
                        request.getContextPath()
                        + "/cart");

                return;
            }
            repo.updateQuantity(
                    cartItemId,
                    quantity,
                    user.getUser_id());
        }

        response.sendRedirect(
                request.getContextPath()
                + "/cart");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
