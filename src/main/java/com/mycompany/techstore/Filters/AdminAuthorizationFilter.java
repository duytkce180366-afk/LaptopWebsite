package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Models.Objects.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns={"/admin","/admin/*"})
public class AdminAuthorizationFilter implements Filter {
    public static final String CSRF_SESSION_KEY="adminCsrfToken";
    @Override public void doFilter(ServletRequest raw,ServletResponse out,FilterChain chain)throws IOException,ServletException{
        HttpServletRequest req=(HttpServletRequest)raw;HttpServletResponse res=(HttpServletResponse)out;
        HttpSession session=req.getSession(false);User user=session==null?null:(User)session.getAttribute("loggedUser");
        if(user==null){res.sendRedirect(req.getContextPath()+"/auth?action=signin");return;}
        if(user.getRole_id()!=1||!"Active".equalsIgnoreCase(user.getStatus())||!user.isIsVerified()){
            res.sendError(403,"Administrator access is required.");return;
        }
        if(session.getAttribute(CSRF_SESSION_KEY)==null)session.setAttribute(CSRF_SESSION_KEY,UUID.randomUUID().toString());
        if(!"GET".equalsIgnoreCase(req.getMethod())&&!"HEAD".equalsIgnoreCase(req.getMethod())){
            String expected=(String)session.getAttribute(CSRF_SESSION_KEY),provided=req.getParameter("csrfToken");
            if(expected==null||provided==null||!expected.equals(provided)){res.sendError(403,"Invalid CSRF token.");return;}
        }
        chain.doFilter(req,res);
    }
}
