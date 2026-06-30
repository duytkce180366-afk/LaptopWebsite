
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mycompany.techstore.services.CartService;
import jakarta.servlet.http.HttpSession;


@WebServlet("/cart/delete")
public class DeleteCartController extends HttpServlet {

    private CartService cartService
            = new CartService();

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

        String id = request.getParameter("cartItemId");

        if (id != null && !id.isEmpty()) {

            int cartItemId = Integer.parseInt(id);

            boolean success
                    = cartService.deleteCartItem(cartItemId, user.getUser_id());

            System.out.println(success);
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
