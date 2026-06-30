package com.mycompany.techstore.Controllers;
 
import com.mycompany.techstore.Models.Objects.User;
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
 
        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
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
 
        // Get voucher info from session (set by ApplyVoucherController)
        HttpSession session = request.getSession();
        com.mycompany.techstore.Models.Objects.Voucher voucher =
                (com.mycompany.techstore.Models.Objects.Voucher) session.getAttribute("voucher");
        Object discountObj = session.getAttribute("discountAmount");
 
        int voucherId = (voucher != null) ? voucher.getVoucherId() : 0;
        double discountAmount = (discountObj != null) ? (Double) discountObj : 0;
 
        OrderService orderService = new OrderService();
        boolean success = orderService.placeOrder(
                userId, paymentMethod, address, district, province, phone,
                voucherId, discountAmount
        );
 
        if (success) {
            // Clear voucher session after order placed
            session.removeAttribute("voucher");
            session.removeAttribute("discountAmount");
            session.removeAttribute("finalTotal");
            response.sendRedirect("order-history");
        } else {
            response.getWriter().println("Place Order Failed");
        }
    }
}

