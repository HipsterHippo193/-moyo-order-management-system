package com.moyo.oms.controller;

import com.moyo.oms.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VendorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getProducts_withValidToken_returnsProducts() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        mockMvc.perform(get("/api/vendors/1/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].productId").value(1))
            .andExpect(jsonPath("$[0].productCode").value("widget-001"))
            .andExpect(jsonPath("$[0].name").value("Widget"))
            .andExpect(jsonPath("$[0].price").value(50.00));
    }

    @Test
    void getProducts_forOtherVendor_returns403() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        mockMvc.perform(get("/api/vendors/2/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access denied: You can only access your own data"))
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void getProducts_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/vendors/1/products"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePrice_withValidData_returnsUpdatedPrice() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": 55.00}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.productCode").value("widget-001"))
            .andExpect(jsonPath("$.productName").value("Widget"))
            .andExpect(jsonPath("$.newPrice").value(55.00))
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updatePrice_forOtherVendor_returns403() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": 55.00}
            """;

        mockMvc.perform(put("/api/vendors/2/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access denied: You can only update your own prices"))
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void updatePrice_withInvalidNegativePrice_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": -10.00}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updatePrice_withZeroPrice_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": 0.00}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updatePrice_withNullPrice_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": null}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updatePrice_forNonExistentProduct_returns404() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": 55.00}
            """;

        mockMvc.perform(put("/api/vendors/1/products/999/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Product not found for vendor: vendorId=1, productId=999"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updatePrice_withoutToken_returns401() throws Exception {
        String requestJson = """
            {"price": 55.00}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePrice_verifyPriceActuallyUpdated() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        // First update the price
        String updateJson = """
            {"price": 65.00}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newPrice").value(65.00));

        // Then verify the price was actually changed
        mockMvc.perform(get("/api/vendors/1/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].price").value(65.00));
    }

    @Test
    void getProducts_vendorB_returnsVendorBProducts() throws Exception {
        String token = jwtTokenProvider.generateToken(2L, "vendor-b");

        mockMvc.perform(get("/api/vendors/2/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].productId").value(1))
            .andExpect(jsonPath("$[0].price").value(45.00));
    }

    @Test
    void getProducts_vendorC_returnsVendorCProducts() throws Exception {
        String token = jwtTokenProvider.generateToken(3L, "vendor-c");

        mockMvc.perform(get("/api/vendors/3/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].productId").value(1))
            .andExpect(jsonPath("$[0].price").value(40.00));
    }

    @Test
    void updatePrice_withMalformedJson_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String malformedJson = "{ invalid json }";

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updatePrice_withMissingPriceField_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String emptyJson = "{}";

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updatePrice_withLargePrice_succeeds() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": 99999999.99}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newPrice").value(99999999.99));
    }

    @Test
    void updatePrice_withHighPrecisionDecimal_roundsCorrectly() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": 45.50}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newPrice").value(45.50));
    }

    @Test
    void updatePrice_withStringPrice_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"price": "not-a-number"}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/price")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    // Stock endpoint integration tests

    @Test
    void getProducts_withValidToken_includesStock() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        mockMvc.perform(get("/api/vendors/1/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stock").value(100));  // Vendor A has 100 stock
    }

    @Test
    void updateStock_withValidData_returnsUpdatedStock() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": 150}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.productCode").value("widget-001"))
            .andExpect(jsonPath("$.productName").value("Widget"))
            .andExpect(jsonPath("$.newStock").value(150))
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateStock_forOtherVendor_returns403() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": 100}
            """;

        mockMvc.perform(put("/api/vendors/2/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access denied: You can only update your own stock"))
            .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void updateStock_withNegativeStock_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": -10}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateStock_forNonExistentProduct_returns404() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": 100}
            """;

        mockMvc.perform(put("/api/vendors/1/products/999/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Product not found for vendor: vendorId=1, productId=999"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateStock_withoutToken_returns401() throws Exception {
        String requestJson = """
            {"stock": 100}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateStock_withZeroStock_succeeds() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": 0}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newStock").value(0));
    }

    @Test
    void updateStock_withLargeStock_succeeds() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": 999999999}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newStock").value(999999999));
    }

    @Test
    void updateStock_verifyStockActuallyUpdated() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        // First update the stock
        String updateJson = """
            {"stock": 200}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newStock").value(200));

        // Then verify the stock was actually changed
        mockMvc.perform(get("/api/vendors/1/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stock").value(200));
    }

    @Test
    void updateStock_withNullStock_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"stock": null}
            """;

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateStock_withMissingStockField_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String emptyJson = "{}";

        mockMvc.perform(put("/api/vendors/1/products/1/stock")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getProducts_vendorB_includesStock() throws Exception {
        String token = jwtTokenProvider.generateToken(2L, "vendor-b");

        mockMvc.perform(get("/api/vendors/2/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stock").value(50));  // Vendor B has 50 stock
    }

    @Test
    void getProducts_vendorC_includesZeroStock() throws Exception {
        String token = jwtTokenProvider.generateToken(3L, "vendor-c");

        mockMvc.perform(get("/api/vendors/3/products")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stock").value(0));  // Vendor C has 0 stock
    }
}
