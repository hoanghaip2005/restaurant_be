package aptech.be.services;

import aptech.be.dto.VoucherCreateRequest;
import aptech.be.dto.VoucherDTO;
import aptech.be.dto.VoucherStatisticsDTO;
import aptech.be.models.Customer;
import aptech.be.models.CustomerDetail;
import aptech.be.models.Voucher;
import aptech.be.models.enums.CampaignType;
import aptech.be.models.enums.CustomerGroup;
import aptech.be.models.enums.VoucherStatus;
import aptech.be.models.enums.VoucherType;
import aptech.be.repositories.CustomerRepository;
import aptech.be.repositories.CustomerVoucherRepository;
import aptech.be.repositories.VoucherRepository;
import aptech.be.repositories.VoucherUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private VoucherUsageRepository voucherUsageRepository;

    @Mock
    private CustomerVoucherRepository customerVoucherRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private Voucher testVoucher;
    private Customer testCustomer;
    private CustomerDetail testCustomerDetail;
    private VoucherCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        // Setup test voucher
        testVoucher = new Voucher();
        testVoucher.setId(1L);
        testVoucher.setCode("TEST10");
        testVoucher.setName("Test Voucher");
        testVoucher.setDescription("Test voucher for testing");
        testVoucher.setDiscountValue(10.0);
        testVoucher.setType(VoucherType.PERCENTAGE);
        testVoucher.setMinimumOrderAmount(100000.0);
        testVoucher.setMaximumDiscount(50000.0);
        testVoucher.setMaxUsage(100);
        testVoucher.setCurrentUsage(0);
        testVoucher.setStartDate(LocalDateTime.now().minusDays(1));
        testVoucher.setEndDate(LocalDateTime.now().plusDays(30));
        testVoucher.setStatus(VoucherStatus.ACTIVE);
        testVoucher.setTargetGroup(CustomerGroup.ALL);
        testVoucher.setCampaignType(CampaignType.CUSTOM);

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setEmail("test@example.com");
        testCustomer.setFullName("Test Customer");

        testCustomerDetail = new CustomerDetail();
        testCustomerDetail.setCustomerLevel("REGULAR");
        testCustomerDetail.setPoint("500");
        testCustomerDetail.setNewsletterSubscribed(false);
        testCustomer.setCustomerDetail(testCustomerDetail);

        // Setup create request
        createRequest = new VoucherCreateRequest();
        createRequest.setCode("TEST10");
        createRequest.setName("Test Voucher");
        createRequest.setDescription("Test voucher for testing");
        createRequest.setDiscountValue(10.0);
        createRequest.setType("PERCENTAGE");
        createRequest.setMinimumOrderAmount(100000.0);
        createRequest.setMaximumDiscount(50000.0);
        createRequest.setMaxUsage(100);
        createRequest.setStartDate(LocalDateTime.now().minusDays(1));
        createRequest.setEndDate(LocalDateTime.now().plusDays(30));
        createRequest.setTargetGroup("ALL");
        createRequest.setCampaignType("CUSTOM");
    }

    @Test
    void testCreateVoucher() {
        // Given
        when(voucherRepository.save(any(Voucher.class))).thenReturn(testVoucher);

        // When
        VoucherDTO result = voucherService.createVoucher(createRequest);

        // Then
        assertNotNull(result);
        assertEquals("TEST10", result.getCode());
        assertEquals("Test Voucher", result.getName());
        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    void testGetAllVouchers() {
        // Given
        List<Voucher> vouchers = Arrays.asList(testVoucher);
        when(voucherRepository.findAll()).thenReturn(vouchers);

        // When
        List<VoucherDTO> result = voucherService.getAllVouchers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST10", result.get(0).getCode());
    }

    @Test
    void testGetVoucherByCode() {
        // Given
        when(voucherRepository.findByCode("TEST10")).thenReturn(Optional.of(testVoucher));

        // When
        VoucherDTO result = voucherService.getVoucherByCode("TEST10");

        // Then
        assertNotNull(result);
        assertEquals("TEST10", result.getCode());
    }

    @Test
    void testIsValidVoucher() {
        // Given
        when(voucherRepository.findByCode("TEST10")).thenReturn(Optional.of(testVoucher));

        // When
        boolean result = voucherService.isValidVoucher("TEST10");

        // Then
        assertTrue(result);
    }

    @Test
    void testCanCustomerUseVoucher_AllGroup() {
        // Given
        testVoucher.setTargetGroup(CustomerGroup.ALL);
        when(voucherRepository.findByCode("TEST10")).thenReturn(Optional.of(testVoucher));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        boolean result = voucherService.canCustomerUseVoucher("TEST10", 1L);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanCustomerUseVoucher_VIPGroup() {
        // Given
        testVoucher.setTargetGroup(CustomerGroup.VIP);
        testCustomerDetail.setCustomerLevel("VIP");
        when(voucherRepository.findByCode("TEST10")).thenReturn(Optional.of(testVoucher));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        boolean result = voucherService.canCustomerUseVoucher("TEST10", 1L);

        // Then
        assertTrue(result);
    }

    @Test
    void testCanCustomerUseVoucher_NewsletterGroup() {
        // Given
        testVoucher.setTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER);
        testCustomerDetail.setNewsletterSubscribed(true);
        when(voucherRepository.findByCode("TEST10")).thenReturn(Optional.of(testVoucher));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        boolean result = voucherService.canCustomerUseVoucher("TEST10", 1L);

        // Then
        assertTrue(result);
    }

    @Test
    void testGetVouchersByCampaign() {
        // Given
        testVoucher.setCampaignType(CampaignType.BIRTHDAY);
        List<Voucher> vouchers = Arrays.asList(testVoucher);
        when(voucherRepository.findByCampaignType(CampaignType.BIRTHDAY)).thenReturn(vouchers);

        // When
        List<VoucherDTO> result = voucherService.getVouchersByCampaign("BIRTHDAY");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CampaignType.BIRTHDAY.toString(), result.get(0).getCampaignType());
    }

    @Test
    void testSearchVouchers() {
        // Given
        List<Voucher> vouchers = Arrays.asList(testVoucher);
        when(voucherRepository.searchVouchers("Test")).thenReturn(vouchers);

        // When
        List<VoucherDTO> result = voucherService.searchVouchers("Test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Test"));
    }

    @Test
    void testUpdateVoucherStatus() {
        // Given
        Voucher expiredVoucher = new Voucher();
        expiredVoucher.setEndDate(LocalDateTime.now().minusDays(1));
        expiredVoucher.setStatus(VoucherStatus.ACTIVE);
        
        List<Voucher> expiredVouchers = Arrays.asList(expiredVoucher);
        List<Voucher> fullyUsedVouchers = Arrays.asList();
        
        when(voucherRepository.findExpiredVouchers(any(LocalDateTime.class))).thenReturn(expiredVouchers);
        when(voucherRepository.findFullyUsedVouchers()).thenReturn(fullyUsedVouchers);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(expiredVoucher);

        // When
        voucherService.updateVoucherStatus();

        // Then
        verify(voucherRepository).save(any(Voucher.class));
    }
} 