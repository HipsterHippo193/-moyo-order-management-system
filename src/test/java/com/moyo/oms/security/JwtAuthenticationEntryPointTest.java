package com.moyo.oms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.oms.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint entryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        entryPoint = new JwtAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    void commence_setsStatus401() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals(401, response.getStatus());
    }

    @Test
    void commence_setsContentTypeJson() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }

    @Test
    void commence_returnsStructuredJsonError() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        String content = response.getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(content, ErrorResponse.class);

        assertNotNull(errorResponse.getError());
        assertEquals(401, errorResponse.getStatus());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void commence_errorMessageIndicatesAuthRequired() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        String content = response.getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(content, ErrorResponse.class);

        assertTrue(errorResponse.getError().contains("Authentication required"));
    }
}
