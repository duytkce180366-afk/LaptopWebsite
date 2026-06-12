package com.mycompany.techstore.Controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.Exceptions.AuthException;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.ProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ProfileController", urlPatterns = {"/profile"})
public class ProfileController extends HttpServlet {

    private transient final ProfileService profileService;

    public ProfileController() {
        this.profileService = new ProfileService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        HttpSession session = request.getSession(false);
        User logged = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        try {
            if ("edit".equalsIgnoreCase(action)) {
                request.setAttribute("user", logged);
                request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/EditProfile.jsp").forward(request, response);
                return;
            }

            // default: show profile page
            request.setAttribute("user", logged);
            request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/ProfilePage.jsp").forward(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            response.sendError(500, ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        HttpSession session = request.getSession(false);
        User logged = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (logged == null) {
            response.sendError(400, "Not signed in");
            return;
        }

        if ("update".equalsIgnoreCase(action)) {
            String fullName = request.getParameter("full_name");
            String phone = request.getParameter("phone");

            try {
                User refreshed = this.profileService.UpdateProfile(logged.getEmail(), fullName, phone);
                if (refreshed == null) {
                    response.sendError(500, "Failed to update profile");
                    return;
                }

                // update session
                session.setAttribute("loggedUser", refreshed);
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            } catch (AuthException ex) {
                Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                response.sendError(500, ex.getMessage());
                return;
            }
        }

        response.sendError(400, "Invalid action");
    }
}
