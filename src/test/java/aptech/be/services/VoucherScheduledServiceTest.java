package aptech.be.services;

import aptech.be.models.Customer;
import aptech.be.models.CustomerDetail;
import aptech.be.models.Voucher;
import aptech.be.models.enums.CampaignType;
import aptech.be.models.enums.CustomerGroup;
import aptech.be.models.enums.VoucherStatus;
import aptech.be.models.enums.VoucherType;
import aptech.be.repositories.CustomerRepository;
import aptech.be.repositories.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherScheduledServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VoucherService voucherService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VoucherScheduledService scheduledService;

    private Voucher birthdayVoucher;
    private Voucher blackFridayVoucher;
    private Voucher vipVoucher;
    private Voucher newsletterVoucher;
    private Customer vipCustomer;
    private Customer newsletterCustomer;
    private CustomerDetail vipCustomerDetail;
    private CustomerDetail newsletterCustomerDetail;

    @BeforeEach
    void setUp() {
        // Setup birthday voucher
        birthdayVoucher = new Voucher();
        birthdayVoucher.setId(1L);
        birthdayVoucher.setCode("BIRTHDAY10");
        birthdayVoucher.setName("Birthday Voucher");
        birthdayVoucher.setCampaignType(CampaignType.BIRTHDAY);
        birthdayVoucher.setStatus(VoucherStatus.ACTIVE);
        birthdayVoucher.setStartDate(LocalDateTime.now().minusDays(1));
        birthdayVoucher.setEndDate(LocalDateTime.now().plusDays(30));
        birthdayVoucher.setCurrentUsage(0);
        birthdayVoucher.setMaxUsage(100);

        // Setup Black Friday voucher
        blackFridayVoucher = new Voucher();
        blackFridayVoucher.setId(2L);
        blackFridayVoucher.setCode("BLACKFRIDAY20");
        blackFridayVoucher.setName("Black Friday Voucher");
        blackFridayVoucher.setCampaignType(CampaignType.BLACK_FRIDAY);
        blackFridayVoucher.setStatus(VoucherStatus.ACTIVE);
        blackFridayVoucher.setStartDate(LocalDateTime.now().minusDays(1));
        blackFridayVoucher.setEndDate(LocalDateTime.now().plusDays(30));
        blackFridayVoucher.setCurrentUsage(0);
        blackFridayVoucher.setMaxUsage(100);

        // Setup VIP voucher
        vipVoucher = new Voucher();
        vipVoucher.setId(3L);
        vipVoucher.setCode("VIP15");
        vipVoucher.setName("VIP Voucher");
        vipVoucher.setTargetGroup(CustomerGroup.VIP);
        vipVoucher.setStatus(VoucherStatus.ACTIVE);
        vipVoucher.setStartDate(LocalDateTime.now().minusDays(1));
        vipVoucher.setEndDate(LocalDateTime.now().plusDays(30));
        vipVoucher.setCurrentUsage(0);
        vipVoucher.setMaxUsage(100);

        // Setup Newsletter voucher
        newsletterVoucher = new Voucher();
        newsletterVoucher.setId(4L);
        newsletterVoucher.setCode("NEWSLETTER5");
        newsletterVoucher.setName("Newsletter Voucher");
        newsletterVoucher.setTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER);
        newsletterVoucher.setStatus(VoucherStatus.ACTIVE);
        newsletterVoucher.setStartDate(LocalDateTime.now().minusDays(1));
        newsletterVoucher.setEndDate(LocalDateTime.now().plusDays(30));
        newsletterVoucher.setCurrentUsage(0);
        newsletterVoucher.setMaxUsage(100);

        // Setup VIP customer
        vipCustomer = new Customer();
        vipCustomer.setId(1L);
        vipCustomer.setEmail("vip@example.com");
        vipCustomer.setFullName("VIP Customer");

        vipCustomerDetail = new CustomerDetail();
        vipCustomerDetail.setCustomerLevel("VIP");
        vipCustomerDetail.setPoint("1500");
        vipCustomerDetail.setNewsletterSubscribed(false);
        vipCustomer.setCustomerDetail(vipCustomerDetail);

        // Setup Newsletter customer
        newsletterCustomer = new Customer();
        newsletterCustomer.setId(2L);
        newsletterCustomer.setEmail("newsletter@example.com");
        newsletterCustomer.setFullName("Newsletter Customer");

        newsletterCustomerDetail = new CustomerDetail();
        newsletterCustomerDetail.setCustomerLevel("REGULAR");
        newsletterCustomerDetail.setPoint("500");
        newsletterCustomerDetail.setNewsletterSubscribed(true);
        newsletterCustomer.setCustomerDetail(newsletterCustomerDetail);
    }

    @Test
    void testSendBirthdayVouchers() {
        // Given
        List<Voucher> birthdayVouchers = Arrays.asList(birthdayVoucher);
        List<Customer> birthdayCustomers = Arrays.asList(vipCustomer);
        
        when(voucherRepository.findByCampaignType(CampaignType.BIRTHDAY)).thenReturn(birthdayVouchers);
        when(customerRepository.findAll()).thenReturn(birthdayCustomers);

        // When
        scheduledService.sendBirthdayVouchers();

        // Then
        // Since getCustomersWithBirthdayToday() returns empty list by default, no vouchers will be sent
        verify(voucherService, never()).sendVouchersToCustomers(anyLong(), anyList());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendBlackFridayVouchers() {
        // Given
        List<Voucher> blackFridayVouchers = Arrays.asList(blackFridayVoucher);
        List<Customer> allCustomers = Arrays.asList(vipCustomer, newsletterCustomer);
        
        when(voucherRepository.findByCampaignType(CampaignType.BLACK_FRIDAY)).thenReturn(blackFridayVouchers);
        when(customerRepository.findAll()).thenReturn(allCustomers);

        // When
        scheduledService.sendBlackFridayVouchers();

        // Then
        verify(voucherService, times(1)).sendVouchersToCustomers(anyLong(), anyList());
    }

    @Test
    void testSendNewYearVouchers() {
        // Given
        List<Voucher> newYearVouchers = Arrays.asList(birthdayVoucher);
        List<Customer> allCustomers = Arrays.asList(vipCustomer, newsletterCustomer);
        
        when(voucherRepository.findByCampaignType(CampaignType.NEW_YEAR)).thenReturn(newYearVouchers);
        when(customerRepository.findAll()).thenReturn(allCustomers);

        // When
        scheduledService.sendNewYearVouchers();

        // Then
        verify(voucherService, times(1)).sendVouchersToCustomers(anyLong(), anyList());
    }

    @Test
    void testSendVIPMonthlyVouchers() {
        // Given
        List<Voucher> vipVouchers = Arrays.asList(vipVoucher);
        List<Customer> vipCustomers = Arrays.asList(vipCustomer);
        
        when(voucherRepository.findByTargetGroup(CustomerGroup.VIP)).thenReturn(vipVouchers);
        when(customerRepository.findAll()).thenReturn(vipCustomers);

        // When
        scheduledService.sendVIPMonthlyVouchers();

        // Then
        verify(voucherService, times(1)).sendVouchersToCustomers(anyLong(), anyList());
    }

    @Test
    void testSendNewsletterVouchers() {
        // Given
        List<Voucher> newsletterVouchers = Arrays.asList(newsletterVoucher);
        List<Customer> newsletterCustomers = Arrays.asList(newsletterCustomer);
        
        when(voucherRepository.findByTargetGroup(CustomerGroup.NEWSLETTER_SUBSCRIBER)).thenReturn(newsletterVouchers);
        when(customerRepository.findAll()).thenReturn(newsletterCustomers);

        // When
        scheduledService.sendNewsletterVouchers();

        // Then
        verify(voucherService, times(1)).sendVouchersToCustomers(anyLong(), anyList());
    }

    @Test
    void testSendBirthdayVouchers_NoValidVouchers() {
        // Given
        birthdayVoucher.setStatus(VoucherStatus.INACTIVE);
        List<Voucher> birthdayVouchers = Arrays.asList(birthdayVoucher);
        
        when(voucherRepository.findByCampaignType(CampaignType.BIRTHDAY)).thenReturn(birthdayVouchers);

        // When
        scheduledService.sendBirthdayVouchers();

        // Then
        verify(voucherService, never()).sendVouchersToCustomers(anyLong(), anyList());
    }

    @Test
    void testSendBirthdayVouchers_NoCustomers() {
        // Given
        List<Voucher> birthdayVouchers = Arrays.asList(birthdayVoucher);
        List<Customer> emptyCustomers = Arrays.asList();
        
        when(voucherRepository.findByCampaignType(CampaignType.BIRTHDAY)).thenReturn(birthdayVouchers);
        when(customerRepository.findAll()).thenReturn(emptyCustomers);

        // When
        scheduledService.sendBirthdayVouchers();

        // Then
        verify(voucherService, never()).sendVouchersToCustomers(anyLong(), anyList());
    }
} 