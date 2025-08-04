package aptech.be.repositories;

import aptech.be.models.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    
    List<VoucherUsage> findByCustomerId(Long customerId);
    
    List<VoucherUsage> findByVoucherId(Long voucherId);
    
    List<VoucherUsage> findByOrderId(Long orderId);
    
    @Query("SELECT vu FROM VoucherUsage vu WHERE vu.usedAt BETWEEN :startDate AND :endDate")
    List<VoucherUsage> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT vu FROM VoucherUsage vu WHERE vu.voucher.id = :voucherId AND vu.usedAt BETWEEN :startDate AND :endDate")
    List<VoucherUsage> findByVoucherAndDateRange(@Param("voucherId") Long voucherId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(vu) FROM VoucherUsage vu WHERE vu.voucher.id = :voucherId")
    Long countUsageByVoucher(@Param("voucherId") Long voucherId);
    
    @Query("SELECT SUM(vu.discountAmount) FROM VoucherUsage vu WHERE vu.voucher.id = :voucherId")
    Double getTotalDiscountByVoucher(@Param("voucherId") Long voucherId);
    
    @Query("SELECT SUM(vu.discountAmount) FROM VoucherUsage vu WHERE vu.usedAt BETWEEN :startDate AND :endDate")
    Double getTotalDiscountByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT vu.voucher.id, COUNT(vu) as usageCount, SUM(vu.discountAmount) as totalDiscount " +
           "FROM VoucherUsage vu " +
           "WHERE vu.usedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY vu.voucher.id " +
           "ORDER BY usageCount DESC")
    List<Object[]> getVoucherUsageStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 