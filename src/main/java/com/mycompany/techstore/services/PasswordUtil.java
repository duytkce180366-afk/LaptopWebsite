package com.mycompany.techstore.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.InvalidKeySpecException;

public final class PasswordUtil {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int DERIVED_KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes

    private PasswordUtil() {}

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, DERIVED_KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);

            return String.format("pbkdf2$%d$%s$%s", PBKDF2_ITERATIONS, saltB64, hashB64);
        } catch (InvalidKeySpecException ex) {
            throw new NoSuchAlgorithmException("Failed to generate password hash", ex);
        }
    }

    public static boolean verifyPassword(String password, String stored) throws NoSuchAlgorithmException {
        if (stored == null) return false;

        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, expectedHash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] computed = skf.generateSecret(spec).getEncoded();

            // constant time comparison
            if (computed.length != expectedHash.length) return false;
            int result = 0;
            for (int i = 0; i < computed.length; i++) {
                result |= computed[i] ^ expectedHash[i];
            }
            return result == 0;
        } catch (InvalidKeySpecException | NumberFormatException ex) {
            NoSuchAlgorithmException nae = new NoSuchAlgorithmException("Failed to verify password");
            nae.initCause(ex);
            throw nae;
        }
    }
}
