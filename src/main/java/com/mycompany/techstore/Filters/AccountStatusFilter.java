package com.mycompany.techstore.Filters;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.UserRepository;
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
import java.util.Set;

@WebFilter("/*")
public class AccountStatusFilter implements Filter {

    private static final Set<String> STATIC_PREFIXES = Set.of(
            "/css/", "/js/", "/lib/", "/images/", "/img/", "/fonts/", "/favicon"
    );
    private final UserRepository userRepository = new UserRepository();

    @Override
    public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) rawRequest;
        HttpServletResponse response = (HttpServletResponse) rawResponse;

        if (isStaticRequest(request.getRequestURI(), request.getContextPath())) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        User sessionUser = session == null ? null : (User) session.getAttribute("loggedUser");
        if (sessionUser == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            User currentUser = userRepository.findById(sessionUser.getUser_id());
            if (currentUser == null || !"Active".equalsIgnoreCase(currentUser.getStatus())) {
                String reason = currentUser != null && "Blocked".equalsIgnoreCase(currentUser.getStatus())
                        ? "blocked" : "disabled";
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/auth?action=signin&reason=" + reason);
                return;
            }

            // Refresh role, verification and profile data so admin changes take effect immediately.
            session.setAttribute("loggedUser", currentUser);
            chain.doFilter(request, response);
        } catch (SQLException exception) {
            throw new ServletException("Unable to verify the current account status.", exception);
        }
    }

    static boolean isStaticRequest(String requestUri, String contextPath) {
        String path = requestUri.substring(Math.min(contextPath.length(), requestUri.length()));
        return STATIC_PREFIXES.stream().anyMatch(path::startsWith)
                || path.endsWith(".ico") || path.endsWith(".png") || path.endsWith(".jpg")
                || path.endsWith(".jpeg") || path.endsWith(".gif") || path.endsWith(".svg")
                || path.endsWith(".webp") || path.endsWith(".woff") || path.endsWith(".woff2");
    }
}
