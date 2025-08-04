package aptech.be.repositories;

import aptech.be.models.CustomerVoucher;
import aptech.be.models.enums.CampaignType;
import aptech.be.models.enums.CustomerVoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Long> {
    
    List<CustomerVoucher> findByCustomerId(Long customerId);
    
    List<CustomerVoucher> findByVoucherId(Long voucherId);
    
    List<CustomerVoucher> findByStatus(CustomerVoucherStatus status);
    
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.status = 'SENT'")
    List<CustomerVoucher> findActiveVouchersByCustomer(@Param("customerId") Long customerId);
    
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.customer.id = :customerId AND cv.voucher.id = :voucherId AND cv.status = 'SENT'")
    Optional<CustomerVoucher> findByCustomerAndVoucher(@Param("customerId") Long customerId, @Param("voucherId") Long voucherId);
    
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.voucher.campaignType = :campaignType AND cv.status = 'SENT'")
    List<CustomerVoucher> findByCampaignType(@Param("campaignType") CampaignType campaignType);
    
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.sentAt BETWEEN :startDate AND :endDate")
    List<CustomerVoucher> findBySentDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(cv) FROM CustomerVoucher cv WHERE cv.voucher.id = :voucherId AND cv.status = 'SENT'")
    Long countSentVouchers(@Param("voucherId") Long voucherId);
    
    @Query("SELECT COUNT(cv) FROM CustomerVoucher cv WHERE cv.voucher.id = :voucherId AND cv.status = 'USED'")
    Long countUsedVouchers(@Param("voucherId") Long voucherId);
    
    @Query("SELECT cv FROM CustomerVoucher cv WHERE cv.expiredAt < :now AND cv.status = 'SENT'")
    List<CustomerVoucher> findExpiredVouchers(@Param("now") LocalDateTime now);
} 