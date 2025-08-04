package aptech.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherStatisticsDTO {
    private Long voucherId;
    private String voucherCode;
    private String voucherName;
    private String campaignType;
    private Long totalUsage;
    private Double totalDiscount;
    private Double averageDiscount;
    private Double conversionRate; // Tỷ lệ chuyển đổi (số lần sử dụng / số lần gửi)
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Integer maxUsage;
    private Integer currentUsage;
    private Double usagePercentage;
} 