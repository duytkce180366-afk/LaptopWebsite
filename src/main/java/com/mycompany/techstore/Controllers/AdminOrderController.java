package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.services.AdminOrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet(urlPatterns={"/admin/orders","/admin/orders/*"})
public class AdminOrderController extends HttpServlet {
    private final AdminOrderService service=new AdminOrderService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        try{
            if("/detail".equals(req.getPathInfo())){
                AdminOrder order=service.findById(number(req,"id",0));if(order==null){res.sendError(404);return;}
                req.setAttribute("order",order);req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/order-detail.jsp").forward(req,res);return;
            }
            String q=text(req,"q"),status=text(req,"status"),payment=text(req,"payment");
            LocalDate from=date(req,"from"),to=date(req,"to");
            req.setAttribute("result",service.findAll(q,status,payment,from,to,number(req,"page",1)));
            req.setAttribute("q",q);req.setAttribute("selectedStatus",status);req.setAttribute("selectedPayment",payment);
            req.setAttribute("from",from);req.setAttribute("to",to);
            req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/orders.jsp").forward(req,res);
        }catch(SQLException ex){throw new ServletException(ex);}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        User admin=(User)req.getSession().getAttribute("loggedUser");int id=number(req,"id",0);
        try{
            service.changeStatus(id,text(req,"status"),text(req,"note"),admin.getUser_id());
            req.getSession().setAttribute("adminMessage","Order status was updated.");
            res.sendRedirect(req.getContextPath()+"/admin/orders/detail?id="+id);
        }catch(IllegalArgumentException ex){
            req.getSession().setAttribute("adminError",ex.getMessage());res.sendRedirect(req.getContextPath()+"/admin/orders/detail?id="+id);
        }catch(SQLException ex){throw new ServletException(ex);}
    }
    private String text(HttpServletRequest r,String n){String v=r.getParameter(n);return v==null?"":v.trim();}
    private int number(HttpServletRequest r,String n,int d){try{return Integer.parseInt(text(r,n));}catch(Exception ex){return d;}}
    private LocalDate date(HttpServletRequest r,String n){try{return LocalDate.parse(text(r,n));}catch(Exception ex){return null;}}
}
