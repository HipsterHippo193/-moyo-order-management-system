package com.moyo.oms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyo.oms.config.SecurityConfig;
import com.moyo.oms.dto.OrderRequest;
import com.moyo.oms.dto.OrderResponse;
import com.moyo.oms.exception.GlobalExceptionHandler;
import com.moyo.oms.exception.ProductNotFoundException;
import com.moyo.oms.security.JwtAuthenticationEntryPoint;
import com.moyo.oms.security.JwtAuthenticationFilter;
import com.moyo.oms.security.JwtTokenProvider;
import com.moyo.oms.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    void createOrder_withValidData_returns201AndOrderResponse() throws Exception {
        // Given
        OrderRequest request = new OrderRequest(1L, 10);
        OrderResponse response = new OrderResponse(1L, 1L, "Widget", 10, 2L, "Vendor Beta",
            new java.math.BigDecimal("45.00"), new java.math.BigDecimal("450.00"), "ALLOCATED", "2026-01-20T14:30:00");

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").value(1))
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.productName").value("Widget"))
            .andExpect(jsonPath("$.quantity").value(10))
            .andExpect(jsonPath("$.allocatedVendorId").value(2))
            .andExpect(jsonPath("$.allocatedVendorName").value("Vendor Beta"))
            .andExpect(jsonPath("$.price").value(45.00))
            .andExpect(jsonPath("$.totalPrice").value(450.00))
            .andExpect(jsonPath("$.status").value("ALLOCATED"))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser
    void createOrder_withMissingProductId_returns400() throws Exception {
        // Given
        String requestJson = """
            {"quantity": 10}
            """;

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createOrder_withMissingQuantity_returns400() throws Exception {
        // Given
        String requestJson = """
            {"productId": 1}
            """;

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createOrder_withZeroQuantity_returns400() throws Exception {
        // Given
        String requestJson = """
            {"productId": 1, "quantity": 0}
            """;

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createOrder_withNegativeQuantity_returns400() throws Exception {
        // Given
        String requestJson = """
            {"productId": 1, "quantity": -5}
            """;

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createOrder_withNonExistentProduct_returns404() throws Exception {
        // Given
        OrderRequest request = new OrderRequest(999L, 10);

        when(orderService.createOrder(any(OrderRequest.class)))
            .thenThrow(new ProductNotFoundException("Product not found: productId=999"));

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createOrder_withValidDataButNoAllocation_returns201WithPendingStatus() throws Exception {
        // Given
        OrderRequest request = new OrderRequest(1L, 1000);
        OrderResponse response = new OrderResponse(1L, 1L, "Widget", 1000, null, null,
            null, null, "PENDING", "2026-01-20T14:30:00");

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").value(1))
            .andExpect(jsonPath("$.allocatedVendorId").doesNotExist())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createOrder_withoutAuthentication_returns401() throws Exception {
        // Given
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        // When/Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isUnauthorized());
    }
}
