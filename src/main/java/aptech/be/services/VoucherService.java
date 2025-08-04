package aptech.be.services;

import aptech.be.dto.*;
import aptech.be.models.Voucher;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherService {
    
    // CRUD Operations
    VoucherDTO createVoucher(VoucherCreateRequest request);
    VoucherDTO updateVoucher(Long id, VoucherCreateRequest request);
    VoucherDTO getVoucherById(Long id);
    VoucherDTO getVoucherByCode(String code);
    List<VoucherDTO> getAllVouchers();
    void deleteVoucher(Long id);
    
    // Voucher Application
    VoucherApplyResponse applyVoucher(VoucherApplyRequest request);
    VoucherApplyResponse applyVoucherToOrder(String voucherCode, Long customerId, Long orderId);
    
    // Customer Voucher Management
    List<VoucherDTO> getAvailableVouchersForCustomer(Long customerId);
    List<VoucherDTO> getCustomerVouchers(Long customerId);
    
    // Campaign Management
    List<VoucherDTO> getVouchersByCampaign(String campaignType);
    void sendVouchersToCustomers(Long voucherId, List<Long> customerIds);
    void sendBirthdayVouchers();
    void sendBlackFridayVouchers();
    void sendVipVouchers();
    void sendNewsletterVouchers();
    
    // Statistics
    List<VoucherStatisticsDTO> getVoucherStatistics(LocalDateTime startDate, LocalDateTime endDate);
    List<CampaignStatisticsDTO> getCampaignStatistics(LocalDateTime startDate, LocalDateTime endDate);
    VoucherStatisticsDTO getVoucherStatisticsById(Long voucherId, LocalDateTime startDate, LocalDateTime endDate);
    List<VoucherStatisticsDTO> getVipVoucherStatistics();
    List<VoucherStatisticsDTO> getNewsletterVoucherStatistics();
    
    // Utility Methods
    boolean isValidVoucher(String voucherCode);
    boolean canCustomerUseVoucher(String voucherCode, Long customerId);
    void updateVoucherStatus();
    List<VoucherDTO> searchVouchers(String keyword);
} 