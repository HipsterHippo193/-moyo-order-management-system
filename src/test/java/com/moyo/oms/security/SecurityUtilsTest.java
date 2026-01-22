package com.moyo.oms.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentVendorId_withAuthenticatedUser_returnsVendorId() {
        VendorUserDetails userDetails = new VendorUserDetails(42L, "vendor-test");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals(42L, SecurityUtils.getCurrentVendorId());
    }

    @Test
    void getCurrentVendorId_withNoAuthentication_throwsException() {
        assertThrows(IllegalStateException.class, SecurityUtils::getCurrentVendorId);
    }

    @Test
    void getCurrentVendorId_withWrongPrincipalType_throwsException() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "string-principal", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(IllegalStateException.class, SecurityUtils::getCurrentVendorId);
    }

    @Test
    void getCurrentUsername_withAuthenticatedUser_returnsUsername() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("vendor-a", SecurityUtils.getCurrentUsername());
    }

    @Test
    void getCurrentUsername_withNoAuthentication_throwsException() {
        assertThrows(IllegalStateException.class, SecurityUtils::getCurrentUsername);
    }
}
