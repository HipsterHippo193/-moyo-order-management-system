package com.fuchs.oms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void loginResponse_holdsToken() {
        LoginResponse response = new LoginResponse();
        response.setToken("test-jwt-token");

        assertEquals("test-jwt-token", response.getToken());
    }

    @Test
    void loginResponse_nullToken() {
        LoginResponse response = new LoginResponse();
        assertNull(response.getToken());
    }
}
