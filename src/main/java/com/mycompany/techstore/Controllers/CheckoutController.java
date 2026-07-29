package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.Address;
import com.mycompany.techstore.Models.Objects.CartItem;
import com.mycompany.techstore.Repositories.AddressRepository;
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
import java.util.ArrayList;
import java.util.Locale;

@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {

    private final CartService cartService
            = new CartService();
    private final AddressRepository addressRepo = new AddressRepository();

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
        String cartItemId
                = request.getParameter("cartItemId");

        List<CartItem> cartItems;

        if (cartItemId != null) {

            CartItem item
                    = cartService.getCartItemById(
                            Integer.parseInt(cartItemId),
                            user.getUser_id());

            cartItems = new ArrayList<>();

            if (item != null) {
                cartItems.add(item);
            }

            session.setAttribute("checkoutCartItemId",
                    Integer.valueOf(cartItemId));

        } else {

            cartItems
                    = cartService.getCartItems(
                            user.getUser_id());

            session.removeAttribute(
                    "checkoutCartItemId");
        }

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
                         Locale.of("vi", "VN"));
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
        Address defaultAddress
                = addressRepo.getDefaultAddress(
                        user.getUser_id());

        request.setAttribute(
                "defaultAddress",
                defaultAddress);

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

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
