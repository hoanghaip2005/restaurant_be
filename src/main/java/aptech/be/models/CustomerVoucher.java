package aptech.be.models;

import aptech.be.models.enums.CustomerVoucherStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Enumerated(EnumType.STRING)
    private CustomerVoucherStatus status; // SENT, USED, EXPIRED

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private LocalDateTime usedAt;

    private LocalDateTime expiredAt;

    @Column(columnDefinition = "TEXT")
    private String sentVia; // EMAIL, SMS, SYSTEM

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        status = CustomerVoucherStatus.SENT;
    }
}

 