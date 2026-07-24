package com.mycompany.techstore.services;

import com.mycompany.techstore.Exceptions.BackOfficeValidationException;
import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminUserRepository;
import jakarta.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AdminUserService {

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_STAFF = "Staff";
    public static final String ROLE_CUSTOMER = "Customer";

    private final AdminUserRepository repository = new AdminUserRepository();
    private final EmailService emailService = new EmailService();

    public PageResult<AdminUser> findAll(String q, int role, String status, int page)
            throws SQLException {
        return repository.findAll(q, role, status, Math.max(1, page), 12);
    }

    public List<LookupOption> roles() throws SQLException {
        return repository.roles();
    }

    public PageResult<AdminUser> findAllForActor(
            String actorRole, String q, int role, String status, int page) throws SQLException {
        if (ROLE_STAFF.equals(actorRole)) {
            role = repository.findRoleId(ROLE_CUSTOMER);
        }
        return findAll(q, role, status, page);
    }

    public List<LookupOption> rolesForActor(String actorRole) throws SQLException {
        if (!ROLE_STAFF.equals(actorRole)) {
            return roles();
        }
        return roles().stream().filter(role -> ROLE_CUSTOMER.equals(role.getName()) || "User".equals(role.getName())).toList();
    }

    public AdminUser findById(int id) throws SQLException {
        return repository.findById(id);
    }

    public void setStatus(int id, String status, int adminId) throws SQLException {
        if (id == adminId) {
            throw new BackOfficeValidationException("You cannot change your own status.");
        }
        if (!Set.of("Active", "Blocked").contains(status)) {
            throw new BackOfficeValidationException("Invalid user status.");
        }
        if ("Blocked".equals(status) && repository.isLastActiveAdmin(id)) {
            throw new BackOfficeValidationException("The last active administrator cannot be blocked.");
        }
        repository.setStatus(id, status, adminId);
    }

    public void setRole(int id, int roleId, int adminId) throws SQLException {
        if (id == adminId) {
            throw new BackOfficeValidationException("You cannot change your own role.");
        }
        if (!repository.isManagedRole(roleId)) {
            throw new BackOfficeValidationException("Invalid role.");
        }
        AdminUser selected = repository.findById(id);
        if (selected == null) {
            throw new BackOfficeValidationException("User not found.");
        }
        if (ROLE_ADMIN.equals(selected.getRoleName()) && repository.isLastActiveAdmin(id)) {
            for (LookupOption role : repository.roles()) {
                if (role.getId() == roleId && !ROLE_ADMIN.equals(role.getName())) {
                    throw new BackOfficeValidationException(
                            "The last active administrator cannot be demoted.");
                }
            }
        }
        repository.setRole(id, roleId, adminId);
    }

    public void setStatusForActor(int id, String status, int actorId, String actorRole)
            throws SQLException, MessagingException {
        setStatusForActor(id, status, actorId, actorRole, null);
    }

    public void setStatusForActor(int id, String status, int actorId, String actorRole, String reason)
            throws SQLException, MessagingException {
        AdminUser selected = repository.findById(id);
        if (selected == null) {
            throw new BackOfficeValidationException("User not found.");
        }
        if (ROLE_STAFF.equals(actorRole) && !ROLE_CUSTOMER.equals(selected.getRoleName())) {
            throw new BackOfficeValidationException("Staff can only block or unblock customer accounts.");
        }
        setStatus(id, status, actorId);

        if ("Blocked".equals(status)) {
            // Send email when blocked account
            emailService.sendAccountBlockedEmail(selected.getEmail(), selected.getFullName(), reason);
        }
    }

    public void createStaff(String name, String email, String phone, String password, int adminId)
            throws SQLException, NoSuchAlgorithmException, MessagingException {
        validateStaff(name, email, phone);
        if (password == null || password.length() < 8) {
            throw new BackOfficeValidationException("Password must contain at least 8 characters.");
        }
        if (repository.emailExists(email, 0)) {
            throw new BackOfficeValidationException("Email is already in use.");
        }
        repository.createStaff(name.trim(), email.trim(), clean(phone), PasswordUtil.hashPassword(password), adminId);
        emailService.sendStaffCredentialsEmail(email.trim(), name.trim(), password);
    }

    public void updateStaff(int id, String name, String email, String phone, int adminId)
            throws SQLException {
        validateStaff(name, email, phone);
        AdminUser current = repository.findById(id);
        if (current == null || !ROLE_STAFF.equals(current.getRoleName())) {
            throw new BackOfficeValidationException("Staff account not found.");
        }
        if (repository.emailExists(email, id)) {
            throw new BackOfficeValidationException("Email is already in use.");
        }
        repository.updateStaff(id, name.trim(), email.trim(), clean(phone), adminId);
    }

    public void deleteStaff(int id, int adminId) throws SQLException {
        if (id == adminId) {
            throw new BackOfficeValidationException("You cannot delete your own account.");
        }
        repository.deactivateStaff(id, adminId);
    }

    private void validateStaff(String name, String email, String phone) {
        if (name == null || name.trim().length() < 2) {
            throw new BackOfficeValidationException("Staff name is required.");
        }
        if (!isValidEmail(email)) {
            throw new BackOfficeValidationException("A valid email is required.");
        }
        if (phone != null && !phone.isBlank() && !phone.trim().matches("[0-9+ .()-]{8,20}")) {
            throw new BackOfficeValidationException("Invalid phone number.");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    static boolean isValidEmail(String value) {
        if (value == null) {
            return false;
        }
        String email = value.trim();
        if (email.isEmpty() || email.length() > 254) {
            return false;
        }

        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            return false;
        }

        int dot = email.indexOf('.', at + 1);
        if (dot <= at + 1 || dot == email.length() - 1) {
            return false;
        }

        for (int i = 0; i < email.length(); i++) {
            if (Character.isWhitespace(email.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
