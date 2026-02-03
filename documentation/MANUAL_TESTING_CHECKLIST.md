# Fuchs Order Management System - Manual Testing Checklist

## Before You Begin

**IMPORTANT**: If you haven't started the application yet, follow `START_GUIDE.md` first!

This guide assumes your application is running successfully. If you encounter startup issues, refer to the troubleshooting section in `START_GUIDE.md`.

## Testing Environment
- **URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## Test Data (Pre-loaded Vendors)

| Vendor ID | Username | Password | Product | Price | Stock |
|-----------|----------|----------|---------|-------|-------|
| 1 | vendor-a | password123 | Widget (ID: 1) | $50.00 | 100 |
| 2 | vendor-b | password123 | Widget (ID: 1) | $45.00 | 50 |
| 3 | vendor-c | password123 | Widget (ID: 1) | $40.00 | 0 |

---

## ✅ Test Suite 1: Health Check

### Test 1.1: Verify Application is Running
**Endpoint**: `GET /api/health`

**Steps in Swagger UI**:
1. Find "health-controller" section
2. Click on `GET /api/health`
3. Click "Try it out"
4. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Response Body: `{"status":"UP"}`

**Pass Criteria**: Application responds with UP status

---

## ✅ Test Suite 2: Authentication

### Test 2.1: Successful Login - Vendor A
**Endpoint**: `POST /api/auth/login`

**Steps in Swagger UI**:
1. Find "auth-controller" section
2. Click on `POST /api/auth/login`
3. Click "Try it out"
4. Enter request body:
```json
{
  "username": "vendor-a",
  "password": "password123"
}
```
5. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Response contains `token` field with JWT string
- ✅ Token starts with "eyJ" (JWT format)
- ✅ Response example:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZW5kb3ItYSIsInZlbmRvcklkIjoxLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0..."
}
```

**Action**: **COPY THIS TOKEN** - You'll need it for all subsequent tests!

---

### Test 2.2: Successful Login - Vendor B
**Endpoint**: `POST /api/auth/login`

**Steps**: Same as Test 2.1, but use:
```json
{
  "username": "vendor-b",
  "password": "password123"
}
```

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Receives different JWT token (contains vendorId: 2)

**Action**: **COPY THIS TOKEN TOO** - For vendor-specific tests

---

### Test 2.3: Failed Login - Invalid Password
**Endpoint**: `POST /api/auth/login`

**Steps**: Use incorrect password:
```json
{
  "username": "vendor-a",
  "password": "wrongpassword"
}
```

**Expected Result**:
- ✅ Status Code: `401 Unauthorized`
- ✅ Error message: "Invalid username or password"

---

### Test 2.4: Failed Login - Non-existent User
**Endpoint**: `POST /api/auth/login`

**Steps**: Use non-existent username:
```json
{
  "username": "fake-vendor",
  "password": "password123"
}
```

**Expected Result**:
- ✅ Status Code: `401 Unauthorized`
- ✅ Error message: "Invalid username or password"

---

### Test 2.5: Missing Credentials
**Endpoint**: `POST /api/auth/login`

**Steps**: Send empty request:
```json
{
  "username": "",
  "password": ""
}
```

**Expected Result**:
- ✅ Status Code: `400 Bad Request` or `401 Unauthorized`
- ✅ Error indicates missing/invalid credentials

---

## ✅ Test Suite 3: Vendor Product Management

**Important**: For ALL tests below, you must authenticate first!

### How to Add JWT Token in Swagger UI:
1. Scroll to top of Swagger UI page
2. Click the green **"Authorize"** button (or lock icon 🔒)
3. In the dialog, paste your JWT token from Test 2.1
4. Format: `Bearer <your-token-here>` (Swagger may add "Bearer" automatically)
5. Click "Authorize"
6. Click "Close"

---

### Test 3.1: View Vendor A's Products
**Endpoint**: `GET /api/vendors/{vendorId}/products`

**Prerequisites**: Login as vendor-a (Test 2.1) and authorize

**Steps**:
1. Click on `GET /api/vendors/{vendorId}/products`
2. Click "Try it out"
3. Enter vendorId: `1`
4. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Response is an array with 1 product:
```json
[
  {
    "productId": 1,
    "productCode": "WDG-001",
    "productName": "Widget",
    "price": 50.00,
    "stock": 100
  }
]
```

---

### Test 3.2: Security Test - Vendor A Cannot Access Vendor B's Products
**Endpoint**: `GET /api/vendors/{vendorId}/products`

**Prerequisites**: Still logged in as vendor-a

**Steps**:
1. Try to access Vendor B's products
2. Enter vendorId: `2` (not your own vendor)
3. Click "Execute"

**Expected Result**:
- ✅ Status Code: `403 Forbidden`
- ✅ Error message: "Access denied. You can only access your own vendor data."

---

### Test 3.3: Update Product Price - Vendor A
**Endpoint**: `PUT /api/vendors/{vendorId}/products/{productId}/price`

**Prerequisites**: Login as vendor-a

**Steps**:
1. Click on `PUT /api/vendors/{vendorId}/products/{productId}/price`
2. Click "Try it out"
3. Enter vendorId: `1`
4. Enter productId: `1`
5. Request body:
```json
{
  "price": 48.99
}
```
6. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Response shows updated price:
```json
{
  "productId": 1,
  "productCode": "WDG-001",
  "productName": "Widget",
  "price": 48.99,
  "stock": 100
}
```

**Verification**: Run Test 3.1 again - price should be $48.99

---

### Test 3.4: Update Product Price - Invalid Negative Price
**Endpoint**: `PUT /api/vendors/{vendorId}/products/{productId}/price`

**Prerequisites**: Login as vendor-a

**Steps**: Try to set negative price:
```json
{
  "price": -10.00
}
```

**Expected Result**:
- ✅ Status Code: `400 Bad Request`
- ✅ Error indicates price must be positive

---

### Test 3.5: Update Product Stock - Vendor A
**Endpoint**: `PUT /api/vendors/{vendorId}/products/{productId}/stock`

**Prerequisites**: Login as vendor-a

**Steps**:
1. Click on `PUT /api/vendors/{vendorId}/products/{productId}/stock`
2. Click "Try it out"
3. Enter vendorId: `1`
4. Enter productId: `1`
5. Request body:
```json
{
  "stock": 150
}
```
6. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Response shows updated stock:
```json
{
  "productId": 1,
  "productCode": "WDG-001",
  "productName": "Widget",
  "price": 48.99,
  "stock": 150
}
```

---

### Test 3.6: Update Stock - Invalid Negative Stock
**Endpoint**: `PUT /api/vendors/{vendorId}/products/{productId}/stock`

**Steps**: Try to set negative stock:
```json
{
  "stock": -5
}
```

**Expected Result**:
- ✅ Status Code: `400 Bad Request`
- ✅ Error indicates stock cannot be negative

---

### Test 3.7: Update Non-existent Product
**Endpoint**: `PUT /api/vendors/{vendorId}/products/{productId}/price`

**Prerequisites**: Login as vendor-a

**Steps**:
1. Enter vendorId: `1`
2. Enter productId: `999` (doesn't exist)
3. Request body:
```json
{
  "price": 25.00
}
```
4. Click "Execute"

**Expected Result**:
- ✅ Status Code: `404 Not Found`
- ✅ Error message about product not found

---

## ✅ Test Suite 4: Order Creation & Allocation

### Test 4.1: Create Order - Successful Allocation to Cheapest Vendor
**Endpoint**: `POST /api/orders`

**Prerequisites**:
- Login as vendor-a (or any vendor)
- IMPORTANT: Reset prices first if you changed them:
  - Vendor A: Set price back to $50.00 (Test 3.3)
  - Vendor B: Login as vendor-b, set price to $45.00

**Steps**:
1. Click on `POST /api/orders`
2. Click "Try it out"
3. Request body:
```json
{
  "productId": 1,
  "quantity": 10
}
```
4. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Order allocated to **Vendor B** (cheapest with stock):
```json
{
  "orderId": 1,
  "productId": 1,
  "productName": "Widget",
  "quantity": 10,
  "allocatedVendorId": 2,
  "allocatedVendorName": "Vendor Beta",
  "price": 45.00,
  "totalPrice": 450.00,
  "status": "ALLOCATED"
}
```

**Why Vendor B?**:
- Vendor C: $40 but 0 stock ❌
- Vendor B: $45 with 50 stock ✅ (CHEAPEST with stock)
- Vendor A: $50 with 100 stock

---

### Test 4.2: Create Order - Allocation to Second Cheapest (Vendor B Out of Stock)
**Endpoint**: `POST /api/orders`

**Prerequisites**:
1. Login as vendor-b
2. Set Vendor B's stock to 5: `PUT /api/vendors/2/products/1/stock`
```json
{
  "stock": 5
}
```
3. Login as vendor-a (or stay logged in)

**Steps**: Create order for 10 widgets:
```json
{
  "productId": 1,
  "quantity": 10
}
```

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Order allocated to **Vendor A** (Vendor B has insufficient stock):
```json
{
  "orderId": 2,
  "productId": 1,
  "quantity": 10,
  "allocatedVendorId": 1,
  "allocatedVendorName": "Vendor Alpha",
  "price": 50.00,
  "totalPrice": 500.00,
  "status": "ALLOCATED"
}
```

---

### Test 4.3: Create Order - No Stock Available
**Endpoint**: `POST /api/orders`

**Prerequisites**:
1. Set all vendors' stock to 0:
   - Login as vendor-a: `PUT /api/vendors/1/products/1/stock` → `{"stock": 0}`
   - Login as vendor-b: `PUT /api/vendors/2/products/1/stock` → `{"stock": 0}`
   - Vendor C already has 0 stock

**Steps**: Try to create order:
```json
{
  "productId": 1,
  "quantity": 10
}
```

**Expected Result**:
- ✅ Status Code: `400 Bad Request`
- ✅ Error message: "No vendors have sufficient stock for this product"

---

### Test 4.4: Create Order - Invalid Product
**Endpoint**: `POST /api/orders`

**Steps**: Order non-existent product:
```json
{
  "productId": 999,
  "quantity": 10
}
```

**Expected Result**:
- ✅ Status Code: `404 Not Found`
- ✅ Error message: "Product not found"

---

### Test 4.5: Create Order - Invalid Quantity (Zero)
**Endpoint**: `POST /api/orders`

**Steps**: Order with zero quantity:
```json
{
  "productId": 1,
  "quantity": 0
}
```

**Expected Result**:
- ✅ Status Code: `400 Bad Request`
- ✅ Error indicates quantity must be greater than 0

---

### Test 4.6: Create Order - Invalid Quantity (Negative)
**Endpoint**: `POST /api/orders`

**Steps**: Order with negative quantity:
```json
{
  "productId": 1,
  "quantity": -5
}
```

**Expected Result**:
- ✅ Status Code: `400 Bad Request`
- ✅ Error indicates quantity must be positive

---

## ✅ Test Suite 5: Order Retrieval

**Prerequisites for this section**:
1. Reset stock levels:
   - Vendor A: 100 stock
   - Vendor B: 50 stock
2. Create 2-3 orders using Test 4.1 steps
3. Note the order IDs returned

---

### Test 5.1: Get Vendor's Allocated Orders
**Endpoint**: `GET /api/orders`

**Prerequisites**: Login as vendor-b (who should have allocated orders)

**Steps**:
1. Click on `GET /api/orders`
2. Click "Try it out"
3. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Returns array of orders allocated to Vendor B:
```json
[
  {
    "orderId": 1,
    "productId": 1,
    "productName": "Widget",
    "quantity": 10,
    "allocatedVendorId": 2,
    "allocatedVendorName": "Vendor Beta",
    "price": 45.00,
    "totalPrice": 450.00,
    "status": "ALLOCATED"
  }
]
```

---

### Test 5.2: Get Specific Order by ID
**Endpoint**: `GET /api/orders/{orderId}`

**Prerequisites**: Login as vendor who was allocated the order

**Steps**:
1. Click on `GET /api/orders/{orderId}`
2. Click "Try it out"
3. Enter orderId: `1` (use actual order ID from your tests)
4. Click "Execute"

**Expected Result**:
- ✅ Status Code: `200 OK`
- ✅ Returns the specific order details

---

### Test 5.3: Security Test - Cannot View Another Vendor's Order
**Endpoint**: `GET /api/orders/{orderId}`

**Prerequisites**:
1. Know an order ID allocated to Vendor B (e.g., order #1)
2. Login as vendor-a (different vendor)

**Steps**:
1. Try to access Vendor B's order
2. Enter orderId: `1`
3. Click "Execute"

**Expected Result**:
- ✅ Status Code: `403 Forbidden`
- ✅ Error message: "Access denied. This order is not allocated to your vendor."

---

### Test 5.4: Get Non-existent Order
**Endpoint**: `GET /api/orders/{orderId}`

**Steps**: Request order that doesn't exist:
- Enter orderId: `99999`

**Expected Result**:
- ✅ Status Code: `404 Not Found`
- ✅ Error message: "Order not found"

---

## ✅ Test Suite 6: Authorization & Security

### Test 6.1: Access Protected Endpoint Without Token
**Endpoint**: `GET /api/vendors/1/products`

**Steps**:
1. Click the **"Authorize"** button
2. Click **"Logout"** to clear token
3. Try to access `GET /api/vendors/1/products`

**Expected Result**:
- ✅ Status Code: `401 Unauthorized`
- ✅ Error indicates missing or invalid token

---

### Test 6.2: Access with Invalid/Expired Token
**Endpoint**: `GET /api/vendors/1/products`

**Steps**:
1. Click "Authorize"
2. Enter fake token: `Bearer invalidtoken123`
3. Try to access endpoint

**Expected Result**:
- ✅ Status Code: `401 Unauthorized`
- ✅ Error indicates invalid token

---

## ✅ Test Suite 7: End-to-End Workflow Test

### Complete Business Flow Test

**Scenario**: Customer orders 20 widgets. System allocates to cheapest vendor. Vendor updates stock.

**Steps**:

1. **Setup**: Login as vendor-a, set price to $55, stock to 200
2. **Setup**: Login as vendor-b, set price to $50, stock to 100
3. **Order**: Create order for 20 widgets
   - Expected: Allocated to Vendor B ($50)
4. **View Order**: Login as vendor-b, get all orders
   - Expected: See the order for 20 widgets
5. **Update Stock**: Vendor B updates stock to 80 (100 - 20)
6. **Create Another Order**: Order 90 widgets
   - Expected: Should fail or allocate to Vendor A (Vendor B has only 80 left)
7. **Verify**: Check Vendor A's orders
   - Expected: Should see the second order

**Pass Criteria**: All steps complete successfully with expected results

---

## 🎯 Testing Summary Checklist

Use this quick checklist to ensure you've tested everything:

### Core Functionality
- [ ] Application starts successfully
- [ ] Health check responds
- [ ] Swagger UI is accessible

### Authentication
- [ ] Successful login returns JWT token
- [ ] Invalid credentials are rejected
- [ ] Missing credentials are rejected

### Vendor Management
- [ ] Vendors can view their own products
- [ ] Vendors cannot view other vendors' products (403)
- [ ] Can update product price
- [ ] Can update product stock
- [ ] Cannot set negative price
- [ ] Cannot set negative stock
- [ ] Cannot update non-existent product

### Order Creation
- [ ] Order allocates to cheapest vendor with stock
- [ ] Order allocates to next-cheapest when first is out of stock
- [ ] Order fails when no vendor has stock
- [ ] Cannot order non-existent product
- [ ] Cannot order zero or negative quantity

### Order Retrieval
- [ ] Vendors can view their allocated orders
- [ ] Vendors can view specific order details
- [ ] Vendors cannot view other vendors' orders (403)
- [ ] Non-existent order returns 404

### Security
- [ ] Protected endpoints require authentication
- [ ] Invalid tokens are rejected
- [ ] Vendors can only access their own data

### End-to-End
- [ ] Complete business workflow works correctly

---

## 🐛 Troubleshooting

### Issue: "401 Unauthorized" on all requests
**Solution**:
1. Ensure you've logged in first (`POST /api/auth/login`)
2. Copy the token from response
3. Click "Authorize" button in Swagger UI
4. Paste token (with or without "Bearer " prefix)
5. Click "Authorize" then "Close"

### Issue: "403 Forbidden" errors
**Solution**:
- Ensure the vendorId in the URL matches your logged-in vendor
- If logged in as vendor-a (ID: 1), only use vendorId: 1

### Issue: Application won't start
**Solution**:
1. Check if port 8080 is already in use
2. On Windows: `netstat -ano | findstr :8080`
3. Kill the process or change port in `application.yml`

### Issue: Swagger UI not loading
**Solution**:
- Wait 30 seconds after application starts
- Clear browser cache
- Try incognito mode
- Verify URL: http://localhost:8080/swagger-ui.html

---

## 📊 Alternative Testing Methods

### Option 1: H2 Database Console (View Data Directly)
1. Go to: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: (leave empty)
5. Click "Connect"
6. Run SQL queries to inspect data:
```sql
SELECT * FROM vendors;
SELECT * FROM products;
SELECT * FROM vendor_products;
SELECT * FROM orders;
```

### Option 2: Use cURL Commands (Terminal)
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"vendor-a","password":"password123"}'

# Use token in subsequent requests
curl -X GET http://localhost:8080/api/vendors/1/products \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Option 3: Browser Extensions
- **REST Client** (VS Code extension)
- **Postman** (desktop app or web)
- **Insomnia** (desktop app)

---

## ✅ Final Verification

After completing all tests, verify:

1. **All authentication flows work** (login success/failure)
2. **All CRUD operations work** (view, update products)
3. **Order allocation logic is correct** (allocates to cheapest vendor)
4. **Security is enforced** (vendors can't access each other's data)
5. **Error handling is proper** (404s, 400s, 403s return correctly)
6. **End-to-end workflow completes** (order → allocate → view)

**PASS CRITERIA**: All checkboxes above are checked ✅

---

## 📝 Notes
- Take screenshots of successful tests for documentation
- Record any bugs or unexpected behavior
- Note performance (response times should be < 500ms)
- Test with multiple browser tabs (simulate concurrent vendors)

**Good luck with your testing!** 🚀
