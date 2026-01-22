-- Seed Vendors (passwords are BCrypt hashed: "password123")
-- BCrypt hash for "password123" generated with BCryptPasswordEncoder
INSERT INTO vendors (username, password, name, created_at) VALUES
('vendor-a', '$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq', 'Vendor Alpha', CURRENT_TIMESTAMP),
('vendor-b', '$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq', 'Vendor Beta', CURRENT_TIMESTAMP),
('vendor-c', '$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq', 'Vendor Charlie', CURRENT_TIMESTAMP);

-- Seed Products
INSERT INTO products (product_code, name, description, created_at) VALUES
('widget-001', 'Widget', 'Standard widget for demo purposes', CURRENT_TIMESTAMP);

-- Seed VendorProducts (varied prices/stock for demo scenarios)
-- Vendor A: $50, 100 stock (expensive but has stock)
-- Vendor B: $45, 50 stock (mid-price with stock - should win allocations)
-- Vendor C: $40, 0 stock (cheapest but no stock - will be skipped)
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(1, 1, 50.00, 100, CURRENT_TIMESTAMP),
(2, 1, 45.00, 50, CURRENT_TIMESTAMP),
(3, 1, 40.00, 0, CURRENT_TIMESTAMP);
