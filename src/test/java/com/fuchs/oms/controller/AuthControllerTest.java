package com.fuchs.oms.controller;

import com.fuchs.oms.config.SecurityConfig;
import com.fuchs.oms.dto.LoginRequest;
import com.fuchs.oms.dto.LoginResponse;
import com.fuchs.oms.exception.GlobalExceptionHandler;
import com.fuchs.oms.security.JwtAuthenticationEntryPoint;
import com.fuchs.oms.security.JwtAuthenticationFilter;
import com.fuchs.oms.security.JwtTokenProvider;
import com.fuchs.oms.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setToken("generated-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        String loginJson = """
            {"username": "vendor-a", "password": "password123"}
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("generated-jwt-token"));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Invalid username or password"));

        String loginJson = """
            {"username": "vendor-a", "password": "wrongpassword"}
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Invalid username or password"))
            .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void login_withMissingUsername_returns400() throws Exception {
        String loginJson = """
            {"password": "password123"}
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withMissingPassword_returns400() throws Exception {
        String loginJson = """
            {"username": "vendor-a"}
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankUsername_returns400() throws Exception {
        String loginJson = """
            {"username": "", "password": "password123"}
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_withBlankPassword_returns400() throws Exception {
        String loginJson = """
            {"username": "vendor-a", "password": ""}
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
            .andExpect(status().isBadRequest());
    }
}
