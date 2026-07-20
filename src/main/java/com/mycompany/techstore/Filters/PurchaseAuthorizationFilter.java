package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebFilter(urlPatterns={"/cart","/cart/*","/checkout","/place-order","/apply-voucher","/vnpay-pay"})
public class PurchaseAuthorizationFilter implements Filter {
    private final UserRepository users=new UserRepository();
    @Override
    public void doFilter(ServletRequest raw,ServletResponse out,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest request=(HttpServletRequest)raw;
        HttpServletResponse response=(HttpServletResponse)out;
        HttpSession session=request.getSession(false);
        User user=session==null?null:(User)session.getAttribute("loggedUser");
        if(user!=null)try{String role=users.findRoleName(user.getUser_id());if("Admin".equals(role)||"Staff".equals(role)){response.sendError(403,"Back-office accounts cannot use purchasing functions.");return;}}
        catch(SQLException ex){throw new ServletException("Unable to verify the account role.",ex);}
        chain.doFilter(request,response);
    }
}
