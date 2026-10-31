
package com.moviematch.moviematch.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class PasswordVerifierTest {
    @Test
    void correctPasswordIsAccepted() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("Movie123!");

        assertTrue(PasswordVerifier.matches("Movie123!", hash));
    }
    @Test
    void wrongPasswordIsRejected() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("Movie123!");
        assertFalse(PasswordVerifier.matches("WrongPassword", hash));
    }
}
