package aptech.be.services;

import aptech.be.dto.*;
import aptech.be.models.*;
import aptech.be.models.enums.*;
import aptech.be.repositories.*;
import aptech.be.models.enums.CustomerVoucherStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherUsageRepository voucherUsageRepository;

    @Autowired
    private CustomerVoucherRepository customerVoucherRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    // CRUD Operations
    @Override
    public VoucherDTO createVoucher(VoucherCreateRequest request) {
        // Kiểm tra code voucher đã tồn tại chưa
        if (voucherRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Voucher code already exists");
        }

        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setName(request.getName());
        voucher.setDescription(request.getDescription());
        voucher.setType(VoucherType.valueOf(request.getType()));
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinimumOrderAmount(request.getMinimumOrderAmount());
        voucher.setMaximumDiscount(request.getMaximumDiscount());
        voucher.setMaxUsage(request.getMaxUsage());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setTargetGroup(CustomerGroup.valueOf(request.getTargetGroup()));
        voucher.setCampaignType(CampaignType.valueOf(request.getCampaignType()));
        voucher.setCampaignName(request.getCampaignName());

        Voucher savedVoucher = voucherRepository.save(voucher);
        return convertToDTO(savedVoucher);
    }

    @Override
    public VoucherDTO updateVoucher(Long id, VoucherCreateRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        voucher.setName(request.getName());
        voucher.setDescription(request.getDescription());
        voucher.setType(VoucherType.valueOf(request.getType()));
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinimumOrderAmount(request.getMinimumOrderAmount());
        voucher.setMaximumDiscount(request.getMaximumDiscount());
        voucher.setMaxUsage(request.getMaxUsage());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setTargetGroup(CustomerGroup.valueOf(request.getTargetGroup()));
        voucher.setCampaignType(CampaignType.valueOf(request.getCampaignType()));
        voucher.setCampaignName(request.getCampaignName());

        Voucher updatedVoucher = voucherRepository.save(voucher);
        return convertToDTO(updatedVoucher);
    }

    @Override
    public VoucherDTO getVoucherById(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        return convertToDTO(voucher);
    }

    @Override
    public VoucherDTO getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        return convertToDTO(voucher);
    }

    @Override
    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucherRepository.delete(voucher);
    }

    // Voucher Application
    @Override
    public VoucherApplyResponse applyVoucher(VoucherApplyRequest request) {
        try {
            // Validate request
            if (request.getVoucherCode() == null || request.getVoucherCode().trim().isEmpty()) {
                throw new RuntimeException("Voucher code is required");
            }
            if (request.getCustomerId() == null) {
                throw new RuntimeException("Customer ID is required");
            }
            if (request.getOrderAmount() == null || request.getOrderAmount() <= 0) {
                throw new RuntimeException("Order amount must be greater than 0");
            }
            
            // Find voucher
            Voucher voucher = voucherRepository.findByCode(request.getVoucherCode())
                    .orElseThrow(() -> new RuntimeException("Voucher not found with code: " + request.getVoucherCode()));
            
            // Find customer
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId()));
            
            // Validate voucher
            if (!voucher.isValid()) {
                throw new RuntimeException("Voucher is not valid (expired, inactive, or fully used)");
            }
            
            // Check if customer can use this voucher
            if (!voucher.canApplyToCustomer(customer)) {
                throw new RuntimeException("Customer is not eligible for this voucher");
            }
            
            // Check minimum order amount
            if (request.getOrderAmount() < voucher.getMinimumOrderAmount()) {
                throw new RuntimeException("Order amount must be at least " + voucher.getMinimumOrderAmount());
            }
            
            // Calculate discount
            double discountAmount = 0.0;
            if (voucher.getType() == VoucherType.PERCENTAGE) {
                discountAmount = request.getOrderAmount() * (voucher.getDiscountValue() / 100.0);
                discountAmount = Math.min(discountAmount, voucher.getMaximumDiscount());
            } else {
                discountAmount = voucher.getDiscountValue();
            }
            
            double finalAmount = request.getOrderAmount() - discountAmount;
            
            return new VoucherApplyResponse(true, "Voucher applied successfully", 
                request.getVoucherCode(), voucher.getName(), request.getOrderAmount(), 
                discountAmount, finalAmount, voucher.getType().toString(), 
                voucher.getType() == VoucherType.PERCENTAGE ? voucher.getDiscountValue() : 0.0);
                
        } catch (Exception e) {
            return new VoucherApplyResponse(false, e.getMessage(), 
                request.getVoucherCode(), "", request.getOrderAmount(), 0.0, 
                request.getOrderAmount(), "", 0.0);
        }
    }

    @Override
    public VoucherApplyResponse applyVoucherToOrder(String voucherCode, Long customerId, Long orderId) {
        try {
            // Validate parameters
            if (voucherCode == null || voucherCode.trim().isEmpty()) {
                throw new RuntimeException("Voucher code is required");
            }
            if (customerId == null) {
                throw new RuntimeException("Customer ID is required");
            }
            if (orderId == null) {
                throw new RuntimeException("Order ID is required");
            }
            
            // Find voucher
            Voucher voucher = voucherRepository.findByCode(voucherCode)
                    .orElseThrow(() -> new RuntimeException("Voucher not found with code: " + voucherCode));
            
            // Find customer
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
            
            // Find order
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
            
            // Validate voucher
            if (!voucher.isValid()) {
                throw new RuntimeException("Voucher is not valid (expired, inactive, or fully used)");
            }
            
            // Check if customer can use this voucher
            if (!voucher.canApplyToCustomer(customer)) {
                throw new RuntimeException("Customer is not eligible for this voucher");
            }
            
            // Check minimum order amount
            if (order.getTotalPrice() < voucher.getMinimumOrderAmount()) {
                throw new RuntimeException("Order amount must be at least " + voucher.getMinimumOrderAmount());
            }
            
            // Calculate discount
            double discountAmount = 0.0;
            if (voucher.getType() == VoucherType.PERCENTAGE) {
                discountAmount = order.getTotalPrice() * (voucher.getDiscountValue() / 100.0);
                discountAmount = Math.min(discountAmount, voucher.getMaximumDiscount());
            } else {
                discountAmount = voucher.getDiscountValue();
            }
            
            double finalAmount = order.getTotalPrice() - discountAmount;
            
            // Update order with discount
            order.setVoucherDiscount(discountAmount);
            order.setAppliedVoucherCode(voucherCode);
            order.setOriginalAmount(order.getTotalPrice());
            order.setTotalPrice(finalAmount);
            orderRepository.save(order);
            
            // Increment voucher usage
            voucher.setCurrentUsage(voucher.getCurrentUsage() + 1);
            voucherRepository.save(voucher);
            
            // Create voucher usage record
            VoucherUsage voucherUsage = new VoucherUsage();
            voucherUsage.setVoucher(voucher);
            voucherUsage.setOrder(order);
            voucherUsage.setCustomer(customer);
            voucherUsage.setDiscountAmount(discountAmount);
            voucherUsage.setUsedAt(LocalDateTime.now());
            voucherUsageRepository.save(voucherUsage);
            
            return new VoucherApplyResponse(true, "Voucher applied to order successfully", 
                voucherCode, voucher.getName(), order.getTotalPrice() + discountAmount, 
                discountAmount, finalAmount, voucher.getType().toString(), 
                voucher.getType() == VoucherType.PERCENTAGE ? voucher.getDiscountValue() : 0.0);
                
        } catch (Exception e) {
            return new VoucherApplyResponse(false, e.getMessage(), 
                voucherCode, "", 0.0, 0.0, 0.0, "", 0.0);
        }
    }

    // Customer Voucher Management
    @Override
    public List<VoucherDTO> getAvailableVouchersForCustomer(Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

            List<Voucher> activeVouchers = voucherRepository.findActiveVouchers(LocalDateTime.now());
            
            return activeVouchers.stream()
                    .filter(voucher -> voucher.canApplyToCustomer(customer))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error getting available vouchers for customer: " + e.getMessage());
        }
    }

    @Override
    public List<VoucherDTO> getCustomerVouchers(Long customerId) {
        List<CustomerVoucher> customerVouchers = customerVoucherRepository.findByCustomerId(customerId);
        
        return customerVouchers.stream()
                .map(cv -> convertToDTO(cv.getVoucher()))
                .collect(Collectors.toList());
    }

    // Campaign Management
    @Override
    public List<VoucherDTO> getVouchersByCampaign(String campaignType) {
        try {
            // Validate campaign type
            CampaignType campaignTypeEnum;
            try {
                campaignTypeEnum = CampaignType.valueOf(campaignType);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid campaign type: " + campaignType + ". Valid types: " + 
                    Arrays.toString(CampaignType.values()));
            }
            
            return voucherRepository.findByCampaignType(campaignTypeEnum).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error getting vouchers by campaign: " + e.getMessage());
        }
    }

    @Override
    public void sendVouchersToCustomers(Long voucherId, List<Long> customerIds) {
        try {
            System.out.println("=== SENDING VOUCHER TO CUSTOMERS ===");
            System.out.println("Voucher ID: " + voucherId);
            System.out.println("Customer IDs: " + customerIds);
            
            // Validate voucher exists
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Voucher not found with ID: " + voucherId));
            
            System.out.println("Found voucher: " + voucher.getCode() + " - " + voucher.getName());
            System.out.println("Voucher target group: " + voucher.getTargetGroup());
            
            if (!voucher.isValid()) {
                throw new RuntimeException("Voucher is not valid for sending");
            }
            
            // Validate customers exist
            List<Customer> customers = customerRepository.findAllById(customerIds);
            System.out.println("Found " + customers.size() + " customers out of " + customerIds.size() + " requested");
            
            if (customers.size() != customerIds.size()) {
                throw new RuntimeException("Some customers not found. Found: " + customers.size() + ", Requested: " + customerIds.size());
            }
            
            int sentCount = 0;
            int skippedCount = 0;
            
            for (Customer customer : customers) {
                System.out.println("\n--- Processing customer: " + customer.getId() + " - " + customer.getEmail() + " ---");
                
                // Check if customer can use this voucher
                boolean canUse = voucher.canApplyToCustomer(customer);
                System.out.println("Customer can use voucher: " + canUse);
                
                if (!canUse) {
                    System.out.println("❌ Skipping customer " + customer.getId() + " - cannot use this voucher");
                    skippedCount++;
                    continue;
                }
                
                // Check if customer already has this voucher
                Optional<CustomerVoucher> existing = customerVoucherRepository.findByCustomerAndVoucher(customer.getId(), voucherId);
                if (existing.isPresent()) {
                    System.out.println("❌ Skipping customer " + customer.getId() + " - already has this voucher");
                    skippedCount++;
                    continue;
                }
                
                CustomerVoucher cv = new CustomerVoucher();
                cv.setCustomer(customer);
                cv.setVoucher(voucher);
                cv.setStatus(CustomerVoucherStatus.SENT);
                cv.setSentAt(LocalDateTime.now());
                cv.setExpiredAt(voucher.getEndDate());
                cv.setSentVia("SYSTEM");
                
                customerVoucherRepository.save(cv);
                sentCount++;
                
                System.out.println("✅ Voucher sent to customer " + customer.getId());
            }
            
            System.out.println("\n=== SUMMARY ===");
            System.out.println("Total customers processed: " + customers.size());
            System.out.println("Vouchers sent: " + sentCount);
            System.out.println("Customers skipped: " + skippedCount);
            
            if (sentCount == 0) {
                throw new RuntimeException("No vouchers were sent. All customers either cannot use this voucher or already have it.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in sendVouchersToCustomers: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error sending vouchers: " + e.getMessage());
        }
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?") // Chạy lúc 9h sáng mỗi ngày
    public void sendBirthdayVouchers() {
        // Logic gửi voucher sinh nhật
        // Có thể implement sau khi có thêm thông tin ngày sinh của customer
        System.out.println("Checking for birthday vouchers...");
    }

    @Override
    @Scheduled(cron = "0 0 0 25 11 ?") // Chạy lúc 00:00 ngày 25/11 (Black Friday)
    public void sendBlackFridayVouchers() {
        try {
            List<Voucher> blackFridayVouchers = voucherRepository.findByCampaignType(CampaignType.BLACK_FRIDAY);
            
            for (Voucher voucher : blackFridayVouchers) {
                if (voucher.isValid()) {
                    // Gửi voucher cho tất cả khách hàng hoặc nhóm khách hàng cụ thể
                    List<Customer> customers = customerRepository.findAll();
                    List<Long> customerIds = customers.stream()
                            .map(Customer::getId)
                            .collect(Collectors.toList());
                    
                    sendVouchersToCustomers(voucher.getId(), customerIds);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending Black Friday vouchers: " + e.getMessage());
        }
    }

    // Statistics
    @Override
    public List<VoucherStatisticsDTO> getVoucherStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> statistics = voucherUsageRepository.getVoucherUsageStatistics(startDate, endDate);
        
        return statistics.stream().map(row -> {
            VoucherStatisticsDTO dto = new VoucherStatisticsDTO();
            dto.setVoucherId((Long) row[0]);
            dto.setTotalUsage((Long) row[1]);
            dto.setTotalDiscount((Double) row[2]);
            
            // Lấy thông tin voucher
            Voucher voucher = voucherRepository.findById(dto.getVoucherId()).orElse(null);
            if (voucher != null) {
                dto.setVoucherCode(voucher.getCode());
                dto.setVoucherName(voucher.getName());
                dto.setCampaignType(voucher.getCampaignType().toString());
                dto.setStartDate(voucher.getStartDate());
                dto.setEndDate(voucher.getEndDate());
                dto.setStatus(voucher.getStatus().toString());
                dto.setMaxUsage(voucher.getMaxUsage());
                dto.setCurrentUsage(voucher.getCurrentUsage());
                dto.setUsagePercentage((double) voucher.getCurrentUsage() / voucher.getMaxUsage() * 100);
            }
            
            if (dto.getTotalUsage() > 0) {
                dto.setAverageDiscount(dto.getTotalDiscount() / dto.getTotalUsage());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CampaignStatisticsDTO> getCampaignStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> campaignStats = voucherRepository.getCampaignStatistics(startDate, endDate);
        
        return campaignStats.stream().map(row -> {
            CampaignStatisticsDTO dto = new CampaignStatisticsDTO();
            dto.setCampaignType((String) row[0]);
            dto.setCampaignName((String) row[1]);
            dto.setTotalVouchers((Long) row[2]);
            dto.setTotalSent((Long) row[3]);
            dto.setTotalUsed((Long) row[4]);
            dto.setTotalDiscount((Double) row[5]);
            
            if (dto.getTotalSent() > 0) {
                dto.setConversionRate((double) dto.getTotalUsed() / dto.getTotalSent() * 100);
            }
            
            if (dto.getTotalUsed() > 0) {
                dto.setAverageDiscount(dto.getTotalDiscount() / dto.getTotalUsed());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public VoucherStatisticsDTO getVoucherStatisticsById(Long voucherId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Validate voucher exists
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Voucher not found with ID: " + voucherId));
            
            // Get voucher usage statistics
            List<VoucherUsage> usages = voucherUsageRepository.findByVoucherAndDateRange(voucherId, startDate, endDate);
            
            long totalUsage = usages.size();
            double totalDiscount = usages.stream().mapToDouble(VoucherUsage::getDiscountAmount).sum();
            double averageDiscount = totalUsage > 0 ? totalDiscount / totalUsage : 0.0;
            
            VoucherStatisticsDTO dto = new VoucherStatisticsDTO();
            dto.setVoucherId(voucher.getId());
            dto.setVoucherCode(voucher.getCode());
            dto.setVoucherName(voucher.getName());
            dto.setCampaignType(voucher.getCampaignType().toString());
            dto.setTotalUsage(totalUsage);
            dto.setTotalDiscount(totalDiscount);
            dto.setAverageDiscount(averageDiscount);
            dto.setMaxUsage(voucher.getMaxUsage());
            dto.setCurrentUsage(voucher.getCurrentUsage());
            dto.setStartDate(voucher.getStartDate());
            dto.setEndDate(voucher.getEndDate());
            dto.setStatus(voucher.getStatus().toString());
            dto.setUsagePercentage(voucher.getMaxUsage() > 0 ? (double) voucher.getCurrentUsage() / voucher.getMaxUsage() * 100 : 0.0);
            
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error getting voucher statistics: " + e.getMessage());
        }
    }

    // Utility Methods
    @Override
    public boolean isValidVoucher(String voucherCode) {
        Optional<Voucher> voucher = voucherRepository.findByCode(voucherCode);
        return voucher.isPresent() && voucher.get().isValid();
    }

    @Override
    public boolean canCustomerUseVoucher(String voucherCode, Long customerId) {
        Optional<Voucher> voucher = voucherRepository.findByCode(voucherCode);
        if (voucher.isEmpty()) return false;

        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) return false;

        return voucher.get().canApplyToCustomer(customer.get());
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Chạy lúc 2h sáng mỗi ngày
    public void updateVoucherStatus() {
        LocalDateTime now = LocalDateTime.now();
        
        // Cập nhật voucher hết hạn
        List<Voucher> expiredVouchers = voucherRepository.findExpiredVouchers(now);
        for (Voucher voucher : expiredVouchers) {
            voucher.setStatus(VoucherStatus.EXPIRED);
            voucherRepository.save(voucher);
        }

        // Cập nhật voucher đã sử dụng hết
        List<Voucher> fullyUsedVouchers = voucherRepository.findFullyUsedVouchers();
        for (Voucher voucher : fullyUsedVouchers) {
            voucher.setStatus(VoucherStatus.INACTIVE);
            voucherRepository.save(voucher);
        }
    }

    @Override
    public List<VoucherDTO> searchVouchers(String keyword) {
        return voucherRepository.searchVouchers(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper Methods
    private VoucherDTO convertToDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getId());
        dto.setCode(voucher.getCode());
        dto.setName(voucher.getName());
        dto.setDescription(voucher.getDescription());
        dto.setType(voucher.getType().toString());
        dto.setDiscountValue(voucher.getDiscountValue());
        dto.setMinimumOrderAmount(voucher.getMinimumOrderAmount());
        dto.setMaximumDiscount(voucher.getMaximumDiscount());
        dto.setMaxUsage(voucher.getMaxUsage());
        dto.setCurrentUsage(voucher.getCurrentUsage());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        dto.setStatus(voucher.getStatus().toString());
        dto.setTargetGroup(voucher.getTargetGroup().toString());
        dto.setCampaignType(voucher.getCampaignType().toString());
        dto.setCampaignName(voucher.getCampaignName());
        dto.setCreatedAt(voucher.getCreatedAt());
        dto.setUpdatedAt(voucher.getUpdatedAt());
        dto.setValid(voucher.isValid());
        dto.setUsagePercentage(voucher.getMaxUsage() > 0 ? 
            (double) voucher.getCurrentUsage() / voucher.getMaxUsage() * 100 : 0.0);
        
        return dto;
    }

    private void sendVoucherEmail(Customer customer, Voucher voucher) {
        String subject = "Bạn có voucher mới: " + voucher.getName();
        String content = String.format(
            "Xin chào %s,\n\n" +
            "Bạn có voucher mới: %s\n" +
            "Mã voucher: %s\n" +
            "Giảm giá: %s\n" +
            "Hạn sử dụng: %s\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!",
            customer.getFullName(),
            voucher.getName(),
            voucher.getCode(),
            voucher.getType() == VoucherType.PERCENTAGE ? 
                voucher.getDiscountValue() + "%" : voucher.getDiscountValue() + " VND",
            voucher.getEndDate().toString()
        );
        
        emailService.sendEmail(customer.getEmail(), subject, content);
    }
} 