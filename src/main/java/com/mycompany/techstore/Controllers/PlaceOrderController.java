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
import java.io.IOException;
 
/**
 * @author Nguyen Lam Khang
 */
@WebServlet(name = "PlaceOrderController", urlPatterns = {"/place-order"})
public class PlaceOrderController extends HttpServlet {
 
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
 
        HttpSession session = request.getSession();
 
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }
        int userId = loggedUser.getUser_id();
 
        String address       = request.getParameter("address");
        String district      = request.getParameter("district");
        String province      = request.getParameter("province");
        String phone         = request.getParameter("phone");
        String paymentMethod = request.getParameter("paymentMethod");
 
        // Get voucher info from session
        Voucher voucher = (Voucher) session.getAttribute("voucher");
        Object discountObj = session.getAttribute("discountAmount");
 
        int voucherId = (voucher != null) ? voucher.getVoucherId() : 0;
        double discountAmount = (discountObj != null) ? (Double) discountObj : 0;
 
        OrderService orderService = new OrderService();
 
        // placeOrder now returns orderId (> 0 = success, -1 = failed)
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
                // Store order info for VNPay
                double totalAmount = orderService.getOrderTotal(orderId);
                session.setAttribute("pendingOrderId",     orderId);
                session.setAttribute("pendingOrderAmount", totalAmount);
                response.sendRedirect(request.getContextPath() + "/vnpay-pay");
            } else {
                response.sendRedirect(request.getContextPath() + "/order-history");
            }
        } else {
            response.getWriter().println("Place Order Failed");
        }
    }
}
 