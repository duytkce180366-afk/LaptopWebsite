    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
     */
    package com.mycompany.techstore.Controllers;

    import com.mycompany.techstore.Models.Objects.Order;
    import com.mycompany.techstore.Models.Objects.OrderDetail;
import com.mycompany.techstore.Models.Objects.User;
    import com.mycompany.techstore.Repositories.OrderDetailRepository;
    import com.mycompany.techstore.Repositories.OrderRepository;
    import com.nimbusds.jose.shaded.gson.Gson;
    import java.io.IOException;
    import java.io.PrintWriter;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.annotation.WebServlet;
    import jakarta.servlet.http.HttpServlet;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import jakarta.servlet.http.HttpSession;
    import java.util.List;
    import java.util.Map;

    /**
     *
     * @author Nguyen Lam Khang
     */
    @WebServlet(name = "OrderDetailController", urlPatterns = {"/order-detail"})
    public class OrderDetailController extends HttpServlet {

        /**
         * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
         * methods.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet OrderDetailController</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet OrderDetailController at " + request.getContextPath() + "</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        }

        // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
        /**
         * Handles the HTTP <code>GET</code> method.
         *
         * @param request servlet request
         * @param response servlet response
         * @throws ServletException if a servlet-specific error occurs
         * @throws IOException if an I/O error occurs
         */
        @Override
protected void doGet(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    // Lấy từ session
    User loggedUser = (User) request.getSession().getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=signin");
        return;
    }
    int userId = loggedUser.getUser_id(); // thay vì hardcode = 1

    String idParam = request.getParameter("id");
    if (idParam == null) {
        response.sendRedirect("order-history");
        return;
    }

    int orderId = Integer.parseInt(idParam);

    OrderDetailRepository repo = new OrderDetailRepository();
    Order order = repo.getOrderById(orderId, userId);
                               List<OrderDetail> details = repo.getDetailsByOrderId(orderId);

    if (order == null) {
        response.sendRedirect("order-history");
        return;
    }

    request.setAttribute("order", order);
    request.setAttribute("details", details);

request.getRequestDispatcher(
    "/WEB-INF/JSPViews/GuestView/order-detail.jsp")
    .forward(request, response);
}
    }