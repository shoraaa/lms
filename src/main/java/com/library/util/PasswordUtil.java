package com.library.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {

    // Method to hash a password using SHA-256
    public static String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hash the password
            byte[] hashBytes = digest.digest(password.getBytes());

            // Convert the hashed bytes into a Base64 encoded string
            return Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing the password: " + e.getMessage(), e);
        }
    }

    // Method to check if a password matches a hashed password
    public static boolean verifyPassword(String password, String storedHash) {
        System.out.println("Password: " + hashPassword(password) + ", Stored Hash: " + storedHash);
        // Hash the input password and compare with the stored hash
        return storedHash.equals(hashPassword(password));
    }

}
