# Quick Start Testing Guide (5 Minutes)

This is a condensed version for rapid smoke testing. For comprehensive testing, see `MANUAL_TESTING_CHECKLIST.md`.

## Prerequisites

### IMPORTANT: First Time Setup
**If this is your first time starting the application**, please follow `START_GUIDE.md` first to avoid any issues!

### Start Your Application
```bash
mvn spring-boot:run
```

**Wait for**: `Started FuchsOmsApplication in X.XXX seconds`

**Troubleshooting**: If you encounter any startup issues, see `START_GUIDE.md`

---

## Step 1: Open Swagger UI
Open browser to: **http://localhost:8080/swagger-ui.html**

---

## Step 2: Test Health Check (No Auth Required)

1. Find `health-controller`
2. Click `GET /api/health`
3. Click "Try it out" → "Execute"
4. **Expected**: `200 OK` with `{"status":"UP"}`

✅ **PASS** if status is UP

---

## Step 3: Login and Get Token

1. Find `auth-controller`
2. Click `POST /api/auth/login`
3. Click "Try it out"
4. Paste this JSON:
```json
{
  "username": "vendor-b",
  "password": "password123"
}
```
5. Click "Execute"
6. **Expected**: `200 OK` with a long JWT token

**COPY THE TOKEN** (looks like: `eyJhbGciOiJIUzI1NiJ9...`)

✅ **PASS** if you get a token

---

## Step 4: Authorize in Swagger

1. Scroll to top, click green **"Authorize"** button (or 🔒 icon)
2. Paste your token in the value field
3. Click "Authorize"
4. Click "Close"

You're now authenticated as Vendor B!

---

## Step 5: View Your Products

1. Find `vendor-controller`
2. Click `GET /api/vendors/{vendorId}/products`
3. Click "Try it out"
4. Enter `vendorId`: **2** (Vendor B's ID)
5. Click "Execute"

**Expected**: `200 OK` with Widget product:
```json
[
  {
    "productId": 1,
    "productCode": "WDG-001",
    "productName": "Widget",
    "price": 45.00,
    "stock": 50
  }
]
```

✅ **PASS** if you see your product with price $45.00

---

## Step 6: Update Product Price

1. Stay in `vendor-controller`
2. Click `PUT /api/vendors/{vendorId}/products/{productId}/price`
3. Click "Try it out"
4. Enter `vendorId`: **2**
5. Enter `productId`: **1**
6. Paste request body:
```json
{
  "price": 42.99
}
```
7. Click "Execute"

**Expected**: `200 OK` with updated price $42.99

✅ **PASS** if price updated successfully

---

## Step 7: Update Product Stock

1. Click `PUT /api/vendors/{vendorId}/products/{productId}/stock`
2. Click "Try it out"
3. Enter `vendorId`: **2**
4. Enter `productId`: **1**
5. Paste request body:
```json
{
  "stock": 75
}
```
6. Click "Execute"

**Expected**: `200 OK` with updated stock 75

✅ **PASS** if stock updated successfully

---

## Step 8: Create an Order

1. Find `order-controller`
2. Click `POST /api/orders`
3. Click "Try it out"
4. Paste request body:
```json
{
  "productId": 1,
  "quantity": 10
}
```
5. Click "Execute"

**Expected**: `200 OK` with order allocated to **Vendor B** (you, cheapest vendor):
```json
{
  "orderId": 1,
  "productId": 1,
  "productName": "Widget",
  "quantity": 10,
  "allocatedVendorId": 2,
  "allocatedVendorName": "Vendor Beta",
  "price": 42.99,
  "totalPrice": 429.90,
  "status": "ALLOCATED"
}
```

**Note the orderId!**

✅ **PASS** if order allocated to Vendor B (ID: 2)

---

## Step 9: View Your Orders

1. Click `GET /api/orders`
2. Click "Try it out"
3. Click "Execute"

**Expected**: `200 OK` with array containing your order from Step 8

✅ **PASS** if you see your order

---

## Step 10: Get Specific Order

1. Click `GET /api/orders/{orderId}`
2. Click "Try it out"
3. Enter `orderId`: **1** (or the ID from Step 8)
4. Click "Execute"

**Expected**: `200 OK` with your order details

✅ **PASS** if order details match

---

## Step 11: Test Security - Try Accessing Another Vendor's Products

1. Go back to `GET /api/vendors/{vendorId}/products`
2. Click "Try it out"
3. Enter `vendorId`: **1** (Vendor A, not you!)
4. Click "Execute"

**Expected**: `403 Forbidden` with error message

✅ **PASS** if you get 403 Forbidden (security working!)

---

## 🎉 Smoke Test Complete!

If all 11 steps passed, your application is working correctly!

### Quick Summary:
- ✅ Health check works
- ✅ Authentication works
- ✅ JWT authorization works
- ✅ View products works
- ✅ Update price works
- ✅ Update stock works
- ✅ Create order works (with smart allocation)
- ✅ View orders works
- ✅ Security prevents cross-vendor access

---

## Next Steps

For comprehensive testing covering all edge cases and error scenarios:
👉 See **MANUAL_TESTING_CHECKLIST.md**

This includes:
- Invalid credentials testing
- Negative price/stock validation
- Out of stock scenarios
- Non-existent product/order handling
- Complete end-to-end workflows
- 50+ test cases with expected results

---

## Troubleshooting

**Getting 401 errors?**
- Make sure you clicked "Authorize" and pasted your token

**Getting 403 errors on your own vendor?**
- Ensure vendorId matches your logged-in vendor (vendor-b = ID 2)

**Application not starting?**
- Check if port 8080 is in use
- Look for errors in console output

**Need to reset data?**
- Restart the application (it uses in-memory database)
- All data resets to initial state
