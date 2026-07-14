package com.mycompany.techstore.Controllers;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.services.CartService;
import com.mycompany.techstore.services.VoucherService;
import com.mycompany.techstore.Repositories.OrderRepository;
import java.io.IOException;
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
    private OrderRepository orderRepository
            = new OrderRepository();
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
        // Block if this user has already used this voucher before
        if (orderRepository.isVoucherUsedByUser(user.getUser_id(), voucher.getVoucherId())) {
            response.getWriter().write(
                    """
                {
                  "success": false,
                  "message": "You have already used this voucher"
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

        // NOTE: quantity is NOT decreased here anymore.
        // The actual deduction happens only when the order is truly placed:
        // - COD/other methods: deducted in OrderRepository.placeOrder()
        // - VNPay: deducted in OrderRepository.confirmPaymentSuccess()
        // This avoids double-deducting (once on Apply, once on real order completion)
        // and avoids wasting a voucher use if the user applies it but never checks out.

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
                + "\"voucherId\":" + voucher.getVoucherId() + ","
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