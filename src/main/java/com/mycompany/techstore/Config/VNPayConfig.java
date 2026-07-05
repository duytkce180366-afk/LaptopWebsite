package com.mycompany.techstore.Config;

public class VNPayConfig {
    public static final String TMN_CODE    = "O3OS6OKO";
    public static final String HASH_SECRET = "PZOXVK1ISZDLJXPC2DFVW386VWCA3OEC";

    public static final String PAY_URL     = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String RETURN_URL  = "http://localhost:8080/vnpay-return";

    public static final String VERSION     = "2.1.0";
    public static final String COMMAND     = "pay";
    public static final String CURRENCY    = "VND";
    public static final String LOCALE      = "vn";
    public static final String ORDER_TYPE  = "other";
}