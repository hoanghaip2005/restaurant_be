### 1. GET ENDPOINTS (truy vấn dữ liệu)
| Tên | Link | Chức năng |
|-----|------|-----------|
| Test All APIs | http://localhost:8080/api/vouchers/test-all | Xem hướng dẫn tất cả API |
| Campaign Types | http://localhost:8080/api/vouchers/campaign/test | Xem các loại campaign hợp lệ |
| All Vouchers | http://localhost:8080/api/vouchers | Lấy tất cả voucher |
| Birthday Vouchers | http://localhost:8080/api/vouchers/campaign/BIRTHDAY | Lấy voucher sinh nhật |
| Black Friday Vouchers | http://localhost:8080/api/vouchers/campaign/BLACK_FRIDAY | Lấy voucher Black Friday |
| New Year Vouchers | http://localhost:8080/api/vouchers/campaign/NEW_YEAR | Lấy voucher năm mới |
| Newsletter Vouchers | http://localhost:8080/api/vouchers/campaign/NEWSLETTER | Lấy voucher newsletter |
| Custom Vouchers | http://localhost:8080/api/vouchers/campaign/CUSTOM | Lấy voucher tùy chỉnh |
| Validate VIP10 | http://localhost:8080/api/vouchers/validate/VIP10 | Kiểm tra voucher VIP10 |
| Search Vouchers | http://localhost:8080/api/vouchers/search?keyword=VIP | Tìm kiếm voucher |
| VIP Customers | http://localhost:8080/api/customers/vip | Lấy danh sách khách hàng VIP |
| Newsletter Customers | http://localhost:8080/api/customers/newsletter | Lấy danh sách khách đăng ký newsletter |

### 2. POST ENDPOINTS (gửi voucher/email)
| Tên | Link | Method | Chức năng |
|-----|------|--------|-----------|
| Send Birthday | http://localhost:8080/api/vouchers/campaign/send-birthday | POST | Gửi voucher sinh nhật tự động qua email |
| Send Black Friday | http://localhost:8080/api/vouchers/campaign/send-black-friday | POST | Gửi voucher Black Friday tự động qua email |
| Send to Customers | http://localhost:8080/api/vouchers/{voucherId}/send | POST | Gửi voucher cho danh sách khách hàng (body: mảng customerIds) |
| Send Newsletter | http://localhost:8080/api/vouchers/campaign/send-newsletter | POST | Gửi voucher cho nhóm khách đăng ký newsletter |
| Send VIP | http://localhost:8080/api/vouchers/campaign/send-vip | POST | Gửi voucher cho nhóm khách VIP |

### 3. STATISTICS ENDPOINTS (thống kê)
| Tên | Link | Chức năng |
|-----|------|-----------|
| Voucher Statistics | http://localhost:8080/api/vouchers/statistics | Thống kê voucher được dùng nhiều nhất (1 tháng gần nhất) |
| Campaign Statistics | http://localhost:8080/api/vouchers/statistics/campaign | Thống kê doanh số tăng thêm theo campaign (1 tháng gần nhất) |
| Voucher Statistics (Custom) | http://localhost:8080/api/vouchers/statistics?startDate=YYYY-MM-DDTHH:mm:ss&endDate=YYYY-MM-DDTHH:mm:ss | Thống kê với thời gian tùy chỉnh |
| Campaign Statistics (Custom) | http://localhost:8080/api/vouchers/statistics/campaign?startDate=YYYY-MM-DDTHH:mm:ss&endDate=YYYY-MM-DDTHH:mm:ss | Thống kê campaign với thời gian tùy chỉnh |
| VIP Usage Statistics | http://localhost:8080/api/vouchers/statistics/vip | Thống kê voucher được dùng bởi nhóm VIP |
| Newsletter Usage Statistics | http://localhost:8080/api/vouchers/statistics/newsletter | Thống kê voucher được dùng bởi nhóm newsletter |

### 4. Lưu ý test POST gửi voucher cho khách hàng
- Endpoint: `POST /api/vouchers/{voucherId}/send`
- Body: mảng id khách hàng, ví dụ: `[1,2,3]`
- Header: `Content-Type: application/json`

### 5. Hướng dẫn test nhanh bằng PowerShell
```powershell
# 1. Xem hướng dẫn và kiểm tra API
# Xem hướng dẫn tất cả API
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/test-all"

# Xem danh sách campaign types hợp lệ
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/campaign/test"

# 2. Quản lý voucher
# Lấy danh sách tất cả voucher
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers"

# Tạo voucher mới
$newVoucher = @{
    code = 'TEST20'
    name = 'Test Voucher 20%'
    description = 'Test voucher for sending'
    type = 'PERCENTAGE'
    discountValue = 25.0
    minimumOrderAmount = 100000.0
    maximumDiscount = 50000.0
    maxUsage = 100
    startDate = '2025-08-04T00:00:00'
    endDate = '2025-09-04T23:59:59'
    targetGroup = 'ALL'
    campaignType = 'CUSTOM'
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers" -Method POST -ContentType "application/json" -Body $newVoucher

# 3. Gửi voucher
# Gửi voucher cho danh sách khách hàng cụ thể
$customerIds = '[1,2,3]'
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/3/send" -Method POST -ContentType "application/json" -Body $customerIds

# Gửi voucher sinh nhật tự động qua email
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/campaign/send-birthday" -Method POST

# Gửi voucher Black Friday tự động qua email
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/campaign/send-black-friday" -Method POST

# Gửi voucher cho nhóm newsletter
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/campaign/send-newsletter" -Method POST

# Gửi voucher cho nhóm VIP
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/campaign/send-vip" -Method POST

# 4. Thống kê
# Thống kê voucher được dùng nhiều nhất (1 tháng gần nhất)
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/statistics"

# Thống kê voucher với thời gian tùy chỉnh
$startDate = "2025-07-01T00:00:00Z"
$endDate = "2025-08-01T00:00:00Z"
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/statistics?startDate=$startDate&endDate=$endDate"

# Thống kê campaign với thời gian tùy chỉnh
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/statistics/campaign?startDate=$startDate&endDate=$endDate"

# 5. Thống kê theo nhóm khách hàng
# Thống kê voucher nhóm VIP
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/statistics/vip"

# Thống kê voucher nhóm newsletter
Invoke-WebRequest -Uri "http://localhost:8080/api/vouchers/statistics/newsletter"
```
