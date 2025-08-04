package aptech.be.models;

import aptech.be.models.enums.CampaignType;
import aptech.be.models.enums.CustomerGroup;
import aptech.be.models.enums.VoucherStatus;
import aptech.be.models.enums.VoucherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VoucherTest {

    private Voucher voucher;
    private Customer customer;
    private CustomerDetail customerDetail;

    @BeforeEach
    void setUp() {
        // Setup voucher
        voucher = new Voucher();
        voucher.setId(1L);
        voucher.setCode("TEST10");
        voucher.setName("Test Voucher");
        voucher.setDescription("Test voucher for testing");
        voucher.setDiscountValue(10.0);
        voucher.setType(VoucherType.PERCENTAGE);
        voucher.setMinimumOrderAmount(100000.0);
        voucher.setMaximumDiscount(50000.0);
        voucher.setMaxUsage(100);
        voucher.setCurrentUsage(0);
        voucher.setStartDate(LocalDateTime.now().minusDays(1));
        voucher.setEndDate(LocalDateTime.now().plusDays(30));
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setTargetGroup(CustomerGroup.ALL);
        voucher.setCampaignType(CampaignType.CUSTOM);

        // Setup customer
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@example.com");
        customer.setFullName("Test Customer");

        customerDetail = new CustomerDetail();
        customerDetail.setCustomerLevel("REGULAR");
        customerDetail.setPoint("500");
        customerDetail.setNewsletterSubscribed(false);
        customer.setCustomerDetail(customerDetail);
    }

    @Test
    void testIsValid_ActiveVoucher() {
        // Given - voucher is already set up as valid

        // When
        boolean result = voucher.isValid();

        // Then
        assertTrue(result);
    }

    @Test
    void testIsValid_InactiveVoucher() {
        // Given
        voucher.setStatus(VoucherStatus.INACTIVE);

        // When
        boolean result = voucher.isValid();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValid_ExpiredVoucher() {
        // Given
        voucher.setEndDate(LocalDateTime.now().minusDays(1));

        // When
        boolean result = voucher.isValid();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValid_FutureVoucher() {
        // Given
        voucher.setStartDate(LocalDateTime.now().plusDays(1));

        // When
        boolean result = voucher.isValid();

        // Then
        assertFalse(result);
    }

    @Test
    void testIsValid_MaxUsageReached() {
        // Given
        voucher.setCurrentUsage(100);
        voucher.setMaxUsage(100);

        // When
        boolean result = voucher.isValid();

        // Then
        assertFalse(result);
    }

    @Test
    void testCanApplyToCustomer_AllGroup() {
        // Given
        voucher.setTargetGroup(CustomerGroup.ALL);

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanApplyToCustomer_VIPGroup_VIPCustomer() {
        // Given
        voucher.setTargetGroup(CustomerGroup.VIP);
        customerDetail.setCustomerLevel("VIP");

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanApplyToCustomer_VIPGroup_PremiumCustomer() {
        // Given
        voucher.setTargetGroup(CustomerGroup.VIP);
        customerDetail.setCustomerLevel("PREMIUM");

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanApplyToCustomer_VIPGroup_HighPoints() {
        // Given
        voucher.setTargetGroup(CustomerGroup.VIP);
        customerDetail.setCustomerLevel("REGULAR");
        customerDetail.setPoint("1500");

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanApplyToCustomer_VIPGroup_RegularCustomer() {
        // Given
        voucher.setTargetGroup(CustomerGroup.VIP);
        customerDetail.setCustomerLevel("REGULAR");
        customerDetail.setPoint("500");

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertFalse(result);
    }

    @Test
    void testCanApplyToCustomer_NewsletterGroup_Subscribed() {
        // Given
        voucher.setTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER);
        customerDetail.setNewsletterSubscribed(true);

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanApplyToCustomer_NewsletterGroup_NotSubscribed() {
        // Given
        voucher.setTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER);
        customerDetail.setNewsletterSubscribed(false);

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertFalse(result);
    }

    @Test
    void testCanApplyToCustomer_NewsletterGroup_NullDetail() {
        // Given
        voucher.setTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER);
        customer.setCustomerDetail(null);

        // When
        boolean result = voucher.canApplyToCustomer(customer);

        // Then
        assertFalse(result);
    }

    @Test
    void testCalculateDiscount_PercentageType() {
        // Given
        voucher.setType(VoucherType.PERCENTAGE);
        voucher.setDiscountValue(10.0);
        voucher.setMaximumDiscount(50000.0);
        double orderAmount = 1000000.0; // 1M VND

        // When
        double result = voucher.calculateDiscount(orderAmount);

        // Then
        assertEquals(50000.0, result); // Should be capped at maximum discount
    }

    @Test
    void testCalculateDiscount_FixedAmountType() {
        // Given
        voucher.setType(VoucherType.FIXED_AMOUNT);
        voucher.setDiscountValue(50000.0);
        double orderAmount = 1000000.0; // 1M VND

        // When
        double result = voucher.calculateDiscount(orderAmount);

        // Then
        assertEquals(50000.0, result);
    }

    @Test
    void testCalculateDiscount_OrderBelowMinimum() {
        // Given
        voucher.setType(VoucherType.PERCENTAGE);
        voucher.setDiscountValue(10.0);
        voucher.setMinimumOrderAmount(100000.0);
        double orderAmount = 50000.0; // Below minimum

        // When
        double result = voucher.calculateDiscount(orderAmount);

        // Then
        assertEquals(0.0, result);
    }

    @Test
    void testCalculateDiscount_PercentageBelowMaximum() {
        // Given
        voucher.setType(VoucherType.PERCENTAGE);
        voucher.setDiscountValue(10.0);
        voucher.setMaximumDiscount(50000.0);
        double orderAmount = 300000.0; // 300K VND

        // When
        double result = voucher.calculateDiscount(orderAmount);

        // Then
        assertEquals(30000.0, result); // 10% of 300K = 30K
    }

    @Test
    void testOnCreate() {
        // Given
        Voucher newVoucher = new Voucher();
        newVoucher.setCode("NEW10");
        newVoucher.setName("New Voucher");

        // When
        newVoucher.onCreate();

        // Then
        assertNotNull(newVoucher.getCreatedAt());
        assertEquals(0, newVoucher.getCurrentUsage());
        assertEquals(VoucherStatus.ACTIVE, newVoucher.getStatus());
    }

    @Test
    void testOnUpdate() {
        // Given
        LocalDateTime originalUpdatedAt = voucher.getUpdatedAt();

        // When
        voucher.onUpdate();

        // Then
        assertNotNull(voucher.getUpdatedAt());
        assertNotEquals(originalUpdatedAt, voucher.getUpdatedAt());
    }
} 