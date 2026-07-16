package com.mycompany.techstore.Controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import com.mycompany.techstore.Exceptions.ProfileException;
import com.mycompany.techstore.Models.Objects.Address;
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

    // Update new 
    private void UpdateProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ProfileException, IOException {
        String fullName = request.getParameter("full_name");
        String phone = request.getParameter("phone");

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new ProfileException(-1, "User not logged in");
        }

        try {
            User user = this.profileService.UpdateProfile(logged.getEmail(), fullName, phone);
            if (user == null) {
                throw new ProfileException(-1, "Failed to update profile");
            }

            session.setAttribute("loggedUser", user);
            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (ProfileException ex) {
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/profile?action=edit_profile&error=" + errorEx);
        }
    }

    // Address Update
    private void UpdateAddress(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ProfileException, IOException {
        String addressIdStr = request.getParameter("address_id");
        String homeAddress = request.getParameter("home_address");
        String phone = request.getParameter("phone");
        String province = request.getParameter("province");
        String ward = request.getParameter("ward");
        boolean isDefault = request.getParameter("is_default") != null;

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new ProfileException(-1, "User not logged in");
        }

        try {
            int userId = logged.getUser_id();
            int addressId = Integer.parseInt(addressIdStr);
            boolean status = this.profileService.UpdateAddress(userId, addressId, homeAddress, phone, province, ward, isDefault);

            if (!status) {
                throw new ProfileException(-1, "Failed to save address");
            }

            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (NumberFormatException | ProfileException ex) {
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/profile?action=edit_address&error=" + errorEx);
        }
    }

    // Delete address
    private void DeleteAddress(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ProfileException, IOException {
        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new ProfileException(-1, "User not logged in");
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (!this.profileService.DeleteAddress(id, logged.getUser_id())) {
                throw new ProfileException(-1, "Failed to delete address");
            }

            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (NumberFormatException | ProfileException ex) {
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/profile?action=remove_address&error=" + errorEx);
        }
    }

    // Add address
    private void AddAddress(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ProfileException, IOException {
        String homeAddress = request.getParameter("home_address");
        String phone = request.getParameter("phone");
        String province = request.getParameter("province");
        String ward = request.getParameter("ward");
        boolean isDefault = (request.getParameter("is_default") != null);

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new ProfileException(-1, "User not logged in");
        }

        try {
            int userId = logged.getUser_id();

            boolean status = this.profileService.CreateAddress(userId, homeAddress, phone, province, ward, isDefault);

            if (!status) {
                throw new ProfileException(-1, "Failed to save address");
            }

            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (ProfileException ex) {
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/profile?action=add_address&error=" + errorEx);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User logged = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        switch (request.getParameter("action")) {
            // Edit profile form (GET) -> process on POST with same action name
            case "edit_profile" -> {
                request.setAttribute("user", logged);
                request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/EditProfile.jsp").forward(request, response);
            }

            // Add address form
            case "add_address" -> {
                request.setAttribute("user", logged);
                request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/AddressForm.jsp").forward(request, response);
            }

            // Edit address form (prefill)
            case "edit_address" -> {
                try {
                    int id = Integer.parseInt(request.getParameter("id").trim());

                    Address addr = this.profileService.GetAddressesForUser(logged.getUser_id())
                            .stream()
                            .filter(add -> (add.getAddressId() == id && add.getUserId() == logged.getUser_id()))
                            .findFirst()
                            .orElse(null);

                    if (addr == null) {
                        throw new ProfileException(-1, "Address not found");
                    }

                    request.setAttribute("user", logged);
                    request.setAttribute("address", addr);
                    request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/AddressForm.jsp").forward(request, response);
                } catch (NumberFormatException | ProfileException ex) {
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/profile?action=edit_address&error=" + errorEx);
                }
            }

            // View profile page
            case "view" -> {
                request.setAttribute("user", logged);
                request.setAttribute("addresses", this.profileService.GetAddressesForUser(logged.getUser_id()));
                request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/ProfilePage.jsp").forward(request, response);
            }

            // Remove address (support on GET per request; will perform deletion and redirect)
            case "remove_address" -> {
                try {
                    this.DeleteAddress(request, response, session);
                } catch (ProfileException ex) {
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/profile?action=remove_address&error=" + errorEx);
                }
            }

            // Default actions
            case null -> {
                response.sendRedirect(request.getContextPath() + "/profile?action=view");
            }
            default -> {
                response.sendRedirect(request.getContextPath() + "/profile?action=view");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User logged = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin");
            return;
        }

        switch (request.getParameter("action")) {
            // Update profile (POST)
            case "edit_profile" -> {
                try {
                    this.UpdateProfile(request, response, session);
                } catch (ProfileException | IOException ex) {
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/profile?action=edit_profile&error=" + errorEx);
                }
            }

            // Add address
            case "add_address" -> {
                try {
                    this.AddAddress(request, response, session);
                } catch (ProfileException | IOException ex) {
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/profile?action=add_address&error=" + errorEx);
                }
            }

            // Edit address
            case "edit_address" -> {
                try {
                    this.UpdateAddress(request, response, session);
                } catch (ProfileException | IOException ex) {
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/profile?action=edit_address&error=" + errorEx);
                }
            }

            // Delete address (POST)
            case "remove_address" -> {
                try {
                    this.DeleteAddress(request, response, session);
                } catch (ProfileException | IOException ex) {
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/profile?action=remove_address&error=" + errorEx);
                }
            }

            default -> {
                response.sendRedirect(request.getContextPath() + "/profile?action=remove_address&error=Invalid+action");
            }
        }
    }
}
