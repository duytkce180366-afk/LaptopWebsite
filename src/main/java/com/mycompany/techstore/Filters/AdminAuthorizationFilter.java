package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import java.sql.SQLException;

@WebFilter(urlPatterns={"/admin","/admin/*"})
public class AdminAuthorizationFilter implements Filter {
    public static final String CSRF_SESSION_KEY="adminCsrfToken";
    private final UserRepository users=new UserRepository();
    @Override public void doFilter(ServletRequest raw,ServletResponse out,FilterChain chain)throws IOException,ServletException{
        HttpServletRequest req=(HttpServletRequest)raw;HttpServletResponse res=(HttpServletResponse)out;
        HttpSession session=req.getSession(false);User user=session==null?null:(User)session.getAttribute("loggedUser");
        if(user==null){res.sendRedirect(req.getContextPath()+"/auth?action=signin");return;}
        try{
            String role=users.findRoleName(user.getUser_id());
            if((!"Admin".equals(role)&&!"Staff".equals(role))||!"Active".equalsIgnoreCase(user.getStatus())||!user.isIsVerified()){res.sendError(403,"Back-office access is required.");return;}
            if(req.getRequestURI().startsWith(req.getContextPath()+"/admin/users")&&!"Admin".equals(role)){res.sendError(403,"Administrator access is required for user management.");return;}
        }catch(SQLException ex){throw new ServletException("Unable to verify the account role.",ex);}
        if(session.getAttribute(CSRF_SESSION_KEY)==null)session.setAttribute(CSRF_SESSION_KEY,UUID.randomUUID().toString());
        if(!"GET".equalsIgnoreCase(req.getMethod())&&!"HEAD".equalsIgnoreCase(req.getMethod())){
            String expected=(String)session.getAttribute(CSRF_SESSION_KEY),provided=req.getParameter("csrfToken");
            if(expected==null||provided==null||!expected.equals(provided)){res.sendError(403,"Invalid CSRF token.");return;}
        }
        chain.doFilter(req,res);
    }
}
