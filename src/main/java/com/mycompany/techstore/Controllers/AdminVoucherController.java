/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Models.Objects.Voucher;
import com.mycompany.techstore.services.VoucherService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author DuyTran
 */
@WebServlet("/admin/voucher")
public class AdminVoucherController extends HttpServlet {

    private VoucherService service
            = new VoucherService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {

            case "create":

                request.getRequestDispatcher(
                        "/WEB-INF/JSPViews/AdminView/CreateVoucher.jsp")
                        .forward(request, response);
                break;

            case "edit":

                int id = Integer.parseInt(
                        request.getParameter("id"));

                Voucher voucher
                        = service.getVoucherById(id);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                request.setAttribute(
                        "expiredDate",
                        sdf.format(voucher.getExpiredDate()));

                request.setAttribute(
                        "voucher",
                        voucher);

                request.getRequestDispatcher(
                        "/WEB-INF/JSPViews/AdminView/EditVoucher.jsp")
                        .forward(request, response);
                break;

            case "list":

            default:

                String keyword
                        = request.getParameter("keyword");

                String status
                        = request.getParameter("status");

                String discount
                        = request.getParameter("discountPercent");

                String expired
                        = request.getParameter("expiredDate");

                if (keyword == null) {
                    keyword = "";
                }
                if (status == null) {
                    status = "";
                }
                if (discount == null) {
                    discount = "";
                }
                if (expired == null) {
                    expired = "";
                }
                if (!discount.isBlank()) {

                    double discountValue = Double.parseDouble(discount);

                    if (discountValue <= 0 || discountValue > 100) {

                        request.setAttribute(
                                "error",
                                "Discount must be between 1 and 100.");

                        request.setAttribute("voucherList",
                                service.getAllVoucher());

                        request.getRequestDispatcher(
                                "/WEB-INF/JSPViews/AdminView/VoucherManagement.jsp")
                                .forward(request, response);

                        return;
                    }
                }
                List<Voucher> list;

                if (keyword.isEmpty()
                        && status.isEmpty()
                        && discount.isEmpty()
                        && expired.isEmpty()) {

                    list = service.getAllVoucher();

                } else {

                    list = service.filterVoucher(
                            keyword,
                            status,
                            discount,
                            expired);
                }

                request.setAttribute("voucherList", list);

                request.getRequestDispatcher(
                        "/WEB-INF/JSPViews/AdminView/VoucherManagement.jsp")
                        .forward(request, response);

                break;
            case "delete":

                int deleteId
                        = Integer.parseInt(request.getParameter("id"));

                service.deleteVoucher(deleteId);

                response.sendRedirect(
                        request.getContextPath()
                        + "/admin/voucher");

                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "create";
        }

        switch (action) {

            case "create":

                createVoucher(request, response);
                break;

            case "update":

                updateVoucher(request, response);
                break;

            default:

                response.sendRedirect(
                        request.getContextPath() + "/admin/voucher");
        }
    }

    private void createVoucher(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Voucher voucher = new Voucher();

        voucher.setCode(request.getParameter("code"));
        double discount = Double.parseDouble(
                request.getParameter("discountPercent"));

        if (discount <= 0 || discount > 100) {

            request.setAttribute(
                    "error",
                    "Discount percent must be between 1 and 100.");

            request.getRequestDispatcher(
                    "/WEB-INF/JSPViews/AdminView/CreateVoucher.jsp")
                    .forward(request, response);

            return;
        }

        voucher.setDiscountPercent(discount);

        voucher.setQuantity(
                Integer.parseInt(request.getParameter("quantity")));
        voucher.setDiscountPercent(
                Double.parseDouble(request.getParameter("discountPercent")));
        voucher.setQuantity(
                Integer.parseInt(request.getParameter("quantity")));

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date expiredDate = sdf.parse(request.getParameter("expiredDate"));

            if (!expiredDate.after(new Date())) {

                request.setAttribute("error",
                        "Expired date must be greater than today.");

                request.getRequestDispatcher(
                        "/WEB-INF/JSPViews/AdminView/CreateVoucher.jsp")
                        .forward(request, response);

                return;
            }

            voucher.setExpiredDate(expiredDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        voucher.setStatus(request.getParameter("status"));

        service.createVoucher(voucher);

        response.sendRedirect(
                request.getContextPath() + "/admin/voucher");
    }

    private void updateVoucher(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Voucher voucher = new Voucher();

        voucher.setVoucherId(
                Integer.parseInt(request.getParameter("voucherId")));
        double discount = Double.parseDouble(
                request.getParameter("discountPercent"));

        if (discount <= 0 || discount > 100) {

            request.setAttribute(
                    "error",
                    "Discount percent must be between 1 and 100.");

            request.getRequestDispatcher(
                    "/WEB-INF/JSPViews/AdminView/CreateVoucher.jsp")
                    .forward(request, response);

            return;
        }

        voucher.setDiscountPercent(discount);

        voucher.setQuantity(
                Integer.parseInt(request.getParameter("quantity")));
        voucher.setCode(request.getParameter("code"));

        voucher.setDiscountPercent(
                Double.parseDouble(request.getParameter("discountPercent")));

        voucher.setQuantity(
                Integer.parseInt(request.getParameter("quantity")));

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date expiredDate
                    = sdf.parse(request.getParameter("expiredDate"));

            if (!expiredDate.after(new Date())) {

                request.setAttribute("error",
                        "Expired date must be greater than today.");

                request.setAttribute("voucher", voucher);

                request.getRequestDispatcher(
                        "/WEB-INF/JSPViews/AdminView/EditVoucher.jsp")
                        .forward(request, response);

                return;
            }

            voucher.setExpiredDate(expiredDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        voucher.setStatus(request.getParameter("status"));

        service.updateVoucher(voucher);

        response.sendRedirect(
                request.getContextPath() + "/admin/voucher");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
