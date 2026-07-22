package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Exceptions.BackOfficePurchaseException;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebFilter(
    urlPatterns = {"/cart", "/cart/*", "/checkout", "/place-order", "/apply-voucher", "/vnpay-pay"})
public class PurchaseAuthorizationFilter implements Filter {
  private static final String BACK_OFFICE_PURCHASE_MESSAGE =
      "Admin and Staff accounts can view the store, but cannot use purchasing functions.";
  private final UserRepository users = new UserRepository();

  @Override
  public void doFilter(ServletRequest raw, ServletResponse out, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) raw;
    HttpServletResponse response = (HttpServletResponse) out;
    HttpSession session = request.getSession(false);
    User user = session == null ? null : (User) session.getAttribute("loggedUser");

    try {
      if (user != null) {
        String role = users.findRoleName(user.getUser_id());
        ensurePurchasingAllowed(role);
      }
      chain.doFilter(request, response);
    } catch (BackOfficePurchaseException ex) {
      request.getSession().setAttribute("storeNotice", ex.getMessage());
      response.sendRedirect(request.getContextPath() + "/home");
    } catch (SQLException ex) {
      throw new ServletException("Unable to verify the account role.", ex);
    }
  }

  private void ensurePurchasingAllowed(String role) throws BackOfficePurchaseException {
    if ("Admin".equals(role) || "Staff".equals(role)) {
      throw new BackOfficePurchaseException(BACK_OFFICE_PURCHASE_MESSAGE);
    }
  }
}
