
package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.Order;
import com.mycompany.techstore.Models.Objects.OrderDetail;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.OrderDetailRepository;
import com.mycompany.techstore.services.ProductService;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "OrderDetailController", urlPatterns = {"/order-detail"})
public class OrderDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // session
        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }
        int userId = loggedUser.getUser_id();

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("order-history");
            return;
        }

        int orderId = Integer.parseInt(idParam);

        OrderDetailRepository repo = new OrderDetailRepository();
        ProductService productService = new ProductService();
        Order order = repo.getOrderById(orderId, userId);
        List<OrderDetail> details = repo.getDetailsByOrderId(orderId);

        if (order == null) {
            response.sendRedirect("/WEB-INF/JSPViews/GuestView/order-history.jsp");
            return;
        }

        request.setAttribute("order", order);
        request.setAttribute("details", details);
        request.setAttribute("reviewedProductIds", productService.getReviewedProductIdsByOrder(orderId, userId));

        request.getRequestDispatcher(
                "/WEB-INF/JSPViews/GuestView/order-detail.jsp")
                .forward(request, response);
    }
}
