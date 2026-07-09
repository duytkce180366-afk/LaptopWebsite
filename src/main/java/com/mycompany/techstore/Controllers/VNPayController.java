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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "VNPayController", urlPatterns = {"/vnpay-pay", "/vnpay-return"})
public class VNPayController extends HttpServlet {

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

        String phone = request.getParameter("phone");
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String address = request.getParameter("address");

        int voucherId = 0;
        double discountAmount = 0;

        try {
            voucherId = Integer.parseInt(request.getParameter("voucherId"));
        } catch (Exception ignored) {
        }

        try {
            discountAmount = Double.parseDouble(request.getParameter("discountAmount"));
        } catch (Exception ignored) {
        }

        OrderService orderService = new OrderService();

        int orderId = orderService.placeOrder(
                loggedUser.getUser_id(),
                "VNPay",
                address,
                district,
                province,
                phone,
                voucherId,
                discountAmount
        );

        if (orderId <= 0) {
            response.getWriter().println("Create order failed");
            return;
        }

        session.removeAttribute("voucher");
        session.removeAttribute("discountAmount");
        session.removeAttribute("finalTotal");

        double totalAmount = orderService.getOrderTotal(orderId);

        String txnRef = String.valueOf(orderId);

        String createDate =
                new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(new Date());

        String ipAddr = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ipAddr)
                || "::1".equals(ipAddr)) {

            ipAddr = "127.0.0.1";
        }

        Map<String, String> vnpParams = new HashMap<>();

        vnpParams.put("vnp_Version", VNPayConfig.VERSION);
        vnpParams.put("vnp_Command", VNPayConfig.COMMAND);
        vnpParams.put("vnp_TmnCode", VNPayConfig.TMN_CODE);

        long amount = Math.round(totalAmount * 100);

        vnpParams.put("vnp_Amount", String.valueOf(amount));

        vnpParams.put("vnp_CurrCode", VNPayConfig.CURRENCY);

        vnpParams.put("vnp_TxnRef", txnRef);

        vnpParams.put("vnp_OrderInfo",
                "Thanh toan don hang " + orderId);

        vnpParams.put("vnp_OrderType", VNPayConfig.ORDER_TYPE);

        vnpParams.put("vnp_Locale", VNPayConfig.LOCALE);

        vnpParams.put("vnp_ReturnUrl", VNPayConfig.RETURN_URL);

        vnpParams.put("vnp_IpAddr", ipAddr);

        vnpParams.put("vnp_CreateDate", createDate);

        List<String> fieldNames =
                new ArrayList<>(vnpParams.keySet());

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

                hashData.append(fieldName)
                        .append("=")
                        .append(URLEncoder.encode(value,
                                StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName,
                                StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(value,
                                StandardCharsets.US_ASCII));
            }
        }

        String secureHash =
                hmacSHA512(
                        VNPayConfig.HASH_SECRET,
                        hashData.toString());

        query.append("&vnp_SecureHash=")
                .append(secureHash);

        String paymentUrl =
                VNPayConfig.PAY_URL + "?" + query;

        System.out.println("HASH DATA = " + hashData);
        System.out.println("SECURE HASH = " + secureHash);
        System.out.println(paymentUrl);

        response.sendRedirect(paymentUrl);
    }

    // Phần doGet() và hmacSHA512() sẽ ở phần 2
    @Override
protected void doGet(HttpServletRequest request,
                     HttpServletResponse response)
        throws ServletException, IOException {
     
    HttpSession session = request.getSession();

    String receivedHash = request.getParameter("vnp_SecureHash");

    Map<String, String> fields = new HashMap<>();

    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {

        String fieldName = entry.getKey();

        if (!"vnp_SecureHash".equals(fieldName)
                && !"vnp_SecureHashType".equals(fieldName)) {

            fields.put(fieldName, entry.getValue()[0]);
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

            hashData.append(fieldName)
                    .append("=")
                    .append(URLEncoder.encode(value,
                            StandardCharsets.US_ASCII));
        }
    }

    String calculatedHash =
            hmacSHA512(
                    VNPayConfig.HASH_SECRET,
                    hashData.toString());

    System.out.println("===== VERIFY =====");
    System.out.println("HASH DATA : " + hashData);
    System.out.println("VNPay HASH: " + receivedHash);
    System.out.println("LOCAL HASH: " + calculatedHash);

    if (!calculatedHash.equalsIgnoreCase(receivedHash)) {

        session.setAttribute("vnpayResult", "invalid");

        response.sendRedirect(request.getContextPath() + "/order-history");

        return;
    }

    String responseCode = request.getParameter("vnp_ResponseCode");

    int orderId = Integer.parseInt(request.getParameter("vnp_TxnRef"));

    OrderService orderService = new OrderService();

   if ("00".equals(responseCode)) {

    orderService.confirmPaymentSuccess(orderId);
    session.setAttribute("vnpayResult", "success");

} else {

    orderService.updateOrderStatus(orderId, "Payment Failed");
    session.setAttribute("vnpayResult", "failed");
}
            response.sendRedirect(request.getContextPath() + "/order-history");

}

private String hmacSHA512(String key, String data) {

    try {

        Mac mac = Mac.getInstance("HmacSHA512");

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        key.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA512");

        mac.init(secretKey);

        byte[] hash =
                mac.doFinal(
                        data.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();

        for (byte b : hash) {

            result.append(String.format("%02x", b));
        }

        return result.toString();

    } catch (Exception ex) {

        throw new RuntimeException(ex);
    }
}
}