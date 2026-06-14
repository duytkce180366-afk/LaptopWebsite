package com.mycompany.techstore.Controllers;

import java.io.IOException;

import com.mycompany.techstore.Exceptions.AuthException;
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
    private void UpdateProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws AuthException, IOException {
        String fullName = request.getParameter("full_name");
        String phone = request.getParameter("phone");

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new AuthException(-1, "User not logged in");
        }

        User refreshed = this.profileService.UpdateProfile(logged.getEmail(), fullName, phone);
        if (refreshed == null) {
            throw new AuthException(-1, "Failed to update profile");
        }

        session.setAttribute("loggedUser", refreshed);
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    // Address Update
    private void UpdateAddress(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws AuthException, IOException {
        String line1 = request.getParameter("line1");
        String line2 = request.getParameter("line2");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String postal = request.getParameter("postal_code");
        String country = request.getParameter("country");
        String addStr = request.getParameter("address_id");
        boolean isDefault = request.getParameter("is_default") != null;

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new AuthException(-1, "User not logged in");
        }

        try {
            int addressId = Integer.parseInt(addStr.strip());
            boolean status = this.profileService.UpdateAddress(addressId, logged.getUser_id(), line1, line2, city, state, postal, country, isDefault);

            if (!status) {
                throw new AuthException(-1, "Failed to save address");
            }
        } catch (NumberFormatException ex) {
            throw new AuthException(-1, "Invalid number ID");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }

    // Delete address
    private void DeleteAddress(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws AuthException, IOException {
        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new AuthException(-1, "User not logged in");
        }

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
    }

    // Add address
    private void AddAddress(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws AuthException, IOException {
        String line1 = request.getParameter("line1");
        String line2 = request.getParameter("line2");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String postal = request.getParameter("postal_code");
        String country = request.getParameter("country");
        boolean isDefault = request.getParameter("is_default") != null;

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new AuthException(-1, "User not logged in");
        }

        boolean status = this.profileService.CreateAddress(logged.getUser_id(), line1, line2, city, state, postal, country, isDefault);
        if (!status) {
            throw new AuthException(-1, "Failed to save address");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
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
                            .filter(add -> (add.getAddress_id() == id && add.getUser_id() == logged.getUser_id()))
                            .findFirst()
                            .orElse(null);

                    if (addr == null) {
                        response.sendError(404, "Address not found");
                        return;
                    }

                    request.setAttribute("user", logged);
                    request.setAttribute("address", addr);
                    request.getRequestDispatcher("/WEB-INF/JSPViews/ProfileView/AddressForm.jsp").forward(request, response);
                } catch (NumberFormatException ignore) {
                    response.sendError(400, "Invalid address id");
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
                } catch (AuthException ex) {
                    response.sendError(400, ex.getLocalizedMessage());
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
            response.sendError(400, "Not signed in");
            return;
        }
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            // Update profile (POST)
            case "edit_profile" -> {
                try {
                    this.UpdateProfile(request, response, session);
                } catch (AuthException | IOException ex) {
                    response.sendError(400, ex.getLocalizedMessage());
                }
            }

            // Add address
            case "add_address" -> {
                try {
                    this.AddAddress(request, response, session);
                } catch (AuthException | IOException ex) {
                    response.sendError(400, ex.getLocalizedMessage());
                }
            }

            // Edit address
            case "edit_address" -> {
                try {
                    this.UpdateAddress(request, response, session);
                } catch (AuthException | IOException ex) {
                    response.sendError(400, ex.getLocalizedMessage());
                }
            }

            // Delete address (POST)
            case "remove_address" -> {
                try {
                    this.DeleteAddress(request, response, session);
                } catch (AuthException | IOException ex) {
                    response.sendError(400, ex.getLocalizedMessage());
                }
            }

            default -> {
                response.sendError(400, "Invalid action");
            }
        }
    }
}
