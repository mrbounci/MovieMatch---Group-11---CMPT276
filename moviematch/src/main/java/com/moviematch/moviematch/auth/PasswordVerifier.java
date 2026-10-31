package com.moviematch.moviematch.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordVerifier {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static boolean matches(String enteredPassword, String storedHash) {
        if(enteredPassword == null || storedHash == null || storedHash.isEmpty() || enteredPassword.isEmpty()) {
            return false;
        }
        return encoder.matches(enteredPassword, storedHash);
    }
}
