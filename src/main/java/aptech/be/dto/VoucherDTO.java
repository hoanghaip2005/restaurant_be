package aptech.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String type; // PERCENTAGE, FIXED_AMOUNT
    private Double discountValue;
    private Double minimumOrderAmount;
    private Double maximumDiscount;
    private Integer maxUsage;
    private Integer currentUsage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status; // ACTIVE, INACTIVE, EXPIRED
    private String targetGroup; // ALL, VIP, NEWSLETTER_SUBSCRIBER
    private String campaignType; // BIRTHDAY, BLACK_FRIDAY, NEW_YEAR, CUSTOM
    private String campaignName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isValid;
    private Double usagePercentage;
} 