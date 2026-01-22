# Manual Test Results

**Tester Name**: ___________________________
**Test Date**: ___________________________
**Application Version/Commit**: ___________________________
**Environment**: Local Development / Docker
**Browser**: ___________________________

---

## Quick Smoke Test Results

| # | Test Case | Status | Notes |
|---|-----------|--------|-------|
| 1 | Health Check | ⬜ PASS ⬜ FAIL | |
| 2 | Login - Vendor B | ⬜ PASS ⬜ FAIL | Token: ____________ |
| 3 | Authorize in Swagger | ⬜ PASS ⬜ FAIL | |
| 4 | View Products (Vendor B) | ⬜ PASS ⬜ FAIL | |
| 5 | Update Price | ⬜ PASS ⬜ FAIL | New Price: _______ |
| 6 | Update Stock | ⬜ PASS ⬜ FAIL | New Stock: _______ |
| 7 | Create Order | ⬜ PASS ⬜ FAIL | Order ID: _______ |
| 8 | View All Orders | ⬜ PASS ⬜ FAIL | |
| 9 | View Specific Order | ⬜ PASS ⬜ FAIL | |
| 10 | Security Test (403) | ⬜ PASS ⬜ FAIL | |

**Overall Smoke Test**: ⬜ PASS ⬜ FAIL

---

## Detailed Test Results

### Authentication Tests

| Test ID | Test Case | Expected | Actual | Status | Notes |
|---------|-----------|----------|--------|--------|-------|
| 2.1 | Login Vendor A | 200 OK + token | | ⬜ PASS ⬜ FAIL | |
| 2.2 | Login Vendor B | 200 OK + token | | ⬜ PASS ⬜ FAIL | |
| 2.3 | Invalid Password | 401 Unauthorized | | ⬜ PASS ⬜ FAIL | |
| 2.4 | Non-existent User | 401 Unauthorized | | ⬜ PASS ⬜ FAIL | |
| 2.5 | Missing Credentials | 400/401 Error | | ⬜ PASS ⬜ FAIL | |

### Vendor Product Management Tests

| Test ID | Test Case | Expected | Actual | Status | Notes |
|---------|-----------|----------|--------|--------|-------|
| 3.1 | View Own Products | 200 OK + products | | ⬜ PASS ⬜ FAIL | |
| 3.2 | View Other Vendor Products | 403 Forbidden | | ⬜ PASS ⬜ FAIL | |
| 3.3 | Update Price (Valid) | 200 OK + updated | | ⬜ PASS ⬜ FAIL | |
| 3.4 | Update Price (Negative) | 400 Bad Request | | ⬜ PASS ⬜ FAIL | |
| 3.5 | Update Stock (Valid) | 200 OK + updated | | ⬜ PASS ⬜ FAIL | |
| 3.6 | Update Stock (Negative) | 400 Bad Request | | ⬜ PASS ⬜ FAIL | |
| 3.7 | Update Non-existent Product | 404 Not Found | | ⬜ PASS ⬜ FAIL | |

### Order Creation Tests

| Test ID | Test Case | Expected | Actual | Status | Notes |
|---------|-----------|----------|--------|--------|-------|
| 4.1 | Order → Cheapest Vendor | Allocated to Vendor B | | ⬜ PASS ⬜ FAIL | |
| 4.2 | Order → 2nd Cheapest | Allocated to Vendor A | | ⬜ PASS ⬜ FAIL | |
| 4.3 | Order - No Stock | 400 Bad Request | | ⬜ PASS ⬜ FAIL | |
| 4.4 | Order - Invalid Product | 404 Not Found | | ⬜ PASS ⬜ FAIL | |
| 4.5 | Order - Zero Quantity | 400 Bad Request | | ⬜ PASS ⬜ FAIL | |
| 4.6 | Order - Negative Quantity | 400 Bad Request | | ⬜ PASS ⬜ FAIL | |

### Order Retrieval Tests

| Test ID | Test Case | Expected | Actual | Status | Notes |
|---------|-----------|----------|--------|--------|-------|
| 5.1 | Get Vendor's Orders | 200 OK + orders list | | ⬜ PASS ⬜ FAIL | |
| 5.2 | Get Specific Order | 200 OK + order details | | ⬜ PASS ⬜ FAIL | |
| 5.3 | View Other Vendor's Order | 403 Forbidden | | ⬜ PASS ⬜ FAIL | |
| 5.4 | Get Non-existent Order | 404 Not Found | | ⬜ PASS ⬜ FAIL | |

### Security Tests

| Test ID | Test Case | Expected | Actual | Status | Notes |
|---------|-----------|----------|--------|--------|-------|
| 6.1 | Access Without Token | 401 Unauthorized | | ⬜ PASS ⬜ FAIL | |
| 6.2 | Access With Invalid Token | 401 Unauthorized | | ⬜ PASS ⬜ FAIL | |

### End-to-End Tests

| Test ID | Test Case | Expected | Actual | Status | Notes |
|---------|-----------|----------|--------|--------|-------|
| 7.1 | Complete Business Flow | All steps succeed | | ⬜ PASS ⬜ FAIL | |

---

## Bugs Found

| Bug ID | Severity | Component | Description | Steps to Reproduce | Expected | Actual |
|--------|----------|-----------|-------------|-------------------|----------|--------|
| BUG-001 | ⬜ Critical ⬜ Major ⬜ Minor | | | | | |
| BUG-002 | ⬜ Critical ⬜ Major ⬜ Minor | | | | | |
| BUG-003 | ⬜ Critical ⬜ Major ⬜ Minor | | | | | |

---

## Performance Observations

| Endpoint | Average Response Time | Notes |
|----------|----------------------|-------|
| POST /api/auth/login | _____ms | |
| GET /api/vendors/{id}/products | _____ms | |
| PUT /api/vendors/{id}/products/{id}/price | _____ms | |
| POST /api/orders | _____ms | |
| GET /api/orders | _____ms | |

**Overall Performance**: ⬜ Excellent (<100ms) ⬜ Good (<500ms) ⬜ Acceptable (<1s) ⬜ Slow (>1s)

---

## Usability Observations

**Swagger UI Experience**:
- ⬜ Easy to use
- ⬜ Clear documentation
- ⬜ Examples helpful
- ⬜ Confusing in places (specify): ___________________________

**API Design**:
- ⬜ Intuitive endpoints
- ⬜ Clear error messages
- ⬜ Consistent response formats
- ⬜ Issues (specify): ___________________________

---

## Test Coverage Summary

| Category | Total Tests | Passed | Failed | Coverage |
|----------|-------------|--------|--------|----------|
| Authentication | 5 | ___ | ___ | ___% |
| Vendor Management | 7 | ___ | ___ | ___% |
| Order Creation | 6 | ___ | ___ | ___% |
| Order Retrieval | 4 | ___ | ___ | ___% |
| Security | 2 | ___ | ___ | ___% |
| End-to-End | 1 | ___ | ___ | ___% |
| **TOTAL** | **25** | ___ | ___ | ___% |

---

## Final Assessment

**Production Readiness**: ⬜ Ready ⬜ Ready with Minor Issues ⬜ Not Ready

**Confidence Level**: ⬜ High ⬜ Medium ⬜ Low

**Blockers for Production**:
1. ___________________________
2. ___________________________
3. ___________________________

**Recommended Actions Before Production**:
1. ___________________________
2. ___________________________
3. ___________________________

---

## Additional Notes

_______________________________________________________________________________
_______________________________________________________________________________
_______________________________________________________________________________
_______________________________________________________________________________
_______________________________________________________________________________

---

## Sign-off

**Tested By**: ___________________________
**Signature**: ___________________________
**Date**: ___________________________

**Approved for Production**: ⬜ Yes ⬜ No ⬜ With Conditions

**Approver**: ___________________________
**Signature**: ___________________________
**Date**: ___________________________
