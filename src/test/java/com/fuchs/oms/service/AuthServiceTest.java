package com.fuchs.oms.service;

import com.fuchs.oms.dto.LoginRequest;
import com.fuchs.oms.dto.LoginResponse;
import com.fuchs.oms.model.Vendor;
import com.fuchs.oms.repository.VendorRepository;
import com.fuchs.oms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private Vendor testVendor;

    @BeforeEach
    void setUp() {
        testVendor = new Vendor();
        testVendor.setId(1L);
        testVendor.setUsername("vendor-a");
        testVendor.setPassword("$2a$10$encodedPassword");
        testVendor.setName("Vendor Alpha");
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("vendor-a");
        request.setPassword("password123");

        when(vendorRepository.findByUsername("vendor-a")).thenReturn(Optional.of(testVendor));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "vendor-a")).thenReturn("generated-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("generated-jwt-token", response.getToken());
    }

    @Test
    void login_withInvalidUsername_throwsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(vendorRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_withInvalidPassword_throwsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setUsername("vendor-a");
        request.setPassword("wrongpassword");

        when(vendorRepository.findByUsername("vendor-a")).thenReturn(Optional.of(testVendor));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
