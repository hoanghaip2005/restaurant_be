package aptech.be.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_usages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private Double orderAmount; // Giá trị đơn hàng trước khi áp dụng voucher

    @Column(nullable = false)
    private Double discountAmount; // Số tiền được giảm

    @Column(nullable = false)
    private Double finalAmount; // Giá trị đơn hàng sau khi áp dụng voucher

    @Column(nullable = false)
    private LocalDateTime usedAt;

    @PrePersist
    protected void onCreate() {
        usedAt = LocalDateTime.now();
    }
} 