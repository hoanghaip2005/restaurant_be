package aptech.be.controllers;

import aptech.be.dto.*;
import aptech.be.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "http://localhost:3000")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    // CRUD Operations
    @PostMapping
    public ResponseEntity<?> createVoucher(@RequestBody VoucherCreateRequest request) {
        try {
            VoucherDTO voucher = voucherService.createVoucher(request);
            return ResponseEntity.ok(voucher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating voucher: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> updateVoucher(@PathVariable Long id, @RequestBody VoucherCreateRequest request) {
        try {
            VoucherDTO voucher = voucherService.updateVoucher(id, request);
            return ResponseEntity.ok(voucher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<VoucherDTO>> getAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    // Voucher Application - Đặt trước /{id} để tránh xung đột
    @PostMapping("/apply")
    public ResponseEntity<VoucherApplyResponse> applyVoucher(@RequestBody VoucherApplyRequest request) {
        try {
            VoucherApplyResponse response = voucherService.applyVoucher(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            VoucherApplyResponse errorResponse = new VoucherApplyResponse(
                false, e.getMessage(), request.getVoucherCode(), "", 
                request.getOrderAmount(), 0.0, request.getOrderAmount(), "", 0.0);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getVoucherByCode(@PathVariable String code) {
        try {
            VoucherDTO voucher = voucherService.getVoucherByCode(code);
            return ResponseEntity.ok(voucher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting voucher by code: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable Long id) {
        try {
            VoucherDTO voucher = voucherService.getVoucherById(id);
            return ResponseEntity.ok(voucher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting voucher by ID: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/apply-to-order")
    public ResponseEntity<?> applyVoucherToOrder(
            @RequestParam String voucherCode,
            @RequestParam Long customerId,
            @RequestParam Long orderId) {
        try {
            VoucherApplyResponse response = voucherService.applyVoucherToOrder(voucherCode, customerId, orderId);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            VoucherApplyResponse errorResponse = new VoucherApplyResponse(
                false, e.getMessage(), voucherCode, "", 0.0, 0.0, 0.0, "", 0.0);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Customer Voucher Management
    @GetMapping("/customer/{customerId}/available")
    public ResponseEntity<?> getAvailableVouchersForCustomer(@PathVariable Long customerId) {
        try {
            List<VoucherDTO> vouchers = voucherService.getAvailableVouchersForCustomer(customerId);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting available vouchers: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<VoucherDTO>> getCustomerVouchers(@PathVariable Long customerId) {
        try {
            List<VoucherDTO> vouchers = voucherService.getCustomerVouchers(customerId);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Campaign Management
    @GetMapping("/campaign/{campaignType}")
    public ResponseEntity<?> getVouchersByCampaign(@PathVariable String campaignType) {
        try {
            // Kiểm tra nếu là URL sai
            if (campaignType.equals("send-birthday") || campaignType.equals("send-black-friday")) {
                return ResponseEntity.badRequest().body(
                    "❌ WRONG URL! Use POST method instead:\n" +
                    "✅ POST /api/vouchers/campaign/send-birthday\n" +
                    "✅ POST /api/vouchers/campaign/send-black-friday\n\n" +
                    "For GET requests, use valid campaign types:\n" +
                    "✅ GET /api/vouchers/campaign/BIRTHDAY\n" +
                    "✅ GET /api/vouchers/campaign/BLACK_FRIDAY\n" +
                    "✅ GET /api/vouchers/campaign/NEW_YEAR\n" +
                    "✅ GET /api/vouchers/campaign/NEWSLETTER\n" +
                    "✅ GET /api/vouchers/campaign/CUSTOM"
                );
            }
            
            List<VoucherDTO> vouchers = voucherService.getVouchersByCampaign(campaignType);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting vouchers by campaign: " + e.getMessage());
        }
    }

    // Test endpoint để kiểm tra campaign types
    @GetMapping("/campaign/test")
    public ResponseEntity<?> testCampaignTypes() {
        return ResponseEntity.ok(
            "🎯 VALID CAMPAIGN TYPES:\n" +
            "• BIRTHDAY\n" +
            "• BLACK_FRIDAY\n" +
            "• NEW_YEAR\n" +
            "• NEWSLETTER\n" +
            "• CUSTOM\n\n" +
            "📝 HOW TO USE:\n" +
            "GET /api/vouchers/campaign/BIRTHDAY - Lấy voucher sinh nhật\n" +
            "POST /api/vouchers/campaign/send-birthday - Gửi voucher sinh nhật\n" +
            "GET /api/vouchers/campaign/BLACK_FRIDAY - Lấy voucher Black Friday\n" +
            "POST /api/vouchers/campaign/send-black-friday - Gửi voucher Black Friday"
        );
    }

    // Endpoint để test tất cả API
    @GetMapping("/test-all")
    public ResponseEntity<?> testAllEndpoints() {
        return ResponseEntity.ok(
            "🚀 VOUCHER SYSTEM API TEST\n\n" +
            "📋 GET ENDPOINTS (Web Browser):\n" +
            "• /api/vouchers - Lấy tất cả voucher\n" +
            "• /api/vouchers/campaign/BIRTHDAY - Lấy voucher sinh nhật\n" +
            "• /api/vouchers/campaign/BLACK_FRIDAY - Lấy voucher Black Friday\n" +
            "• /api/vouchers/validate/VIP10 - Kiểm tra voucher\n" +
            "• /api/vouchers/campaign/test - Xem campaign types\n" +
            "• /api/vouchers/statistics - Thống kê voucher (1 tháng gần nhất)\n" +
            "• /api/vouchers/statistics/campaign - Thống kê campaign (1 tháng gần nhất)\n\n" +
            "📤 POST ENDPOINTS (Postman/curl):\n" +
            "• POST /api/vouchers/campaign/send-birthday - Gửi voucher sinh nhật\n" +
            "• POST /api/vouchers/campaign/send-black-friday - Gửi voucher Black Friday\n" +
            "• POST /api/vouchers/1/send - Gửi voucher cho khách hàng\n\n" +
            "📊 STATISTICS WITH PARAMETERS:\n" +
            "• /api/vouchers/statistics?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59\n" +
            "• /api/vouchers/statistics/campaign?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59\n\n" +
            "⚠️ LƯU Ý: Statistics endpoints giờ có thể dùng không cần parameters!"
        );
    }

    @PostMapping("/{voucherId}/send")
    public ResponseEntity<String> sendVouchersToCustomers(
            @PathVariable Long voucherId,
            @RequestBody List<Long> customerIds) {
        try {
            voucherService.sendVouchersToCustomers(voucherId, customerIds);
            return ResponseEntity.ok("Vouchers sent to " + customerIds.size() + " customers successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send vouchers: " + e.getMessage());
        }
    }

    @PostMapping("/campaign/send-birthday")
    public ResponseEntity<String> sendBirthdayVouchers() {
        try {
            voucherService.sendBirthdayVouchers();
            return ResponseEntity.ok("Birthday vouchers sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send birthday vouchers: " + e.getMessage());
        }
    }

    @PostMapping("/campaign/send-black-friday")
    public ResponseEntity<String> sendBlackFridayVouchers() {
        try {
            voucherService.sendBlackFridayVouchers();
            return ResponseEntity.ok("Black Friday vouchers sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send Black Friday vouchers: " + e.getMessage());
        }
    }

    // Statistics
    @GetMapping("/statistics")
    public ResponseEntity<List<VoucherStatisticsDTO>> getVoucherStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // Nếu không có parameters, dùng thời gian mặc định
            if (startDate == null) {
                startDate = LocalDateTime.now().minusMonths(1); // 1 tháng trước
            }
            if (endDate == null) {
                endDate = LocalDateTime.now(); // Hiện tại
            }
            
            List<VoucherStatisticsDTO> statistics = voucherService.getVoucherStatistics(startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/statistics/campaign")
    public ResponseEntity<List<CampaignStatisticsDTO>> getCampaignStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            // Nếu không có parameters, dùng thời gian mặc định
            if (startDate == null) {
                startDate = LocalDateTime.now().minusMonths(1); // 1 tháng trước
            }
            if (endDate == null) {
                endDate = LocalDateTime.now(); // Hiện tại
            }
            
            List<CampaignStatisticsDTO> statistics = voucherService.getCampaignStatistics(startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{voucherId}/statistics")
    public ResponseEntity<?> getVoucherStatisticsById(
            @PathVariable Long voucherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            VoucherStatisticsDTO statistics = voucherService.getVoucherStatisticsById(voucherId, startDate, endDate);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting voucher statistics: " + e.getMessage());
        }
    }

    // Utility Methods
    @GetMapping("/validate/{voucherCode}")
    public ResponseEntity<Boolean> isValidVoucher(@PathVariable String voucherCode) {
        try {
            boolean isValid = voucherService.isValidVoucher(voucherCode);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/can-use")
    public ResponseEntity<Boolean> canCustomerUseVoucher(
            @RequestParam String voucherCode,
            @RequestParam Long customerId) {
        try {
            boolean canUse = voucherService.canCustomerUseVoucher(voucherCode, customerId);
            return ResponseEntity.ok(canUse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<Void> updateVoucherStatus() {
        try {
            voucherService.updateVoucherStatus();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<VoucherDTO>> searchVouchers(@RequestParam String keyword) {
        try {
            List<VoucherDTO> vouchers = voucherService.searchVouchers(keyword);
            return ResponseEntity.ok(vouchers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 