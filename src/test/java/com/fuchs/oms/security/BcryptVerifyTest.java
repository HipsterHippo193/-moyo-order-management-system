package com.fuchs.oms.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class BcryptVerifyTest {

    @Test
    void verifyStoredHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String storedHash = "$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq";
        String password = "password123";

        boolean matches = encoder.matches(password, storedHash);

        assertTrue(matches, "BCrypt hash from data.sql should match password123");
    }
}
