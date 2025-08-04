# VOUCHER SYSTEM - LOCALHOST TEST URLs

## TÍNH NĂNG ĐÃ IMPLEMENT ĐẦY ĐỦ

### 🎯 **1. Phân nhóm khách hàng**
- **VIP**: Voucher chỉ cho khách VIP (level VIP/PREMIUM hoặc ≥1000 điểm)
- **NEWSLETTER_SUBSCRIBER**: Voucher cho khách đăng ký newsletter
- **ALL**: Voucher cho tất cả khách hàng

### 🎉 **2. Campaign (Sự kiện)**
- **BIRTHDAY**: Voucher sinh nhật (tự động gửi hàng ngày lúc 9h sáng)
- **BLACK_FRIDAY**: Voucher Black Friday (tự động gửi ngày 25/11)
- **NEW_YEAR**: Voucher năm mới (tự động gửi ngày 1/1)
- **NEWSLETTER**: Voucher newsletter (tự động gửi thứ 2 hàng tuần)
- **CUSTOM**: Voucher tùy chỉnh

### 📧 **3. Email/SMS tự động**
- **Email Service**: Gửi email thông báo voucher
- **Birthday Email**: Template chúc mừng sinh nhật đẹp
- **Scheduled Jobs**: Cron jobs tự động gửi voucher
- **VIP Monthly**: Gửi voucher VIP hàng tháng
- **Newsletter Weekly**: Gửi voucher newsletter hàng tuần

### 📊 **4. Thống kê**
- **Voucher Usage**: Thống kê voucher được dùng nhiều nhất
- **Revenue Increase**: Báo cáo doanh số tăng thêm
- **Campaign Statistics**: Thống kê theo campaign
- **Conversion Rate**: Tỷ lệ chuyển đổi voucher

## 📋 GET ENDPOINTS (Web Browser)

| Tên | Link | Chức năng |
|-----|------|-----------|
| Test All APIs | `http://localhost:8080/api/vouchers/test-all` | Xem hướng dẫn tất cả API |
| Campaign Types | `http://localhost:8080/api/vouchers/campaign/test` | Xem các loại campaign hợp lệ |
| All Vouchers | `http://localhost:8080/api/vouchers` | Lấy tất cả voucher |
| Birthday Vouchers | `http://localhost:8080/api/vouchers/campaign/BIRTHDAY` | Lấy voucher sinh nhật |
| Black Friday Vouchers | `http://localhost:8080/api/vouchers/campaign/BLACK_FRIDAY` | Lấy voucher Black Friday |
| New Year Vouchers | `http://localhost:8080/api/vouchers/campaign/NEW_YEAR` | Lấy voucher năm mới |
| Newsletter Vouchers | `http://localhost:8080/api/vouchers/campaign/NEWSLETTER` | Lấy voucher newsletter |
| Custom Vouchers | `http://localhost:8080/api/vouchers/campaign/CUSTOM` | Lấy voucher tùy chỉnh |
| Validate VIP10 | `http://localhost:8080/api/vouchers/validate/VIP10` | Kiểm tra voucher VIP10 |
| Search Vouchers | `http://localhost:8080/api/vouchers/search?keyword=VIP` | Tìm kiếm voucher |

## 📤 POST ENDPOINTS (Postman/curl)

| Tên | Link | Method | Chức năng |
|-----|------|--------|-----------|
| Send Birthday | `http://localhost:8080/api/vouchers/campaign/send-birthday` | POST | Gửi voucher sinh nhật |
| Send Black Friday | `http://localhost:8080/api/vouchers/campaign/send-black-friday` | POST | Gửi voucher Black Friday |
| Send to Customers | `http://localhost:8080/api/vouchers/1/send` | POST | Gửi voucher cho khách hàng |

## 📊 STATISTICS ENDPOINTS

| Tên | Link | Chức năng |
|-----|------|-----------|
| Voucher Statistics | `http://localhost:8080/api/vouchers/statistics` | Thống kê voucher được dùng nhiều nhất (1 tháng gần nhất) |
| Campaign Statistics | `http://localhost:8080/api/vouchers/statistics/campaign` | Thống kê doanh số tăng thêm theo campaign (1 tháng gần nhất) |
| Voucher Statistics (Custom) | `http://localhost:8080/api/vouchers/statistics?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59` | Thống kê với thời gian tùy chỉnh |
| Campaign Statistics (Custom) | `http://localhost:8080/api/vouchers/statistics/campaign?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59` | Thống kê campaign với thời gian tùy chỉnh |

## ❌ URLs SAI (sẽ báo lỗi)

| Tên | Link | Lý do lỗi |
|-----|------|-----------|
| Wrong Birthday | `http://localhost:8080/api/vouchers/campaign/send-birthday` | Dùng GET thay vì POST |
| Wrong Black Friday | `http://localhost:8080/api/vouchers/campaign/send-black-friday` | Dùng GET thay vì POST | 