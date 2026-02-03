# Testing Documentation - Start Here! 📚

Welcome to the Fuchs Order Management System testing documentation.

---

## 🚀 Quick Navigation

**Choose your path**:

### 🆕 First Time User?
**Follow this order**:

1. **START_GUIDE.md** ← Start here!
   - Prerequisites check (Java, Maven)
   - Step-by-step startup instructions
   - Troubleshooting common issues
   - Verification steps

2. **QUICK_START_TEST.md** ← Quick 5-minute test
   - 11-step smoke test
   - Covers all core functionality
   - Perfect for verifying everything works

3. **MANUAL_TESTING_CHECKLIST.md** ← Full test suite
   - 50+ comprehensive test cases
   - All edge cases and error scenarios
   - Production readiness validation

4. **TEST_RESULTS_TEMPLATE.md** ← Document your results
   - Professional test report template
   - Bug tracking
   - Sign-off section

---

### 🔄 Returning User?
**If you've already set up before**:

1. Start application: `mvn spring-boot:run`
2. Open: http://localhost:8080/swagger-ui.html
3. Follow: **QUICK_START_TEST.md**

**Having issues?** → See **START_GUIDE.md** troubleshooting section

---

## 📋 What You'll Need

### Required
- ✅ Java 17 or higher
- ✅ Maven 3.6 or higher
- ✅ Web browser (Chrome, Firefox, Edge)
- ✅ Port 8080 available

### Optional
- Docker (if you want to test containerized deployment)
- Postman/Insomnia (alternative to Swagger UI)

**Don't have these?** → **START_GUIDE.md** has installation instructions

---

## 🎯 What This Application Does

Fuchs Order Management System is a REST API that:

1. **Authenticates vendors** using JWT tokens
2. **Manages vendor inventory** (prices and stock levels)
3. **Processes orders** with smart allocation to the cheapest vendor with stock
4. **Enforces security** (vendors can only access their own data)

---

## 🧪 Testing Overview

### Test Coverage

| Test Suite | Test Cases | Duration | File |
|-------------|-----------|----------|------|
| Quick Smoke Test | 11 | 5 min | QUICK_START_TEST.md |
| Full Manual Test | 50+ | 30-60 min | MANUAL_TESTING_CHECKLIST.md |

### What You'll Test

✅ **Authentication** - Login with username/password, receive JWT token
✅ **Vendor Management** - View products, update prices, update stock
✅ **Order Creation** - Place orders, verify allocation to cheapest vendor
✅ **Order Retrieval** - View assigned orders
✅ **Security** - Verify vendors can't access each other's data
✅ **Error Handling** - Invalid inputs, missing data, edge cases

---

## 🔧 Important Fix Applied

**If you're seeing "401 Unauthorized" on Swagger UI**:

I've fixed the security configuration to allow access to Swagger UI without authentication.

**To apply the fix**:
1. Stop your application (Ctrl+C)
2. Restart: `mvn spring-boot:run`
3. Access: http://localhost:8080/swagger-ui.html

Should now work without 401 errors!

---

## 📖 Document Descriptions

### START_GUIDE.md
**Purpose**: Get the application running without any issues

**Contents**:
- Prerequisites checklist (Java, Maven, port availability)
- 3 startup methods (Maven, Docker, JAR)
- Verification steps
- 10+ common issues with solutions
- Quick command reference

**When to use**:
- First time starting the app
- Encountering startup errors
- Need troubleshooting help

---

### QUICK_START_TEST.md
**Purpose**: Quick 5-minute smoke test of core functionality

**Contents**:
- 11-step guided test
- Tests all major features
- Includes expected results
- Quick troubleshooting tips

**When to use**:
- After startup to verify everything works
- Quick regression test after changes
- Demo purposes

---

### MANUAL_TESTING_CHECKLIST.md
**Purpose**: Comprehensive testing before production

**Contents**:
- 7 test suites (Health, Auth, Vendor, Orders, Security, E2E)
- 50+ test cases with expected results
- Edge cases and error scenarios
- Alternative testing methods (H2 Console, cURL)
- Performance testing guidelines

**When to use**:
- Pre-production validation
- Thorough testing after major changes
- Quality assurance sign-off

---

### TEST_RESULTS_TEMPLATE.md
**Purpose**: Document your testing results professionally

**Contents**:
- Test case checkboxes
- Bug tracking table
- Performance observations
- Production readiness assessment
- Sign-off section

**When to use**:
- Recording test results for documentation
- Quality assurance reports
- Production deployment approval

---

## 🚦 Testing Workflow

### Recommended Process

```
┌─────────────────────┐
│  START_GUIDE.md     │  ← Get app running
│  (First time: 5min) │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ QUICK_START_TEST.md │  ← Smoke test (5min)
│ All tests pass?     │
└──────────┬──────────┘
           │
      ┌────┴────┐
      │   YES   │───────────────┐
      └─────────┘               │
           │                    │
           ▼                    ▼
┌─────────────────────┐  ┌──────────────┐
│MANUAL_TESTING_      │  │ Ready for    │
│CHECKLIST.md         │  │ Production!  │
│(30-60min)           │  └──────────────┘
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│TEST_RESULTS_        │
│TEMPLATE.md          │
│(Document findings)  │
└─────────────────────┘
```

---

## 💡 Testing Tips

### For Best Results

1. **Test in order** - Health → Auth → Vendor → Orders → Security
2. **Read expected results** before executing tests
3. **Take screenshots** of successful tests
4. **Document any bugs** in the template
5. **Test security thoroughly** - try to break access controls
6. **Test with different vendors** - login as vendor-a, vendor-b, vendor-c
7. **Test edge cases** - negative numbers, zero values, non-existent IDs

### Common Mistakes to Avoid

❌ Forgetting to click "Authorize" in Swagger UI
❌ Using wrong vendor ID (e.g., logged in as vendor-a, trying to access vendor-b's data)
❌ Not copying the JWT token correctly
❌ Testing before application fully starts
❌ Skipping error scenario tests

---

## 🎓 How to Use Swagger UI

Swagger UI is your testing interface. Here's how it works:

### 1. Authentication Flow
```
1. Login (POST /api/auth/login) → Get JWT token
2. Click "Authorize" button → Paste token
3. Now you can test protected endpoints
```

### 2. Testing an Endpoint
```
1. Find endpoint in list (e.g., GET /api/vendors/{vendorId}/products)
2. Click to expand
3. Click "Try it out"
4. Fill in parameters
5. Click "Execute"
6. See response below
```

### 3. Reading Responses
- **200 OK** = Success ✅
- **401 Unauthorized** = Need to login/authorize ⚠️
- **403 Forbidden** = Access denied (correct behavior for security tests) ✅
- **404 Not Found** = Resource doesn't exist ⚠️
- **400 Bad Request** = Invalid input ⚠️

---

## 📊 Test Data Reference

### Pre-loaded Vendors

| Vendor | Username | Password | Price | Stock | Notes |
|--------|----------|----------|-------|-------|-------|
| Vendor Alpha | vendor-a | password123 | $50.00 | 100 | Most expensive |
| Vendor Beta | vendor-b | password123 | $45.00 | 50 | **Cheapest with stock** |
| Vendor Charlie | vendor-c | password123 | $40.00 | 0 | Cheapest but no stock |

### Product

| ID | Code | Name |
|----|------|------|
| 1 | WDG-001 | Widget |

### Expected Allocation Logic

When you create an order:
1. System checks all vendors for the product
2. Filters vendors with sufficient stock
3. Selects vendor with **lowest price**
4. **Result**: Orders allocate to Vendor B ($45, 50 stock available)

---

## 🆘 Getting Help

### Issue Priority

1. **Can't start application** → START_GUIDE.md troubleshooting
2. **401 on Swagger UI** → Restart application (fix applied)
3. **Test failures** → Check MANUAL_TESTING_CHECKLIST.md for expected results
4. **Unexpected behavior** → Document in TEST_RESULTS_TEMPLATE.md

### Quick Fixes

**Port already in use**:
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Application won't start**:
```bash
mvn clean install
mvn spring-boot:run
```

**Swagger UI 401 error**:
- Restart application (fix was applied to SecurityConfig.java)

**Can't login**:
- Verify credentials: vendor-a / password123
- Check console logs for errors

---

## ✅ Success Checklist

Before you start testing, verify:

- [ ] Java 17+ installed
- [ ] Maven 3.6+ installed
- [ ] Port 8080 available
- [ ] Application starts successfully
- [ ] http://localhost:8080/api/health returns `{"status":"UP"}`
- [ ] http://localhost:8080/swagger-ui.html loads without errors
- [ ] Can login and receive JWT token
- [ ] Have START_GUIDE.md open for troubleshooting
- [ ] Have QUICK_START_TEST.md ready to follow

**All checked?** You're ready to test! 🚀

---

## 🎯 Next Step

**Start here**: Open **START_GUIDE.md** and follow the startup instructions!

**Good luck with your testing!** 🚀
