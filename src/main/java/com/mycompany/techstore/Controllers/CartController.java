
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

@WebServlet("/cart")
public class CartController extends HttpServlet {

    private CartService cartService = new CartService();

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

        int userId = user.getUser_id();

        List<CartItem> items
                = cartService.getCartItems(userId);

        request.setAttribute("cartItems", items);
        double total
                = cartService.getCartTotal(userId);

        request.setAttribute(
                "cartTotal",
                total);
        request.getRequestDispatcher(
                "/WEB-INF/JSPViews/GuestView/Cart.jsp")
                .forward(request, response);
        for (CartItem item : items) {
            System.out.println("cartItemId = " + item.getCartItemId());
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
