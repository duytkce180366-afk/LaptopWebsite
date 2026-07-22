package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.services.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import java.io.IOException;

@WebServlet(name = "PlaceOrderController", urlPatterns = {"/place-order"})
public class PlaceOrderController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }
        int userId = loggedUser.getUser_id();

        // Sanitize inputs to prevent XSS
        String address = sanitize(request.getParameter("address"));
        String district = sanitize(request.getParameter("district"));
        String province = sanitize(request.getParameter("province"));
        String phone = sanitize(request.getParameter("phone"));
        String paymentMethod = sanitize(request.getParameter("paymentMethod"));

        // Validate required fields
        if (isBlank(address) || isBlank(district) || isBlank(province)) {
            session.setAttribute("orderError", "Please provide a complete shipping address.");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        if (isBlank(phone) || !phone.matches("[0-9]{10,11}")) {
            session.setAttribute("orderError", "Please provide a valid phone number (10-11 digits).");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        if (isBlank(paymentMethod)) {
            session.setAttribute("orderError", "Please select a payment method.");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        // Get voucher info from session
        Voucher voucher = (Voucher) session.getAttribute("voucher");
        Object discountObj = session.getAttribute("discountAmount");
        int voucherId = (voucher != null) ? voucher.getVoucherId() : 0;
        double discountAmount = (discountObj != null) ? (Double) discountObj : 0;

        OrderService orderService = new OrderService();

        // placeOrder: > 0 = success, -1 = system error, -2 = voucher already used
        int orderId = orderService.placeOrder(
                userId, paymentMethod, address, district, province, phone,
                voucherId, discountAmount
        );

        if (orderId > 0) {
            // Clear voucher session
            session.removeAttribute("voucher");
            session.removeAttribute("discountAmount");
            session.removeAttribute("finalTotal");

            if ("VNPay".equals(paymentMethod)) {
                double totalAmount = orderService.getOrderTotal(orderId);
                session.setAttribute("pendingOrderId", orderId);
                session.setAttribute("pendingOrderAmount", totalAmount);
                response.sendRedirect(request.getContextPath() + "/vnpay-pay");
            } else {
                response.sendRedirect(request.getContextPath() + "/order-history");
            }

        } else if (orderId == -2) {
            session.removeAttribute("voucher");
            session.removeAttribute("discountAmount");
            session.removeAttribute("finalTotal");
            session.setAttribute("orderError", "You have already used this voucher. Please choose another one.");
            response.sendRedirect(request.getContextPath() + "/checkout");

        } else {
            session.setAttribute("orderError", "Failed to place order. Please try again.");
            response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return Jsoup.clean(value, Safelist.none());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
