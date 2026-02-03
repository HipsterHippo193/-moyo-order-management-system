-- Seed Vendors (passwords are BCrypt hashed: "password123")
-- BCrypt hash for "password123" generated with BCryptPasswordEncoder
INSERT INTO vendors (username, password, name, created_at) VALUES
('vendor-a', '$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq', 'Vendor Alpha', CURRENT_TIMESTAMP),
('vendor-b', '$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq', 'Vendor Beta', CURRENT_TIMESTAMP),
('vendor-c', '$2a$10$4BBZZlV/ifLF/icVxYVJg..QRJe1Wm8R2MwGY7CbOKnwUzLKHMKgq', 'Vendor Charlie', CURRENT_TIMESTAMP);

-- Seed Categories
INSERT INTO categories (name, description, created_at) VALUES
('Electronics', 'Electronic devices, gadgets, and accessories', CURRENT_TIMESTAMP),
('Clothing', 'Apparel, footwear, and fashion accessories', CURRENT_TIMESTAMP),
('Home & Kitchen', 'Home decor, kitchenware, and appliances', CURRENT_TIMESTAMP),
('Office Supplies', 'Stationery, office equipment, and supplies', CURRENT_TIMESTAMP),
('Sports & Outdoors', 'Sports equipment and outdoor gear', CURRENT_TIMESTAMP);

-- Seed Products
INSERT INTO products (product_code, name, description, category_id, created_at) VALUES
('ELEC-001', 'Wireless Bluetooth Headphones', 'High-quality over-ear headphones with noise cancellation', 1, CURRENT_TIMESTAMP),
('ELEC-002', 'USB-C Charging Cable', 'Fast charging cable, 6ft braided nylon', 1, CURRENT_TIMESTAMP),
('ELEC-003', 'Portable Power Bank', '10000mAh portable charger with dual USB ports', 1, CURRENT_TIMESTAMP),
('CLTH-001', 'Cotton T-Shirt', 'Premium cotton crew neck t-shirt', 2, CURRENT_TIMESTAMP),
('CLTH-002', 'Running Shoes', 'Lightweight breathable running shoes', 2, CURRENT_TIMESTAMP),
('HOME-001', 'Stainless Steel Water Bottle', '32oz insulated water bottle', 3, CURRENT_TIMESTAMP),
('HOME-002', 'Ceramic Coffee Mug', 'Large 16oz ceramic mug with handle', 3, CURRENT_TIMESTAMP),
('OFFC-001', 'Notebook Set', 'Pack of 3 spiral-bound notebooks, 100 pages each', 4, CURRENT_TIMESTAMP),
('OFFC-002', 'Ergonomic Mouse Pad', 'Gel wrist rest mouse pad', 4, CURRENT_TIMESTAMP),
('SPRT-001', 'Yoga Mat', 'Non-slip exercise yoga mat, 6mm thick', 5, CURRENT_TIMESTAMP);

-- Seed VendorProducts (varied prices/stock for demo scenarios)
-- Vendor Alpha specializes in Electronics and Office Supplies
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(1, 1, 89.99, 50, CURRENT_TIMESTAMP),   -- Headphones
(1, 2, 14.99, 200, CURRENT_TIMESTAMP),  -- USB-C Cable
(1, 3, 34.99, 75, CURRENT_TIMESTAMP),   -- Power Bank
(1, 8, 12.99, 150, CURRENT_TIMESTAMP),  -- Notebook Set
(1, 9, 19.99, 80, CURRENT_TIMESTAMP);   -- Mouse Pad

-- Vendor Beta specializes in Clothing and Sports
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(2, 4, 24.99, 100, CURRENT_TIMESTAMP),  -- T-Shirt
(2, 5, 79.99, 40, CURRENT_TIMESTAMP),   -- Running Shoes
(2, 10, 29.99, 60, CURRENT_TIMESTAMP);  -- Yoga Mat

-- Vendor Charlie specializes in Home & Kitchen (competitive pricing)
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(3, 6, 19.99, 90, CURRENT_TIMESTAMP),   -- Water Bottle
(3, 7, 9.99, 120, CURRENT_TIMESTAMP);   -- Coffee Mug

-- Some products available from multiple vendors (for allocation testing)
-- Vendor Beta also sells headphones (cheaper but less stock)
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(2, 1, 84.99, 25, CURRENT_TIMESTAMP);   -- Headphones (cheaper than Alpha)

-- Vendor Charlie also sells USB cables (cheapest but low stock)
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(3, 2, 12.99, 10, CURRENT_TIMESTAMP);   -- USB-C Cable (cheapest)

-- Vendor Alpha also sells water bottles (more expensive)
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(1, 6, 24.99, 30, CURRENT_TIMESTAMP);   -- Water Bottle (more expensive than Charlie)
