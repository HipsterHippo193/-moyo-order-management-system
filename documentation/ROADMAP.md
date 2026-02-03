# Fuchs OMS — Feature Roadmap

> Reference document for tracking feature expansion.
> Update the status checkboxes as work is completed.

---

## Phase 1: Core CRUD Completeness

Expand the system from a vendor-only view into a full management platform with proper entity management.

### 1.1 Vendor Registration
- [x] **Backend**: `POST /api/auth/register` endpoint (username, password, vendor name)
- [x] **Backend**: Validation — unique username, password strength rules
- [x] **Frontend**: Registration page with form, link from login page
- [x] **Frontend**: Success message + redirect to login after registration
- [x] **Seed data**: Keep existing vendors, but registration is now the primary way to add vendors

### 1.2 Product Categories
- [x] **Backend**: `Category` entity (id, name, description, createdAt)
- [x] **Backend**: `CategoryRepository` + `CategoryService`
- [x] **Backend**: Add `categoryId` foreign key to `Product` entity
- [x] **Backend**: `GET /api/categories` — list all categories
- [x] **Backend**: Seed data — add 4-5 default categories and assign existing products
- [x] **Frontend**: Category filter dropdown on product/order pages

### 1.3 Product Catalog Management
- [x] **Backend**: `POST /api/products` — create a new product (name, code, description, categoryId)
- [x] **Backend**: `PUT /api/products/{id}` — edit product details
- [x] **Backend**: `DELETE /api/products/{id}` — remove product (only if no active orders)
- [x] **Backend**: `GET /api/products` — list all products (public catalog)
- [x] **Frontend**: Product catalog page — browse all available products
- [x] **Frontend**: Add/edit product form (admin or vendor depending on role decisions in Phase 3)

### 1.4 Vendor-Product Enrollment
- [x] **Backend**: `POST /api/vendors/{vendorId}/products` — vendor adds a product they supply (set price + stock)
- [x] **Backend**: `DELETE /api/vendors/{vendorId}/products/{productId}` — vendor stops supplying a product
- [x] **Frontend**: "Browse catalog & enroll" flow — vendor picks products to supply, sets price/stock
- [x] **Frontend**: Dashboard shows only enrolled products (already works, just needs enrollment entry point)

### 1.5 Seed Data Update
- [x] Add 3-5 categories (Electronics, Clothing, Home & Kitchen, Office Supplies, etc.)
- [x] Add 5-10 products spread across categories
- [x] Assign existing vendor inventory to match new product set

---

## Phase 2: Order Lifecycle

Move orders from a single "ALLOCATED" state to a full lifecycle with status tracking.

### 2.1 Order Status Workflow
- [ ] **Backend**: Expand `Order.status` to enum: `PENDING`, `ALLOCATED`, `SHIPPED`, `DELIVERED`, `CANCELLED`
- [ ] **Backend**: `PUT /api/orders/{id}/status` — advance or update order status
- [ ] **Backend**: Validation — enforce valid transitions (e.g. can't go from DELIVERED back to PENDING)
- [ ] **Backend**: Auto-set `ALLOCATED` after allocation succeeds (existing behavior)
- [ ] **Frontend**: Status badge with color coding per status
- [ ] **Frontend**: Action buttons on order rows to advance status (e.g. "Mark Shipped")

### 2.2 Order Cancellation
- [ ] **Backend**: `POST /api/orders/{id}/cancel` — cancel order
- [ ] **Backend**: Restore stock to vendor's inventory on cancellation
- [ ] **Backend**: Only allow cancellation for PENDING or ALLOCATED orders
- [ ] **Frontend**: Cancel button on eligible orders with confirmation prompt

### 2.3 Order Detail View
- [ ] **Backend**: Include status history / timestamps in order response
- [ ] **Frontend**: Order detail page (click on an order row to see full details)
- [ ] **Frontend**: Status timeline showing when each transition happened

### 2.4 Multi-Product Orders (stretch)
- [ ] **Backend**: `OrderItem` entity (orderId, productId, quantity, allocatedVendorId, unitPrice)
- [ ] **Backend**: Refactor order creation to accept list of items
- [ ] **Backend**: Allocate each item independently
- [ ] **Frontend**: Cart-style order creation (add multiple products before submitting)

---

## Phase 3: Roles & Admin Dashboard

Introduce role separation so admins can manage the entire system while vendors manage their own scope.

### 3.1 Role System
- [ ] **Backend**: Add `role` field to Vendor entity (VENDOR, ADMIN)
- [ ] **Backend**: Update security config — admin-only endpoints
- [ ] **Backend**: Seed an admin account (admin/admin123)
- [ ] **Frontend**: Route guards based on role
- [ ] **Frontend**: Conditional nav links (admin sees more pages)

### 3.2 Admin Dashboard
- [ ] **Frontend**: Admin overview page — total orders, total vendors, total products
- [ ] **Backend**: `GET /api/admin/stats` — aggregated counts and summaries
- [ ] **Frontend**: Recent orders list (all vendors)
- [ ] **Frontend**: Low-stock alerts — products where any vendor's stock is below threshold

### 3.3 Admin: Vendor Management
- [ ] **Backend**: `GET /api/admin/vendors` — list all vendors
- [ ] **Backend**: `PUT /api/admin/vendors/{id}` — edit vendor, toggle active/inactive
- [ ] **Frontend**: Vendor list page with status indicators

### 3.4 Admin: Product & Category Management
- [ ] **Frontend**: Admin can create/edit/delete categories
- [ ] **Frontend**: Admin can create/edit/delete products
- [ ] **Backend**: Restrict product/category mutation endpoints to admin role

---

## Phase 4: Polish & UX

Improve the user experience across all pages.

### 4.1 Search & Filtering
- [ ] **Frontend**: Search bar on products page (filter by name/code)
- [ ] **Frontend**: Order filtering — by status, by date range
- [ ] **Frontend**: Category filter on product catalog
- [ ] **Backend**: Query parameters on list endpoints for filtering

### 4.2 Pagination
- [ ] **Backend**: Add `Pageable` support to order and product list endpoints
- [ ] **Frontend**: Page controls (prev/next, page numbers) on order and product tables

### 4.3 Confirmation Dialogs
- [ ] **Frontend**: Confirm before cancelling an order
- [ ] **Frontend**: Confirm before deleting a product
- [ ] **Frontend**: Confirm before removing vendor-product enrollment

### 4.4 Loading & Feedback
- [ ] **Frontend**: Spinner/disabled state on buttons during API calls
- [ ] **Frontend**: Toast notifications for success/error instead of inline messages
- [ ] **Frontend**: Empty states — friendly message when no orders/products exist

### 4.5 Responsive Design
- [ ] **Frontend**: Mobile-friendly tables (horizontal scroll or card layout on small screens)
- [ ] **Frontend**: Hamburger menu for navbar on mobile

---

## Current State (Completed)

For reference — what already exists:

- [x] JWT authentication (vendor login)
- [x] Order creation with auto-allocation (lowest price + stock)
- [x] Vendor product management (view, update price, update stock)
- [x] Dashboard with inline editing
- [x] Order history page
- [x] Dark/light theme toggle with persistence
- [x] Hash-based SPA routing
- [x] 41 backend tests (unit + integration)
- [x] H2 in-memory database with seed data
- [x] Swagger UI documentation

---

## Notes

- **Database**: H2 in-memory — intentionally kept for easy clone-and-run. Seed data in `data.sql`.
- **No external services**: Everything runs locally with `mvn spring-boot:run`.
- **Tech stack**: Spring Boot 3 + Java 17 backend, vanilla HTML/CSS/JS frontend.
