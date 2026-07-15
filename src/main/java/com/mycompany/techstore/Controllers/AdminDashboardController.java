package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.services.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns={"/admin","/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {
    private final DashboardService service=new DashboardService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        try{req.setAttribute("stats",service.load());req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/dashboard.jsp").forward(req,res);}
        catch(SQLException ex){throw new ServletException(ex);}
    }
}
