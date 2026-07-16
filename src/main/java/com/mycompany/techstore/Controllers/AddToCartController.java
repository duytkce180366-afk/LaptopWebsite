package com.mycompany.techstore.Controllers;

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

@WebServlet("/cart/add")
public class AddToCartController extends HttpServlet {

    private CartService service
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
        int userId = user.getUser_id();
        int productId
                = Integer.parseInt(
                        request.getParameter(
                                "productId"));

        int quantity
                = Integer.parseInt(
                        request.getParameter(
                                "quantity"));

        double price
                = Double.parseDouble(
                        request.getParameter(
                                "price"));

        boolean success
                = service.addProduct(
                        userId,
                        productId,
                        quantity,
                        price);

        if (!success) {

            session.setAttribute(
                    "cartError",
                    "Only "
                    + service.getProductStock(productId)
                    + " items left in stock!");

            response.sendRedirect(
                    request.getContextPath()
                    + "/product?id="
                    + productId);

            return;
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
