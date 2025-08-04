-- SAMPLE DATA FOR VOUCHER SYSTEM
-- Chạy file này để tạo dữ liệu mẫu

-- 1. Tạo khách hàng mẫu
INSERT INTO customers (id, full_name, email, provider, password, role) VALUES
(1, 'Nguyen Van A', 'nguyenvana@email.com', 'local', 'password123', 'CUSTOMER'),
(2, 'Tran Thi B', 'tranthib@email.com', 'local', 'password123', 'CUSTOMER'),
(3, 'Le Van C', 'levanc@email.com', 'local', 'password123', 'CUSTOMER'),
(4, 'Pham Thi D', 'phamthid@email.com', 'local', 'password123', 'CUSTOMER'),
(5, 'Hoang Van E', 'hoangvane@email.com', 'local', 'password123', 'CUSTOMER');

-- 2. Tạo customer details
INSERT INTO customerdetails (id, customer_id, customer_level, point, newsletter_subscribed, phone_number, address, image_url, voucher) VALUES
(1, 1, 'VIP', '1500', 1, '0123456789', 'Ha Noi', 'https://example.com/avatar1.jpg', ''),
(2, 2, 'REGULAR', '500', 1, '0987654321', 'Ho Chi Minh', 'https://example.com/avatar2.jpg', ''),
(3, 3, 'PREMIUM', '2000', 0, '0111222333', 'Da Nang', 'https://example.com/avatar3.jpg', ''),
(4, 4, 'REGULAR', '300', 1, '0444555666', 'Hai Phong', 'https://example.com/avatar4.jpg', ''),
(5, 5, 'VIP', '1200', 1, '0777888999', 'Can Tho', 'https://example.com/avatar5.jpg', '');

-- 3. Tạo vouchers mẫu
INSERT INTO vouchers (id, code, name, description, type, discount_value, minimum_order_amount, maximum_discount, max_usage, current_usage, start_date, end_date, target_group, campaign_type, campaign_name, status, created_at, updated_at) VALUES
-- Birthday vouchers
(1, 'BIRTH10', 'Voucher Sinh Nhật 10%', 'Giảm 10% cho khách hàng sinh nhật', 'PERCENTAGE', 10.0, 100000.0, 50000.0, 100, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'ALL', 'BIRTHDAY', 'Birthday Campaign 2024', 'ACTIVE', NOW(), NOW()),
(2, 'BIRTH20', 'Voucher Sinh Nhật VIP 20%', 'Giảm 20% cho VIP sinh nhật', 'PERCENTAGE', 20.0, 200000.0, 100000.0, 50, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'VIP', 'BIRTHDAY', 'VIP Birthday Campaign 2024', 'ACTIVE', NOW(), NOW()),

-- Black Friday vouchers
(3, 'BLACK30', 'Black Friday 30%', 'Giảm 30% Black Friday', 'PERCENTAGE', 30.0, 300000.0, 150000.0, 200, 0, '2024-11-01 00:00:00', '2024-11-30 23:59:59', 'ALL', 'BLACK_FRIDAY', 'Black Friday 2024', 'ACTIVE', NOW(), NOW()),
(4, 'BLACK50', 'Black Friday VIP 50%', 'Giảm 50% Black Friday cho VIP', 'PERCENTAGE', 50.0, 500000.0, 250000.0, 100, 0, '2024-11-01 00:00:00', '2024-11-30 23:59:59', 'VIP', 'BLACK_FRIDAY', 'VIP Black Friday 2024', 'ACTIVE', NOW(), NOW()),

-- New Year vouchers
(5, 'NEWYEAR25', 'Năm Mới 25%', 'Chào năm mới giảm 25%', 'PERCENTAGE', 25.0, 250000.0, 125000.0, 150, 0, '2024-12-01 00:00:00', '2025-01-31 23:59:59', 'ALL', 'NEW_YEAR', 'New Year 2025', 'ACTIVE', NOW(), NOW()),

-- Newsletter vouchers
(6, 'NEWS15', 'Newsletter 15%', 'Giảm 15% cho subscriber', 'PERCENTAGE', 15.0, 150000.0, 75000.0, 300, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'NEWSLETTER_SUBSCRIBER', 'NEWSLETTER', 'Newsletter Campaign 2024', 'ACTIVE', NOW(), NOW()),

-- Custom vouchers
(7, 'CUSTOM20', 'Voucher Tùy Chỉnh 20%', 'Voucher tùy chỉnh giảm 20%', 'PERCENTAGE', 20.0, 200000.0, 100000.0, 100, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'ALL', 'CUSTOM', 'Custom Campaign 2024', 'ACTIVE', NOW(), NOW()),
(8, 'FIXED50K', 'Giảm Cố Định 50K', 'Giảm cố định 50,000 VND', 'FIXED_AMOUNT', 50000.0, 100000.0, 50000.0, 200, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'ALL', 'CUSTOM', 'Fixed Amount Campaign', 'ACTIVE', NOW(), NOW());

-- 4. Tạo customer vouchers (vouchers đã gửi cho khách hàng)
INSERT INTO customer_vouchers (id, customer_id, voucher_id, status, sent_at, expired_at, used_at, sent_via) VALUES
(1, 1, 1, 'SENT', NOW(), '2024-12-31 23:59:59', NULL, 'EMAIL'),
(2, 1, 2, 'SENT', NOW(), '2024-12-31 23:59:59', NULL, 'EMAIL'),
(3, 2, 1, 'SENT', NOW(), '2024-12-31 23:59:59', NULL, 'EMAIL'),
(4, 3, 2, 'SENT', NOW(), '2024-12-31 23:59:59', NULL, 'EMAIL'),
(5, 4, 6, 'SENT', NOW(), '2024-12-31 23:59:59', NULL, 'EMAIL'),
(6, 5, 2, 'SENT', NOW(), '2024-12-31 23:59:59', NULL, 'EMAIL');

-- 5. Tạo orders mẫu
INSERT INTO orders (id, customer_id, order_number, original_amount, total_price, voucher_discount, applied_voucher_code, status, confirm_status, delivery_status, created_at) VALUES
(1, 1, 'ORD001', 500000.0, 450000.0, 50000.0, 'BIRTH10', 'COMPLETED', 'CONFIRMED', 'DELIVERED', NOW()),
(2, 2, 'ORD002', 300000.0, 270000.0, 30000.0, 'BIRTH10', 'COMPLETED', 'CONFIRMED', 'DELIVERED', NOW()),
(3, 3, 'ORD003', 800000.0, 640000.0, 160000.0, 'BIRTH20', 'COMPLETED', 'CONFIRMED', 'DELIVERED', NOW()),
(4, 4, 'ORD004', 200000.0, 170000.0, 30000.0, 'NEWS15', 'COMPLETED', 'CONFIRMED', 'DELIVERED', NOW()),
(5, 5, 'ORD005', 600000.0, 480000.0, 120000.0, 'BIRTH20', 'COMPLETED', 'CONFIRMED', 'DELIVERED', NOW());

-- 6. Tạo voucher usages (vouchers đã được sử dụng)
INSERT INTO voucher_usages (id, voucher_id, customer_id, order_id, order_amount, discount_amount, final_amount, used_at) VALUES
(1, 1, 1, 1, 500000.0, 50000.0, 450000.0, NOW()),
(2, 1, 2, 2, 300000.0, 30000.0, 270000.0, NOW()),
(3, 2, 3, 3, 800000.0, 160000.0, 640000.0, NOW()),
(4, 6, 4, 4, 200000.0, 30000.0, 170000.0, NOW()),
(5, 2, 5, 5, 600000.0, 120000.0, 480000.0, NOW());

-- 7. Cập nhật current_usage cho vouchers
UPDATE vouchers SET current_usage = 2 WHERE id = 1;
UPDATE vouchers SET current_usage = 2 WHERE id = 2;
UPDATE vouchers SET current_usage = 1 WHERE id = 6;

-- 8. Cập nhật customer vouchers status thành USED
UPDATE customer_vouchers SET status = 'USED', used_at = NOW() WHERE id IN (1, 2, 3, 4, 5);

-- COMMIT để lưu dữ liệu
COMMIT; 