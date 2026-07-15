package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.AdminUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns={"/admin/users","/admin/users/*"})
public class AdminUserController extends HttpServlet {
    private final AdminUserService service=new AdminUserService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        try{
            String q=text(req,"q"),status=text(req,"status");int role=number(req,"role",0),page=number(req,"page",1);
            req.setAttribute("result",service.findAll(q,role,status,page));req.setAttribute("roles",service.roles());
            req.setAttribute("q",q);req.setAttribute("selectedRole",role);req.setAttribute("selectedStatus",status);
            req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/users.jsp").forward(req,res);
        }catch(SQLException ex){throw new ServletException(ex);}
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        User admin=(User)req.getSession().getAttribute("loggedUser");int id=number(req,"id",0);
        try{
            if("/role".equals(req.getPathInfo()))service.setRole(id,number(req,"roleId",0),admin.getUser_id());
            else service.setStatus(id,text(req,"status"),admin.getUser_id());
            req.getSession().setAttribute("adminMessage","User account was updated.");
        }catch(IllegalArgumentException ex){req.getSession().setAttribute("adminError",ex.getMessage());}
        catch(SQLException ex){throw new ServletException(ex);}
        res.sendRedirect(req.getContextPath()+"/admin/users");
    }
    private String text(HttpServletRequest r,String n){String v=r.getParameter(n);return v==null?"":v.trim();}
    private int number(HttpServletRequest r,String n,int d){try{return Integer.parseInt(text(r,n));}catch(Exception ex){return d;}}
}
