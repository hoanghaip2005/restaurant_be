package aptech.be.models;

import aptech.be.models.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private VoucherType type; // PERCENTAGE, FIXED_AMOUNT

    @Column(nullable = false)
    private Double discountValue; // Percentage hoặc số tiền cố định

    @Column(nullable = false)
    private Double minimumOrderAmount; // Giá trị đơn hàng tối thiểu

    @Column(nullable = false)
    private Double maximumDiscount; // Giảm giá tối đa (cho voucher percentage)

    @Column(nullable = false)
    private Integer maxUsage; // Số lần sử dụng tối đa

    @Column(nullable = false)
    private Integer currentUsage; // Số lần đã sử dụng

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private VoucherStatus status; // ACTIVE, INACTIVE, EXPIRED

    @Enumerated(EnumType.STRING)
    private CustomerGroup targetGroup; // ALL, VIP, NEWSLETTER_SUBSCRIBER

    @Enumerated(EnumType.STRING)
    private CampaignType campaignType; // BIRTHDAY, BLACK_FRIDAY, NEW_YEAR, CUSTOM

    private String campaignName; // Tên chiến dịch

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL)
    private List<VoucherUsage> voucherUsages;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        currentUsage = 0;
        status = VoucherStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return status == VoucherStatus.ACTIVE 
            && now.isAfter(startDate) 
            && now.isBefore(endDate)
            && currentUsage < maxUsage;
    }

    public boolean canApplyToCustomer(Customer customer) {
        System.out.println("Checking if customer " + customer.getId() + " can use voucher with target group: " + targetGroup);
        
        if (targetGroup == CustomerGroup.ALL) {
            System.out.println("Voucher is for ALL customers - returning true");
            return true;
        }
        
        if (targetGroup == CustomerGroup.VIP) {
            // Kiểm tra xem customer có phải VIP không
            CustomerDetail detail = customer.getCustomerDetail();
            System.out.println("Customer detail: " + (detail != null ? "exists" : "null"));
            
            if (detail != null) {
                System.out.println("Customer level: " + detail.getCustomerLevel());
                System.out.println("Customer points: " + detail.getPoint());
                
                // Kiểm tra customer level
                if ("VIP".equals(detail.getCustomerLevel()) || "PREMIUM".equals(detail.getCustomerLevel())) {
                    System.out.println("Customer is VIP by level - returning true");
                    return true;
                }
                // Kiểm tra điểm tích lũy
                if (detail.getPoint() != null) {
                    try {
                        int points = Integer.parseInt(detail.getPoint());
                        boolean isVipByPoints = points >= 1000; // VIP từ 1000 điểm trở lên
                        System.out.println("Customer points: " + points + ", VIP by points: " + isVipByPoints);
                        return isVipByPoints;
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing points: " + e.getMessage());
                        return false;
                    }
                }
            }
            System.out.println("Customer is not VIP - returning false");
            return false;
        }
        
        if (targetGroup == CustomerGroup.NEWSLETTER_SUBSCRIBER) {
            // Kiểm tra xem customer có đăng ký newsletter không
            CustomerDetail detail = customer.getCustomerDetail();
            System.out.println("Customer detail for newsletter check: " + (detail != null ? "exists" : "null"));
            
            if (detail != null) {
                System.out.println("Newsletter subscribed: " + detail.getNewsletterSubscribed());
            }
            
            boolean canUse = detail != null && Boolean.TRUE.equals(detail.getNewsletterSubscribed());
            System.out.println("Customer can use newsletter voucher: " + canUse);
            return canUse;
        }
        
        System.out.println("Unknown target group - returning false");
        return false;
    }

    public double calculateDiscount(double orderAmount) {
        if (orderAmount < minimumOrderAmount) {
            return 0;
        }

        double discount = 0;
        if (type == VoucherType.PERCENTAGE) {
            discount = orderAmount * (discountValue / 100);
            discount = Math.min(discount, maximumDiscount);
        } else if (type == VoucherType.FIXED_AMOUNT) {
            discount = Math.min(discountValue, orderAmount);
        }

        return discount;
    }
}

 