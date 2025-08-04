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
    
    @Query("SELECT v.campaignType, v.campaignName, COUNT(v), " +
           "(SELECT COUNT(cv) FROM CustomerVoucher cv WHERE cv.voucher.campaignType = v.campaignType AND cv.status = 'SENT'), " +
           "(SELECT COUNT(cv) FROM CustomerVoucher cv WHERE cv.voucher.campaignType = v.campaignType AND cv.status = 'USED'), " +
           "(SELECT COALESCE(SUM(vu.discountAmount), 0) FROM VoucherUsage vu WHERE vu.voucher.campaignType = v.campaignType) " +
           "FROM Voucher v " +
           "WHERE v.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY v.campaignType, v.campaignName")
    List<Object[]> getCampaignStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 