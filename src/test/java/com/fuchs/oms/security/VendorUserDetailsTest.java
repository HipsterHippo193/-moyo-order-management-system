package com.fuchs.oms.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class VendorUserDetailsTest {

    @Test
    void constructor_setsVendorIdAndUsername() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");

        assertEquals(1L, userDetails.getVendorId());
        assertEquals("vendor-a", userDetails.getUsername());
    }

    @Test
    void getAuthorities_returnsRoleVendor() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");

        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VENDOR")));
    }

    @Test
    void getPassword_returnsNull() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");
        assertNull(userDetails.getPassword());
    }

    @Test
    void isAccountNonExpired_returnsTrue() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_returnsTrue() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_returnsTrue() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_returnsTrue() {
        VendorUserDetails userDetails = new VendorUserDetails(1L, "vendor-a");
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void getVendorId_returnsVendorId() {
        VendorUserDetails userDetails = new VendorUserDetails(42L, "vendor-test");
        assertEquals(42L, userDetails.getVendorId());
    }
}
