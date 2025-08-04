package aptech.be.repositories;

import aptech.be.models.Voucher;
import aptech.be.models.enums.CampaignType;
import aptech.be.models.enums.CustomerGroup;
import aptech.be.models.enums.VoucherStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    
    Optional<Voucher> findByCode(String code);
    
    List<Voucher> findByStatus(VoucherStatus status);
    
    List<Voucher> findByTargetGroup(CustomerGroup targetGroup);
    
    List<Voucher> findByCampaignType(CampaignType campaignType);
    
    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND v.startDate <= :now AND v.endDate >= :now")
    List<Voucher> findActiveVouchers(@Param("now") LocalDateTime now);
    
    @Query("SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND v.targetGroup = :targetGroup AND v.startDate <= :now AND v.endDate >= :now")
    List<Voucher> findActiveVouchersByTargetGroup(@Param("targetGroup") CustomerGroup targetGroup, @Param("now") LocalDateTime now);
    
    @Query("SELECT v FROM Voucher v WHERE v.campaignType = :campaignType AND v.status = 'ACTIVE'")
    List<Voucher> findActiveVouchersByCampaign(@Param("campaignType") CampaignType campaignType);
    
    @Query("SELECT v FROM Voucher v WHERE v.endDate < :now AND v.status = 'ACTIVE'")
    List<Voucher> findExpiredVouchers(@Param("now") LocalDateTime now);
    
    @Query("SELECT v FROM Voucher v WHERE v.currentUsage >= v.maxUsage AND v.status = 'ACTIVE'")
    List<Voucher> findFullyUsedVouchers();
    
    @Query("SELECT COUNT(v) FROM Voucher v WHERE v.campaignType = :campaignType")
    Long countByCampaignType(@Param("campaignType") CampaignType campaignType);
    
    @Query("SELECT v FROM Voucher v WHERE v.name LIKE %:keyword% OR v.description LIKE %:keyword%")
    List<Voucher> searchVouchers(@Param("keyword") String keyword);

    @Query(value = "SELECT v.id, COUNT(vu), COALESCE(SUM(vu.discount_amount), 0) " +
           "FROM vouchers v LEFT JOIN voucher_usages vu ON v.id = vu.voucher_id " +
           "WHERE (v.start_date BETWEEN :startDate AND :endDate) OR (v.end_date BETWEEN :startDate AND :endDate) " +
           "GROUP BY v.id", nativeQuery = true)
    List<Object[]> getVoucherUsageStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT " +
           "    v.campaign_type, " +
           "    v.campaign_name, " +
           "    COUNT(DISTINCT v.id) as total_vouchers, " +
           "    COALESCE(SUM(CASE WHEN cv.status = 'SENT' THEN 1 ELSE 0 END), 0) as total_sent, " +
           "    COALESCE(SUM(CASE WHEN cv.status = 'USED' THEN 1 ELSE 0 END), 0) as total_used, " +
           "    COALESCE(SUM(vu.discount_amount), 0) as total_discount " +
           "FROM vouchers v " +
           "LEFT JOIN customer_vouchers cv ON v.id = cv.voucher_id " +
           "LEFT JOIN voucher_usages vu ON v.id = vu.voucher_id " +
           "WHERE (v.start_date BETWEEN :startDate AND :endDate) OR (v.end_date BETWEEN :startDate AND :endDate) " +
           "GROUP BY v.campaign_type, v.campaign_name", nativeQuery = true)
    List<Object[]> getCampaignStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 