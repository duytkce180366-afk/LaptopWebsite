package com.mycompany.techstore.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.AuthRepository;
import com.mycompany.techstore.exceptions.AuthException;
import com.mycompany.techstore.resources.DbClass;

public class AuthService extends DbClass {

    private final String emailFormat = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private final AuthRepository authRepo;
    
    public AuthService() {
        this.authRepo = new AuthRepository();
    }
    
    public String convertToMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hashBytes);
    }

    public User GetUserOIDCSignIn(String email) throws AuthException {
        User user;

        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        user = this.authRepo.GetUserOIDCSignIn(email);

        if (user == null) {
            throw new AuthException(-1, "User not found");
        }

        return user;
    }

    public User GetUserSignIn(String email, String password) throws AuthException, NoSuchAlgorithmException {
        User user;

        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }
        
        String pwdHash = this.convertToMD5(password);
        
        user = this.authRepo.GetUserSignIn(email, pwdHash);
        
        if (user == null) {
            throw new AuthException(-1, "User not found");
        }
        
        return user;
    }

    public User CreateUserSignIn(String email, String pasword, String name) throws AuthException, NoSuchAlgorithmException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        if (this.authRepo.IsEmailExists(email)) {
            throw new AuthException(-1, "Email already exists");
        }

        String pwdHash = this.convertToMD5(pasword);

        User created = this.authRepo.CreateUser(email, pwdHash, name);

        if (created == null) {
            throw new AuthException(-1, "Failed to create user");
        }

        return created;
    }

    public User GetOrCreateUserOIDCSignIn(String email, String name) throws AuthException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        User user = this.authRepo.GetUserOIDCSignIn(email);
        if (user != null) {
            return user;
        }

        // create user without password
        User created = this.authRepo.CreateUser(email, null, (name == null) ? "" : name);
        if (created == null) {
            throw new AuthException(-1, "Failed to create OIDC user");
        }

        return created;
    }

    public boolean UpdateUserPassword(String email, String newPassword) throws NoSuchAlgorithmException, AuthException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        String pwdHash = this.convertToMD5(newPassword);
        return this.authRepo.UpdatePassword(email, pwdHash);
    }
    
    
}
