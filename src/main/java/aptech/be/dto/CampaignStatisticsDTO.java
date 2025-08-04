package aptech.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignStatisticsDTO {
    private String campaignType;
    private String campaignName;
    private Long totalVouchers;
    private Long totalSent;
    private Long totalUsed;
    private Double totalDiscount;
    private Double conversionRate;
    private Double averageDiscount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long totalRevenue; // Doanh thu tăng thêm từ voucher
} 