package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.Order;
import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Models.Objects.Review;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.OrderDetailRepository;
import com.mycompany.techstore.services.ProductService;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@WebServlet(name = "ReviewController", urlPatterns = {"/review"})
public class ReviewController extends HttpServlet {

    private final ProductService productService = new ProductService();
    private final OrderDetailRepository orderDetailRepository = new OrderDetailRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        int userId = loggedUser.getUser_id();
        int orderId = parseInt(request.getParameter("orderId"));
        int productId = parseInt(request.getParameter("productId"));
        if (orderId <= 0 || productId <= 0) {
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        Order order = orderDetailRepository.getOrderById(orderId, userId);
        if (order == null || !"Delivered".equalsIgnoreCase(order.getOrderStatus())
                || !productService.canReviewOrderProduct(orderId, productId, userId)) {
            response.sendRedirect(request.getContextPath() + "/order-detail?id=" + orderId);
            return;
        }

        Review review = productService.getReviewByOrderAndProduct(orderId, productId, userId);
        Product product = productService.getById(productId);

        request.setAttribute("order", order);
        request.setAttribute("product", product);
        request.setAttribute("review", review);
        request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/review-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        int userId = loggedUser.getUser_id();
        int orderId = parseInt(request.getParameter("orderId"));
        int productId = parseInt(request.getParameter("productId"));
        int rating = parseInt(request.getParameter("rating"));
        String comment = cleanComment(request.getParameter("comment"));

        Order order = orderDetailRepository.getOrderById(orderId, userId);
        Product product = productService.getById(productId);
        Review existingReview = productService.getReviewByOrderAndProduct(orderId, productId, userId);

        if (order == null || product == null || !"Delivered".equalsIgnoreCase(order.getOrderStatus())
                || !productService.canReviewOrderProduct(orderId, productId, userId)) {
            request.setAttribute("error", "You can only review delivered products from your own order.");
            request.setAttribute("order", order);
            request.setAttribute("product", product);
            request.setAttribute("review", existingReview);
            request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/review-form.jsp").forward(request, response);
            return;
        }

        if (rating < 1 || rating > 5 || comment.isBlank()) {
            request.setAttribute("error", "Please choose a rating and write a short comment.");
            request.setAttribute("order", order);
            request.setAttribute("product", product);
            request.setAttribute("review", existingReview);
            request.setAttribute("submittedComment", comment);
            request.setAttribute("submittedRating", rating);
            request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/review-form.jsp").forward(request, response);
            return;
        }

        if (productService.saveReview(userId, orderId, productId, rating, comment)) {
            response.sendRedirect(request.getContextPath() + "/order-detail?id=" + orderId + "#product-" + productId);
            return;
        }

        request.setAttribute("error", "We could not save your review right now.");
        request.setAttribute("order", order);
        request.setAttribute("product", product);
        request.setAttribute("review", existingReview);
        request.setAttribute("submittedComment", comment);
        request.setAttribute("submittedRating", rating);
        request.getRequestDispatcher("/WEB-INF/JSPViews/GuestView/review-form.jsp").forward(request, response);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return -1;
        }
    }

    private String cleanComment(String userInput) {
        if (userInput == null) {
            return "";
        }
        return Jsoup.clean(userInput, Safelist.basic()).trim();
    }
}
