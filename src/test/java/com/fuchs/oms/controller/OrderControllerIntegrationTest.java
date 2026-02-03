package com.fuchs.oms.controller;

import com.fuchs.oms.repository.OrderRepository;
import com.fuchs.oms.repository.VendorProductRepository;
import com.fuchs.oms.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VendorProductRepository vendorProductRepository;

    @Test
    void createOrder_withValidData_allocatesToLowestPriceVendorWithStock() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").exists())
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.quantity").value(10))
            .andExpect(jsonPath("$.allocatedVendorId").value(2))  // Vendor B - lowest price with stock ($45)
            .andExpect(jsonPath("$.status").value("ALLOCATED"))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createOrder_savesToDatabaseWithCorrectFields() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated());

        // Verify order was saved to database
        var orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        var order = orders.get(0);
        assertEquals(1L, order.getProductId());
        assertEquals(10, order.getQuantity());
        assertEquals(2L, order.getAllocatedVendorId());  // Vendor B
        assertEquals("ALLOCATED", order.getStatus());
        assert order.getCreatedAt() != null;
    }

    @Test
    void createOrder_withMissingProductId_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createOrder_withMissingQuantity_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createOrder_withNonExistentProduct_returns404() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 999, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Product not found: productId=999"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createOrder_createdAtIsSetAutomatically() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdAt").exists());

        // Also verify in database
        var orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        assert orders.get(0).getCreatedAt() != null;
    }

    @Test
    void createOrder_allocationSetsStatusToAllocated() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ALLOCATED"));
    }

    @Test
    void createOrder_withoutJwtToken_returns401() throws Exception {
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_decrementsAllocatedVendorStock() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        // Get initial stock for vendor 2 (should be 50 from data.sql)
        int initialStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertEquals(50, initialStock);

        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.allocatedVendorId").value(2));  // Vendor B

        // Verify stock was decremented
        int newStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertEquals(initialStock - 10, newStock);
    }

    @Test
    void createOrder_allocatesToVendorAWhenVendorBHasInsufficientStock() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        // Vendor B has $45 with 50 stock, Vendor A has $50 with 100 stock
        // Order for 60 units should go to Vendor A since B only has 50
        String requestJson = """
            {"productId": 1, "quantity": 60}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.allocatedVendorId").value(1))  // Vendor A (has 100 stock)
            .andExpect(jsonPath("$.status").value("ALLOCATED"));
    }

    @Test
    void createOrder_withZeroQuantity_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 0}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createOrder_withNegativeQuantity_returns400() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": -5}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    // ==================== FR13: No Stock Error Handling Integration Tests ====================

    @Test
    void createOrder_whenQuantityExceedsAllStock_returns400WithNoStockError() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        // Vendor A has 100, Vendor B has 50 - total 150
        // Order for 200 exceeds all stock
        String requestJson = """
            {"productId": 1, "quantity": 200}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("No vendor has stock for product: Widget"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createOrder_whenAllVendorsHaveZeroStock_returns400() throws Exception {
        // Set all vendor stocks to 0 via repository
        vendorProductRepository.findAll().forEach(vp -> {
            vp.setStock(0);
            vendorProductRepository.save(vp);
        });

        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("No vendor has stock for product: Widget"))
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createOrder_whenNoStock_doesNotCreateOrderRecord() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        long initialOrderCount = orderRepository.count();

        // Order for 1000 units - more than any vendor has
        String requestJson = """
            {"productId": 1, "quantity": 1000}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());

        // Verify no order was created in database
        assertThat(orderRepository.count()).isEqualTo(initialOrderCount);
    }

    @Test
    void createOrder_whenNoStock_doesNotDecrementAnyVendorStock() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        // Get initial stock levels
        int initialVendorAStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow()
            .getStock();
        int initialVendorBStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();

        // Order for 1000 units - more than any vendor has
        String requestJson = """
            {"productId": 1, "quantity": 1000}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());

        // Verify no stock was decremented
        int finalVendorAStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow()
            .getStock();
        int finalVendorBStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();

        assertThat(finalVendorAStock).isEqualTo(initialVendorAStock);
        assertThat(finalVendorBStock).isEqualTo(initialVendorBStock);
    }

    @Test
    void createOrder_multipleOrdersDecrementStockCorrectly() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        // First order: 40 units - should go to Vendor B ($45, 50 stock)
        String requestJson1 = """
            {"productId": 1, "quantity": 40}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson1))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.allocatedVendorId").value(2));  // Vendor B

        // Verify Vendor B stock is now 10
        int vendorBStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertEquals(10, vendorBStock);

        // Second order: 20 units - should go to Vendor A ($50, 100 stock) since B only has 10
        String requestJson2 = """
            {"productId": 1, "quantity": 20}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson2))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.allocatedVendorId").value(1));  // Vendor A

        // Verify Vendor A stock is now 80
        int vendorAStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow()
            .getStock();
        assertEquals(80, vendorAStock);
    }

    // ==================== Story 4B.1: Get Vendor Orders Integration Tests ====================

    @Test
    void getVendorOrders_withValidToken_returnsOnlyOwnOrders() throws Exception {
        // First, create an order that will be allocated to Vendor B (lowest price with stock)
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        String requestJson = """
            {"productId": 1, "quantity": 10}
            """;

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.allocatedVendorId").value(2));  // Allocated to Vendor B

        // Now Vendor B should see the order when they GET /api/orders
        String vendorBToken = jwtTokenProvider.generateToken(2L, "vendor-b");

        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + vendorBToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].allocatedVendorId").value(2))
            .andExpect(jsonPath("$[0].productId").value(1))
            .andExpect(jsonPath("$[0].quantity").value(10))
            .andExpect(jsonPath("$[0].status").value("ALLOCATED"));
    }

    @Test
    void getVendorOrders_withNoOrders_returnsEmptyList() throws Exception {
        // Vendor C (ID=3) has no stock, so no orders will ever be allocated to them
        String vendorCToken = jwtTokenProvider.generateToken(3L, "vendor-c");

        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + vendorCToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getVendorOrders_ordersAreSortedNewestFirst() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        // Create first order - goes to Vendor B
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productId": 1, "quantity": 5}
                    """))
            .andExpect(status().isCreated());

        // Create second order - also goes to Vendor B
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productId": 1, "quantity": 7}
                    """))
            .andExpect(status().isCreated());

        // Now Vendor B should see both orders, newest first
        String vendorBToken = jwtTokenProvider.generateToken(2L, "vendor-b");

        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + vendorBToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            // Second order (quantity 7) should be first (newest)
            .andExpect(jsonPath("$[0].quantity").value(7))
            // First order (quantity 5) should be second (older)
            .andExpect(jsonPath("$[1].quantity").value(5));
    }

    @Test
    void getVendorOrders_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getVendorOrders_doesNotReturnOtherVendorOrders() throws Exception {
        // Create order that allocates to Vendor B
        String anyToken = jwtTokenProvider.generateToken(1L, "vendor-a");
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + anyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productId": 1, "quantity": 10}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.allocatedVendorId").value(2));  // Allocated to Vendor B

        // Vendor A should NOT see the order (it was allocated to Vendor B)
        String vendorAToken = jwtTokenProvider.generateToken(1L, "vendor-a");
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + vendorAToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());  // Empty because order went to Vendor B
    }

    // ==================== Story 4B.2: Get Order By ID Integration Tests ====================

    @Test
    void getOrderById_withValidTokenAndOwnOrder_returnsOrderDetails() throws Exception {
        // First create an order that allocates to Vendor B
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productId": 1, "quantity": 10}
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        // Extract orderId from response
        String responseJson = createResult.getResponse().getContentAsString();
        Long orderId = JsonPath.parse(responseJson).read("$.orderId", Long.class);
        Long allocatedVendorId = JsonPath.parse(responseJson).read("$.allocatedVendorId", Long.class);

        // Now the allocated vendor should be able to get order details
        String vendorToken = jwtTokenProvider.generateToken(allocatedVendorId, "vendor-b");
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + vendorToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId))
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.quantity").value(10))
            .andExpect(jsonPath("$.allocatedVendorId").value(allocatedVendorId))
            .andExpect(jsonPath("$.status").value("ALLOCATED"))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getOrderById_withOtherVendorOrder_returns404() throws Exception {
        // Create order allocated to Vendor B
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productId": 1, "quantity": 10}
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        Long orderId = JsonPath.parse(createResult.getResponse().getContentAsString())
            .read("$.orderId", Long.class);

        // Vendor A tries to access order (allocated to Vendor B) - should get 404
        String vendorAToken = jwtTokenProvider.generateToken(1L, "vendor-a");
        mockMvc.perform(get("/api/orders/" + orderId)
                .header("Authorization", "Bearer " + vendorAToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Order not found: orderId=" + orderId))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getOrderById_withNonExistentOrder_returns404() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");

        mockMvc.perform(get("/api/orders/999999")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Order not found: orderId=999999"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getOrderById_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrderById_errorMessageDoesNotRevealOrderExists() throws Exception {
        // Create order allocated to Vendor B
        String token = jwtTokenProvider.generateToken(1L, "vendor-a");
        MvcResult createResult = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"productId": 1, "quantity": 10}
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        Long existingOrderId = JsonPath.parse(createResult.getResponse().getContentAsString())
            .read("$.orderId", Long.class);
        Long nonExistentOrderId = 999999L;

        // Get error responses for both cases
        String vendorAToken = jwtTokenProvider.generateToken(1L, "vendor-a");

        MvcResult existingOrderResult = mockMvc.perform(get("/api/orders/" + existingOrderId)
                .header("Authorization", "Bearer " + vendorAToken))
            .andExpect(status().isNotFound())
            .andReturn();

        MvcResult nonExistentOrderResult = mockMvc.perform(get("/api/orders/" + nonExistentOrderId)
                .header("Authorization", "Bearer " + vendorAToken))
            .andExpect(status().isNotFound())
            .andReturn();

        // Both should have same error structure (only orderId differs)
        // This verifies we don't leak information about order existence
        String existingError = JsonPath.parse(existingOrderResult.getResponse().getContentAsString())
            .read("$.error", String.class);
        String nonExistentError = JsonPath.parse(nonExistentOrderResult.getResponse().getContentAsString())
            .read("$.error", String.class);

        assertThat(existingError).startsWith("Order not found: orderId=");
        assertThat(nonExistentError).startsWith("Order not found: orderId=");
    }
}
