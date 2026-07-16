package com.mycompany.techstore.Config;

public class VNPayConfig {

    public static final String TMN_CODE =
            (System.getenv("VNPAY_TMN_CODE") != null)
                    ? System.getenv("VNPAY_TMN_CODE")
                    : "O3OS6OKO"; // fallback for local sandbox testing only

    public static final String HASH_SECRET =
            (System.getenv("VNPAY_HASH_SECRET") != null)
                    ? System.getenv("VNPAY_HASH_SECRET")
                    : "PZOXVK1ISZDLJXPC2DFVW386VWCA3OEC"; // fallback for local sandbox testing only

    public static final String PAY_URL =
            (System.getenv("VNPAY_PAY_URL") != null)
                    ? System.getenv("VNPAY_PAY_URL")
                    : "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static final String RETURN_URL =
            (System.getenv("VNPAY_RETURN_URL") != null)
                    ? System.getenv("VNPAY_RETURN_URL")
                    : "http://localhost:8080/vnpay-return";

    public static final String VERSION     = "2.1.0";
    public static final String COMMAND     = "pay";
    public static final String CURRENCY    = "VND";
    public static final String LOCALE      = "vn";
    public static final String ORDER_TYPE  = "other";
}