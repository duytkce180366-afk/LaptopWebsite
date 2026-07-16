package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Config.VNPayConfig;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.Repositories.VoucherRepository;

@WebServlet(name = "VNPayController", urlPatterns = {"/vnpay-pay", "/vnpay-return", "/vnpay-retry"})
public class VNPayController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(VNPayController.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        // Sanitize inputs to prevent XSS
        String phone = sanitize(request.getParameter("phone"));
        String province = sanitize(request.getParameter("province"));
        String district = sanitize(request.getParameter("district"));
        String address = sanitize(request.getParameter("address"));

        // Validate required fields
        if (isBlank(phone) || isBlank(province) || isBlank(district) || isBlank(address)) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (!phone.matches("[0-9]{10,11}")) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Parse voucher info safely
        int voucherId = 0;
        double discountAmount = 0;

        String voucherIdStr = request.getParameter("voucherId");
        String discountStr = request.getParameter("discountAmount");

        if (!isBlank(voucherIdStr)) {
            try {
                int parsed = Integer.parseInt(voucherIdStr);
                if (parsed > 0) {
                    voucherId = parsed;
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid voucherId: {0}", voucherIdStr);
            }
        }

        if (!isBlank(discountStr)) {
            try {
                double parsed = Double.parseDouble(discountStr);
                if (parsed >= 0) {
                    discountAmount = parsed;
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid discountAmount: {0}", discountStr);
            }
        }

        // Place order
        OrderService orderService = new OrderService();
        int orderId = orderService.placeOrder(
                loggedUser.getUser_id(), "VNPay",
                address, district, province, phone,
                voucherId, discountAmount
        );

        if (orderId <= 0) {
            logger.log(Level.SEVERE, "Failed to create VNPay order for user {0}", loggedUser.getUser_id());
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Clear voucher session
        session.removeAttribute("voucher");
        session.removeAttribute("discountAmount");
        session.removeAttribute("finalTotal");

        double totalAmount = orderService.getOrderTotal(orderId);
        String redirectUrl = buildVnpayRedirectUrl(orderId, totalAmount, request);
        response.sendRedirect(redirectUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/vnpay-retry".equals(path)) {
            handleRetry(request, response);
            return;
        }

        // Otherwise: this is the VNPay return/callback
        HttpSession session = request.getSession();
        String receivedHash = request.getParameter("vnp_SecureHash");

        // Build fields for signature verification
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            if (!"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key)) {
                fields.put(key, entry.getValue()[0]);
            }
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = fields.get(fieldName);
            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                }
                hashData.append(fieldName).append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }

        String calculatedHash = hmacSHA512(VNPayConfig.HASH_SECRET, hashData.toString());

        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            session.setAttribute("vnpayResult", "invalid");
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        String responseCode = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");

        if (isBlank(txnRef)) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(txnRef);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid vnp_TxnRef: {0}", txnRef);
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        OrderService orderService = new OrderService();

        if ("00".equals(responseCode)) {
            orderService.confirmPaymentSuccess(orderId);
            session.setAttribute("vnpayResult", "success");
        } else {
            // Payment Failed: order stays without stock/voucher ever deducted.
            // User can Retry Payment or Cancel it later from order history.
            orderService.updateOrderStatus(orderId, "Payment Failed");
            session.setAttribute("vnpayResult", "failed");
        }

        response.sendRedirect(request.getContextPath() + "/order-history");
    }

    private void handleRetry(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        if (isBlank(orderIdStr)) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        OrderService orderService = new OrderService();
        Map<String, Object> result = orderService.retryToCheckout(orderId, loggedUser.getUser_id());

        if (result == null) {
            session.setAttribute("adminOrderMessage", "This order cannot be retried.");
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        // Preselect VNPay on checkout, since retry only applies to VNPay orders
        session.setAttribute("preferredPaymentMethod", "VNPAY");

        // Restore voucher into session if still valid
        Object voucherIdObj = result.get("voucherId");
        if (voucherIdObj != null) {
            int voucherId = (Integer) voucherIdObj;
            VoucherRepository voucherRepository = new VoucherRepository();
            Voucher voucher = voucherRepository.getById(voucherId);

            boolean stillValid = voucher != null
                    && voucher.getQuantity() > 0
                    && "Active".equals(voucher.getStatus())
                    && (voucher.getExpiredDate() == null
                    || !voucher.getExpiredDate().before(new java.util.Date()));

            if (stillValid) {
                session.setAttribute("voucher", voucher);
                session.setAttribute("discountAmount", result.get("discountAmount"));
            } else {
                session.removeAttribute("voucher");
                session.removeAttribute("discountAmount");
                session.setAttribute("adminOrderMessage",
                        "Your previous voucher is no longer valid, please re-apply it if needed.");
            }
        } else {
            session.removeAttribute("voucher");
            session.removeAttribute("discountAmount");
        }

        response.sendRedirect(request.getContextPath() + "/checkout");
    }

    // Shared VNPay redirect URL builder, used both for new orders and retried ones.
    private String buildVnpayRedirectUrl(int orderId, double totalAmount, HttpServletRequest request) {

        String ipAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ipAddr) || "::1".equals(ipAddr)) {
            ipAddr = "127.0.0.1";
        }

        String txnRef = String.valueOf(orderId);
        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", VNPayConfig.VERSION);
        vnpParams.put("vnp_Command", VNPayConfig.COMMAND);
        vnpParams.put("vnp_TmnCode", VNPayConfig.TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(Math.round(totalAmount * 100)));
        vnpParams.put("vnp_CurrCode", VNPayConfig.CURRENCY);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        vnpParams.put("vnp_OrderType", VNPayConfig.ORDER_TYPE);
        vnpParams.put("vnp_Locale", VNPayConfig.LOCALE);
        vnpParams.put("vnp_ReturnUrl", VNPayConfig.RETURN_URL);
        vnpParams.put("vnp_IpAddr", ipAddr);
        vnpParams.put("vnp_CreateDate", createDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnpParams.get(fieldName);
            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                    query.append("&");
                }
                hashData.append(fieldName).append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }

        String secureHash = hmacSHA512(VNPayConfig.HASH_SECRET, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return VNPayConfig.PAY_URL + "?" + query;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
