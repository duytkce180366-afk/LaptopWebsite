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

            if ("address_add".equalsIgnoreCase(action)) {
                request.setAttribute("user", logged);
                request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/AddressForm.jsp").forward(request, response);
                return;
            }

            if ("address_edit".equalsIgnoreCase(action)) {
                String idStr = request.getParameter("id");
                int id = -1;
                try {
                    id = Integer.parseInt(idStr);
                } catch (NumberFormatException ignore) {

                }

                java.util.List<com.mycompany.techstore.Models.Objects.Address> addrs = this.profileService.GetAddressesForUser(logged.getUser_id());
                com.mycompany.techstore.Models.Objects.Address found = null;
                for (com.mycompany.techstore.Models.Objects.Address a : addrs) {
                    if (a.getAddress_id() == id) {
                        found = a;
                        break;
                    }
                }

                request.setAttribute("user", logged);
                request.setAttribute("address", found);
                request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/AddressForm.jsp").forward(request, response);
                return;
            }

            // default: show profile page with addresses
            request.setAttribute("user", logged);
            request.setAttribute("addresses", this.profileService.GetAddressesForUser(logged.getUser_id()));
            request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/ProfilePage.jsp").forward(request, response);
        } catch (ServletException | IOException ex) {
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

        if ("address_save".equalsIgnoreCase(action)) {
            String addrId = request.getParameter("address_id");
            int addressId = -1;
            try {
                addressId = Integer.parseInt(addrId);
            } catch (NumberFormatException ignore) {
                // ignored
            }

            String line1 = request.getParameter("line1");
            String line2 = request.getParameter("line2");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String postal = request.getParameter("postal_code");
            String country = request.getParameter("country");
            boolean isDefault = request.getParameter("is_default") != null;

            boolean ok;
            if (addressId > 0) {
                ok = this.profileService.UpdateAddress(addressId, logged.getUser_id(), line1, line2, city, state, postal, country, isDefault);
            } else {
                ok = this.profileService.CreateAddress(logged.getUser_id(), line1, line2, city, state, postal, country, isDefault);
            }

            if (!ok) {
                response.sendError(500, "Failed to save address");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        if ("address_delete".equalsIgnoreCase(action)) {
            String idStr = request.getParameter("id");
            int id = -1;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException ignore) {

            }

            if (id <= 0) {
                response.sendError(400, "Invalid address id");
                return;
            }

            boolean ok = this.profileService.DeleteAddress(id, logged.getUser_id());
            if (!ok) {
                response.sendError(500, "Failed to delete address");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        response.sendError(400, "Invalid action");
    }
}
