package aptech.be.services;

import aptech.be.models.Customer;
import aptech.be.models.CustomerDetail;
import aptech.be.models.Voucher;
import aptech.be.models.enums.CampaignType;
import aptech.be.models.enums.CustomerGroup;
import aptech.be.repositories.CustomerRepository;
import aptech.be.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherScheduledService {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmailService emailService;

    // Gửi voucher sinh nhật hàng ngày lúc 9h sáng
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBirthdayVouchers() {
        System.out.println("Checking for birthday vouchers...");
        
        // Lấy voucher sinh nhật đang hoạt động
        List<Voucher> birthdayVouchers = voucherRepository.findByCampaignType(CampaignType.BIRTHDAY);
        
        for (Voucher voucher : birthdayVouchers) {
            if (voucher.isValid()) {
                // Lấy danh sách khách hàng có sinh nhật hôm nay
                List<Customer> birthdayCustomers = getCustomersWithBirthdayToday();
                
                if (!birthdayCustomers.isEmpty()) {
                    List<Long> customerIds = birthdayCustomers.stream()
                            .map(Customer::getId)
                            .collect(Collectors.toList());
                    
                    voucherService.sendVouchersToCustomers(voucher.getId(), customerIds);
                    
                    // Gửi email chúc mừng sinh nhật
                    for (Customer customer : birthdayCustomers) {
                        sendBirthdayEmail(customer, voucher);
                    }
                }
            }
        }
    }

    // Gửi voucher Black Friday vào ngày 25/11 lúc 00:00
    @Scheduled(cron = "0 0 0 25 11 ?")
    public void sendBlackFridayVouchers() {
        System.out.println("Sending Black Friday vouchers...");
        
        List<Voucher> blackFridayVouchers = voucherRepository.findByCampaignType(CampaignType.BLACK_FRIDAY);
        
        for (Voucher voucher : blackFridayVouchers) {
            if (voucher.isValid()) {
                // Gửi cho tất cả khách hàng hoặc nhóm khách hàng cụ thể
                List<Customer> allCustomers = customerRepository.findAll();
                List<Long> customerIds = allCustomers.stream()
                        .map(Customer::getId)
                        .collect(Collectors.toList());
                
                voucherService.sendVouchersToCustomers(voucher.getId(), customerIds);
            }
        }
    }

    // Gửi voucher năm mới vào ngày 1/1 lúc 00:00
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void sendNewYearVouchers() {
        System.out.println("Sending New Year vouchers...");
        
        List<Voucher> newYearVouchers = voucherRepository.findByCampaignType(CampaignType.NEW_YEAR);
        
        for (Voucher voucher : newYearVouchers) {
            if (voucher.isValid()) {
                // Gửi cho tất cả khách hàng
                List<Customer> allCustomers = customerRepository.findAll();
                List<Long> customerIds = allCustomers.stream()
                        .map(Customer::getId)
                        .collect(Collectors.toList());
                
                voucherService.sendVouchersToCustomers(voucher.getId(), customerIds);
            }
        }
    }

    // Gửi voucher cho khách hàng VIP hàng tháng
    @Scheduled(cron = "0 0 10 1 * ?") // Ngày 1 hàng tháng lúc 10h sáng
    public void sendVIPMonthlyVouchers() {
        System.out.println("Sending VIP monthly vouchers...");
        
        // Lấy danh sách khách hàng VIP
        List<Customer> vipCustomers = getVIPCustomers();
        
        if (!vipCustomers.isEmpty()) {
            // Tạo voucher đặc biệt cho VIP (có thể implement logic tạo voucher động)
            // Hoặc sử dụng voucher có sẵn với target group VIP
            List<Voucher> vipVouchers = voucherRepository.findByTargetGroup(CustomerGroup.VIP);
            
            for (Voucher voucher : vipVouchers) {
                if (voucher.isValid()) {
                    List<Long> customerIds = vipCustomers.stream()
                            .map(Customer::getId)
                            .collect(Collectors.toList());
                    
                    voucherService.sendVouchersToCustomers(voucher.getId(), customerIds);
                }
            }
        }
    }

    // Gửi voucher cho khách hàng đăng ký newsletter hàng tuần
    @Scheduled(cron = "0 0 9 * * MON") // Thứ 2 hàng tuần lúc 9h sáng
    public void sendNewsletterVouchers() {
        System.out.println("Sending newsletter vouchers...");
        
        // Lấy danh sách khách hàng đăng ký newsletter
        List<Customer> newsletterCustomers = getNewsletterSubscribers();
        
        if (!newsletterCustomers.isEmpty()) {
            List<Voucher> newsletterVouchers = voucherRepository.findByTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER);
            
            for (Voucher voucher : newsletterVouchers) {
                if (voucher.isValid()) {
                    List<Long> customerIds = newsletterCustomers.stream()
                            .map(Customer::getId)
                            .collect(Collectors.toList());
                    
                    voucherService.sendVouchersToCustomers(voucher.getId(), customerIds);
                }
            }
        }
    }

    // Helper methods
    private List<Customer> getCustomersWithBirthdayToday() {
        // Logic để lấy khách hàng có sinh nhật hôm nay
        // Cần thêm trường birthDate vào CustomerDetail
        return customerRepository.findAll().stream()
                .filter(customer -> {
                    CustomerDetail detail = customer.getCustomerDetail();
                    // Tạm thời return false, cần implement logic kiểm tra ngày sinh
                    return false;
                })
                .collect(Collectors.toList());
    }

    private List<Customer> getVIPCustomers() {
        return customerRepository.findAll().stream()
                .filter(customer -> {
                    CustomerDetail detail = customer.getCustomerDetail();
                    if (detail != null) {
                        // Kiểm tra customer level
                        if ("VIP".equals(detail.getCustomerLevel()) || "PREMIUM".equals(detail.getCustomerLevel())) {
                            return true;
                        }
                        // Kiểm tra điểm tích lũy
                        if (detail.getPoint() != null) {
                            try {
                                int points = Integer.parseInt(detail.getPoint());
                                return points >= 1000;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private List<Customer> getNewsletterSubscribers() {
        return customerRepository.findAll().stream()
                .filter(customer -> {
                    CustomerDetail detail = customer.getCustomerDetail();
                    return detail != null && Boolean.TRUE.equals(detail.getNewsletterSubscribed());
                })
                .collect(Collectors.toList());
    }

    private void sendBirthdayEmail(Customer customer, Voucher voucher) {
        String subject = "Chúc mừng sinh nhật! Bạn có voucher đặc biệt";
        String content = String.format(
            "Xin chào %s,\n\n" +
            "Chúc mừng sinh nhật! 🎉\n\n" +
            "Nhân dịp sinh nhật của bạn, chúng tôi gửi tặng voucher đặc biệt:\n" +
            "Mã voucher: %s\n" +
            "Giảm giá: %s\n" +
            "Hạn sử dụng: %s\n\n" +
            "Chúc bạn có một ngày sinh nhật thật vui vẻ!\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!",
            customer.getFullName(),
            voucher.getCode(),
            voucher.getType().toString().equals("PERCENTAGE") ? 
                voucher.getDiscountValue() + "%" : voucher.getDiscountValue() + " VND",
            voucher.getEndDate().toString()
        );
        
        emailService.sendEmail(customer.getEmail(), subject, content);
    }
} 