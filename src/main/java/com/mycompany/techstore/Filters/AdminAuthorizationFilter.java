package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Exceptions.BackOfficeAccessException;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.UserRepository;
import com.mycompany.techstore.services.BackOfficeAuthorizationPolicy;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebFilter(urlPatterns = {"/admin", "/admin/*"})
public class AdminAuthorizationFilter implements Filter {
    public static final String CSRF_SESSION_KEY = "adminCsrfToken";

    private final UserRepository users = new UserRepository();

    @Override
    public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) rawRequest;
        HttpServletResponse response = (HttpServletResponse) rawResponse;
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        try {
            String role = users.findRoleName(user.getUser_id());
            requireBackOfficeAccount(user, role);

            String path = request.getRequestURI().substring(request.getContextPath().length());
            requirePermission(role, request.getMethod(), path);
            requireValidCsrf(request, session);

            request.setAttribute("backOfficeRole", role);
            request.setAttribute("isAdmin", "Admin".equals(role));
            chain.doFilter(request, response);
        } catch (BackOfficeAccessException exception) {
            String messageKey = "/home".equals(exception.getRedirectPath()) ? "storeNotice" : "adminError";
            session.setAttribute(messageKey, exception.getMessage());
            response.sendRedirect(request.getContextPath() + exception.getRedirectPath());
        } catch (SQLException exception) {
            throw new ServletException("Unable to verify the account role.", exception);
        }
    }

    private void requireBackOfficeAccount(User user, String role) throws BackOfficeAccessException {
        boolean allowed = BackOfficeAuthorizationPolicy.isBackOfficeRole(role)
                && "Active".equalsIgnoreCase(user.getStatus())
                && user.isIsVerified();
        if (!allowed) {
            throw new BackOfficeAccessException(
                    "An active and verified Admin or Staff account is required.", "/home");
        }
    }

    private void requirePermission(String role, String method, String path)
            throws BackOfficeAccessException {
        if (!BackOfficeAuthorizationPolicy.canAccess(role, method, path)) {
            throw new BackOfficeAccessException(
                    "You do not have permission to perform this operation.", "/admin/dashboard");
        }
    }

    private void requireValidCsrf(HttpServletRequest request, HttpSession session)
            throws BackOfficeAccessException {
        if (session.getAttribute(CSRF_SESSION_KEY) == null) {
            session.setAttribute(CSRF_SESSION_KEY, UUID.randomUUID().toString());
        }
        if ("GET".equalsIgnoreCase(request.getMethod()) || "HEAD".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        String expected = (String) session.getAttribute(CSRF_SESSION_KEY);
        String provided = request.getParameter("csrfToken");
        if (provided == null || !expected.equals(provided)) {
            throw new BackOfficeAccessException(
                    "Your security token is invalid or expired. Please try again.", "/admin/dashboard");
        }
    }
}