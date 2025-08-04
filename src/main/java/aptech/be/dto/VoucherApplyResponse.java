package aptech.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherApplyResponse {
    private boolean success;
    private String message;
    private String voucherCode;
    private String voucherName;
    private Double originalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String discountType;
    private Double discountPercentage;
} 