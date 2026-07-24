package com.mycompany.techstore.services;

import com.mycompany.techstore.Exceptions.AuthException;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.Repositories.AuthRepository;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AuthService {

    // Allow case-insensitive email local-part/domain validation
    private final String emailFormat = "(?i)^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private final String nameFormat = "^[\\p{L}\\s\\-\\u0027. ]+$";

    private final AuthRepository authRepo;

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int DERIVED_KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    public AuthService() {
        this.authRepo = new AuthRepository();
    }

    /**
     * Local Password Hashing functions
     */
    // Hash password using PBKDF2 and store as: pbkdf2$iterations$base64(salt)$base64(hash)
    private String HashPassword(String password) throws NoSuchAlgorithmException {
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);

            PBEKeySpec spec
                    = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, DERIVED_KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);

            return String.format("pbkdf2$%d$%s$%s", PBKDF2_ITERATIONS, saltB64, hashB64);
        } catch (InvalidKeySpecException ex) {
            throw new NoSuchAlgorithmException("Failed to generate password hash", ex);
        }
    }

    // Verify Password
    private boolean VerifyPassword(String password, String stored) throws NoSuchAlgorithmException {
        if (stored == null) {
            return false;
        }

        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec
                    = new PBEKeySpec(password.toCharArray(), salt, iterations, expectedHash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] computed = skf.generateSecret(spec).getEncoded();

            // constant time comparison
            if (computed.length != expectedHash.length) {
                return false;
            }
            int result = 0;
            for (int i = 0; i < computed.length; i++) {
                result |= computed[i] ^ expectedHash[i];
            }
            return result == 0;
        } catch (InvalidKeySpecException | NumberFormatException ex) {
            throw new NoSuchAlgorithmException("Failed to verify password", ex);
        }
    }

    /*
   * User Sign-in/Sign-up methods
     */
    // Sign in with email and password
    public User GetUserSignIn(String email, String password)
            throws AuthException, NoSuchAlgorithmException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        User user = this.authRepo.GetUserOIDCSignIn(email);

        if (user == null) {
            throw new AuthException(-1, "Invalid credential");
        }
        if (!"Active".equalsIgnoreCase(user.getStatus())) {
            throw new AuthException(-1, "This account is blocked or inactive");
        }

        String stored = user.getPassword();
        if (stored == null) {
            throw new AuthException(-1, "Invalid credential");
        }

        boolean verified = false;

        if (stored.startsWith("pbkdf2$")) {
            // modern format
            verified = VerifyPassword(password, stored);
        } else {
            // legacy MD5 format: accept for compatibility, then upgrade to PBKDF2
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] md5 = md.digest(password.getBytes(StandardCharsets.UTF_8));
                String hex = java.util.HexFormat.of().formatHex(md5);
                if (hex.equalsIgnoreCase(stored)) {
                    verified = true;
                    // upgrade stored password to PBKDF2
                    String newHash = this.HashPassword(password);
                    this.authRepo.UpdatePassword(email, newHash);
                }
            } catch (java.security.NoSuchAlgorithmException ex) {
                // fallback - should not happen on modern JDKs
                throw ex;
            }
        }

        if (!verified) {
            throw new AuthException(-1, "Invalid credentials");
        }

        return user;
    }

    // Sign-up with email and password
    public User CreateUserSignIn(String email, String password, String name)
            throws AuthException, NoSuchAlgorithmException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        if (!name.matches((this.nameFormat))) {
            throw new AuthException(-1, "Name is not in correct format");
        }

        if (this.authRepo.IsEmailExists(email)) {
            throw new AuthException(-1, "Email already exists");
        }

        String pwdHash = null;
        if (password != null) {
            pwdHash = this.HashPassword(password);
        }

        User created = this.authRepo.CreateUser(email, pwdHash, name);

        if (created == null) {
            throw new AuthException(-1, "Failed to create user");
        }

        return created;
    }

    /*
   * User Sign-in/Sign-up with OIDC methods
     */
    public User GetOrCreateUserOIDCSignIn(String email, String name) throws AuthException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        if (!name.matches((this.nameFormat))) {
            throw new AuthException(-1, "Name is not in correct format");
        }

        User user = this.authRepo.GetUserOIDCSignIn(email);
        if (user != null) {
            if (!"Active".equalsIgnoreCase(user.getStatus())) {
                throw new AuthException(-1, "This account is blocked or inactive");
            }
            return user;
        }

        // create user without password
        User created = this.authRepo.CreateUser(email, null, (name == null) ? "" : name);
        if (created == null) {
            throw new AuthException(-1, "Failed to create OIDC user");
        }

        return created;
    }

    public boolean VerifyEmail(String email) throws AuthException {
        return this.authRepo.VerifiedUser(email);
    }

    // Get user by email (refresh user state)
    public User GetUserByEmail(String email) {
        return this.authRepo.GetUserOIDCSignIn(email);
    }

    // Reset password
    public boolean UpdateUserPassword(String email, String newPassword)
            throws NoSuchAlgorithmException, AuthException {
        if (!email.matches(this.emailFormat)) {
            throw new AuthException(-1, "Email is not in correct format");
        }

        String pwdHash = this.HashPassword(newPassword);
        return this.authRepo.UpdatePassword(email, pwdHash);
    }
}
