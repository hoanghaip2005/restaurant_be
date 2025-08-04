package aptech.be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherApplyRequest {
    private String voucherCode;
    private Long customerId;
    private Double orderAmount;
} 