package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Models.Objects.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns={"/cart","/cart/*","/checkout","/place-order","/apply-voucher","/vnpay-pay"})
public class PurchaseAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest raw,ServletResponse out,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest request=(HttpServletRequest)raw;
        HttpServletResponse response=(HttpServletResponse)out;
        HttpSession session=request.getSession(false);
        User user=session==null?null:(User)session.getAttribute("loggedUser");
        if(user!=null&&user.getRole_id()==1){
            response.sendError(403,"Administrator accounts cannot use purchasing functions.");return;
        }
        chain.doFilter(request,response);
    }
}
