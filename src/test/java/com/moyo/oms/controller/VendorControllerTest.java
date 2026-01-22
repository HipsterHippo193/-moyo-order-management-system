package com.moyo.oms.controller;

import com.moyo.oms.dto.PriceUpdateRequest;
import com.moyo.oms.dto.PriceUpdateResponse;
import com.moyo.oms.dto.StockUpdateRequest;
import com.moyo.oms.dto.StockUpdateResponse;
import com.moyo.oms.dto.VendorProductResponse;
import com.moyo.oms.exception.ResourceNotFoundException;
import com.moyo.oms.exception.VendorAccessDeniedException;
import com.moyo.oms.security.VendorUserDetails;
import com.moyo.oms.service.VendorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorControllerTest {

    @Mock
    private VendorService vendorService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VendorController vendorController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(Long vendorId) {
        VendorUserDetails userDetails = new VendorUserDetails(vendorId, "vendor-" + vendorId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void getProducts_withValidVendorId_returnsProducts() {
        setupSecurityContext(1L);

        VendorProductResponse product = new VendorProductResponse(1L, "widget-001", "Widget", new BigDecimal("50.00"), 100);
        when(vendorService.getVendorProducts(1L)).thenReturn(Arrays.asList(product));

        ResponseEntity<List<VendorProductResponse>> response = vendorController.getProducts(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getProductId()).isEqualTo(1L);
        verify(vendorService).getVendorProducts(1L);
    }

    @Test
    void getProducts_forOtherVendor_throwsVendorAccessDeniedException() {
        setupSecurityContext(1L);

        assertThatThrownBy(() -> vendorController.getProducts(2L))
            .isInstanceOf(VendorAccessDeniedException.class)
            .hasMessage("Access denied: You can only access your own data");

        verify(vendorService, never()).getVendorProducts(anyLong());
    }

    @Test
    void updatePrice_withValidRequest_returnsUpdatedPrice() {
        setupSecurityContext(1L);

        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("55.00"));
        PriceUpdateResponse expectedResponse = new PriceUpdateResponse(
            1L, "widget-001", "Widget", new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );
        when(vendorService.updatePrice(1L, 1L, request)).thenReturn(expectedResponse);

        ResponseEntity<PriceUpdateResponse> response = vendorController.updatePrice(1L, 1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getNewPrice()).isEqualTo(new BigDecimal("55.00"));
        verify(vendorService).updatePrice(1L, 1L, request);
    }

    @Test
    void updatePrice_forOtherVendor_throwsVendorAccessDeniedException() {
        setupSecurityContext(1L);

        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("55.00"));

        assertThatThrownBy(() -> vendorController.updatePrice(2L, 1L, request))
            .isInstanceOf(VendorAccessDeniedException.class)
            .hasMessage("Access denied: You can only update your own prices");

        verify(vendorService, never()).updatePrice(anyLong(), anyLong(), any());
    }

    @Test
    void updatePrice_forNonExistentProduct_propagatesException() {
        setupSecurityContext(1L);

        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("55.00"));
        when(vendorService.updatePrice(1L, 999L, request))
            .thenThrow(new ResourceNotFoundException("Product not found for vendor: vendorId=1, productId=999"));

        assertThatThrownBy(() -> vendorController.updatePrice(1L, 999L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found");
    }

    @Test
    void updateStock_withValidRequest_returnsUpdatedStock() {
        setupSecurityContext(1L);

        StockUpdateRequest request = new StockUpdateRequest(150);
        StockUpdateResponse expectedResponse = new StockUpdateResponse(
            1L, "widget-001", "Widget", 150, "2026-01-19T14:30:00.000Z"
        );
        when(vendorService.updateStock(1L, 1L, request)).thenReturn(expectedResponse);

        ResponseEntity<StockUpdateResponse> response = vendorController.updateStock(1L, 1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getNewStock()).isEqualTo(150);
        verify(vendorService).updateStock(1L, 1L, request);
    }

    @Test
    void updateStock_forOtherVendor_throwsVendorAccessDeniedException() {
        setupSecurityContext(1L);

        StockUpdateRequest request = new StockUpdateRequest(150);

        assertThatThrownBy(() -> vendorController.updateStock(2L, 1L, request))
            .isInstanceOf(VendorAccessDeniedException.class)
            .hasMessage("Access denied: You can only update your own stock");

        verify(vendorService, never()).updateStock(anyLong(), anyLong(), any());
    }
}
