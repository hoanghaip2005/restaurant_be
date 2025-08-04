package aptech.be.controllers;

import aptech.be.dto.VoucherCreateRequest;
import aptech.be.dto.VoucherDTO;
import aptech.be.dto.VoucherStatisticsDTO;
import aptech.be.dto.CampaignStatisticsDTO;
import aptech.be.services.VoucherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VoucherControllerTest {

    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private VoucherController voucherController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private VoucherCreateRequest createRequest;
    private VoucherDTO voucherDTO;
    private VoucherStatisticsDTO statisticsDTO;
    private CampaignStatisticsDTO campaignStatisticsDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(voucherController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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

        // Setup voucher DTO
        voucherDTO = new VoucherDTO();
        voucherDTO.setId(1L);
        voucherDTO.setCode("TEST10");
        voucherDTO.setName("Test Voucher");
        voucherDTO.setDescription("Test voucher for testing");
        voucherDTO.setDiscountValue(10.0);
        voucherDTO.setType("PERCENTAGE");
        voucherDTO.setMinimumOrderAmount(100000.0);
        voucherDTO.setMaximumDiscount(50000.0);
        voucherDTO.setMaxUsage(100);
        voucherDTO.setCurrentUsage(0);
        voucherDTO.setStartDate(LocalDateTime.now().minusDays(1));
        voucherDTO.setEndDate(LocalDateTime.now().plusDays(30));
        voucherDTO.setStatus("ACTIVE");
        voucherDTO.setTargetGroup("ALL");
        voucherDTO.setCampaignType("CUSTOM");

        // Setup statistics DTO
        statisticsDTO = new VoucherStatisticsDTO();
        statisticsDTO.setVoucherId(1L);
        statisticsDTO.setVoucherCode("TEST10");
        statisticsDTO.setVoucherName("Test Voucher");
        statisticsDTO.setTotalUsage(50L);
        statisticsDTO.setTotalDiscount(500000.0);
        statisticsDTO.setAverageDiscount(10000.0);
        statisticsDTO.setConversionRate(25.0);
        statisticsDTO.setUsagePercentage(50.0);

        // Setup campaign statistics DTO
        campaignStatisticsDTO = new CampaignStatisticsDTO();
        campaignStatisticsDTO.setCampaignType("BIRTHDAY");
        campaignStatisticsDTO.setCampaignName("Birthday Campaign");
        campaignStatisticsDTO.setTotalVouchers(10L);
        campaignStatisticsDTO.setTotalSent(1000L);
        campaignStatisticsDTO.setTotalUsed(250L);
        campaignStatisticsDTO.setTotalDiscount(2500000.0);
        campaignStatisticsDTO.setConversionRate(25.0);
        campaignStatisticsDTO.setAverageDiscount(10000.0);
        campaignStatisticsDTO.setTotalRevenue(5000000L);
    }

    @Test
    void testCreateVoucher() throws Exception {
        // Given
        when(voucherService.createVoucher(any(VoucherCreateRequest.class))).thenReturn(voucherDTO);

        // When & Then
        mockMvc.perform(post("/api/vouchers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST10"))
                .andExpect(jsonPath("$.name").value("Test Voucher"));

        verify(voucherService).createVoucher(any(VoucherCreateRequest.class));
    }

    @Test
    void testGetAllVouchers() throws Exception {
        // Given
        List<VoucherDTO> vouchers = Arrays.asList(voucherDTO);
        when(voucherService.getAllVouchers()).thenReturn(vouchers);

        // When & Then
        mockMvc.perform(get("/api/vouchers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TEST10"))
                .andExpect(jsonPath("$[0].name").value("Test Voucher"));

        verify(voucherService).getAllVouchers();
    }

    @Test
    void testGetVoucherByCode() throws Exception {
        // Given
        when(voucherService.getVoucherByCode("TEST10")).thenReturn(voucherDTO);

        // When & Then
        mockMvc.perform(get("/api/vouchers/code/TEST10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST10"))
                .andExpect(jsonPath("$.name").value("Test Voucher"));

        verify(voucherService).getVoucherByCode("TEST10");
    }

    @Test
    void testGetVoucherById() throws Exception {
        // Given
        when(voucherService.getVoucherById(1L)).thenReturn(voucherDTO);

        // When & Then
        mockMvc.perform(get("/api/vouchers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST10"))
                .andExpect(jsonPath("$.name").value("Test Voucher"));

        verify(voucherService).getVoucherById(1L);
    }

    @Test
    void testDeleteVoucher() throws Exception {
        // Given
        doNothing().when(voucherService).deleteVoucher(1L);

        // When & Then
        mockMvc.perform(delete("/api/vouchers/1"))
                .andExpect(status().isOk());

        verify(voucherService).deleteVoucher(1L);
    }

    @Test
    void testGetVouchersByCampaign() throws Exception {
        // Given
        List<VoucherDTO> vouchers = Arrays.asList(voucherDTO);
        when(voucherService.getVouchersByCampaign("BIRTHDAY")).thenReturn(vouchers);

        // When & Then
        mockMvc.perform(get("/api/vouchers/campaign/BIRTHDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].campaignType").value("CUSTOM"));

        verify(voucherService).getVouchersByCampaign("BIRTHDAY");
    }

    @Test
    void testSendVouchersToCustomers() throws Exception {
        // Given
        List<Long> customerIds = Arrays.asList(1L, 2L);
        doNothing().when(voucherService).sendVouchersToCustomers(1L, customerIds);

        // When & Then
        mockMvc.perform(post("/api/vouchers/1/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerIds)))
                .andExpect(status().isOk())
                .andExpect(content().string("Vouchers sent to 2 customers successfully"));

        verify(voucherService).sendVouchersToCustomers(1L, customerIds);
    }

    @Test
    void testSendBirthdayVouchers() throws Exception {
        // Given
        doNothing().when(voucherService).sendBirthdayVouchers();

        // When & Then
        mockMvc.perform(post("/api/vouchers/campaign/send-birthday"))
                .andExpect(status().isOk())
                .andExpect(content().string("Birthday vouchers sent successfully"));

        verify(voucherService).sendBirthdayVouchers();
    }

    @Test
    void testSendBlackFridayVouchers() throws Exception {
        // Given
        doNothing().when(voucherService).sendBlackFridayVouchers();

        // When & Then
        mockMvc.perform(post("/api/vouchers/campaign/send-black-friday"))
                .andExpect(status().isOk())
                .andExpect(content().string("Black Friday vouchers sent successfully"));

        verify(voucherService).sendBlackFridayVouchers();
    }

    @Test
    void testGetVoucherStatistics() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<VoucherStatisticsDTO> statistics = Arrays.asList(statisticsDTO);
        when(voucherService.getVoucherStatistics(startDate, endDate)).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/api/vouchers/statistics")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].voucherCode").value("TEST10"))
                .andExpect(jsonPath("$[0].totalUsage").value(50));

        verify(voucherService).getVoucherStatistics(startDate, endDate);
    }

    @Test
    void testGetCampaignStatistics() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<CampaignStatisticsDTO> campaignStatistics = Arrays.asList(campaignStatisticsDTO);
        when(voucherService.getCampaignStatistics(startDate, endDate)).thenReturn(campaignStatistics);

        // When & Then
        mockMvc.perform(get("/api/vouchers/statistics/campaign")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].campaignType").value("BIRTHDAY"))
                .andExpect(jsonPath("$[0].totalVouchers").value(10));

        verify(voucherService).getCampaignStatistics(startDate, endDate);
    }

    @Test
    void testGetVoucherStatisticsById() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        when(voucherService.getVoucherStatisticsById(1L, startDate, endDate)).thenReturn(statisticsDTO);

        // When & Then
        mockMvc.perform(get("/api/vouchers/1/statistics")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voucherCode").value("TEST10"))
                .andExpect(jsonPath("$.totalUsage").value(50));

        verify(voucherService).getVoucherStatisticsById(1L, startDate, endDate);
    }

    @Test
    void testIsValidVoucher() throws Exception {
        // Given
        when(voucherService.isValidVoucher("TEST10")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/vouchers/validate/TEST10"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(voucherService).isValidVoucher("TEST10");
    }

    @Test
    void testCanCustomerUseVoucher() throws Exception {
        // Given
        when(voucherService.canCustomerUseVoucher("TEST10", 1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/vouchers/can-use")
                .param("voucherCode", "TEST10")
                .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(voucherService).canCustomerUseVoucher("TEST10", 1L);
    }

    @Test
    void testSearchVouchers() throws Exception {
        // Given
        List<VoucherDTO> vouchers = Arrays.asList(voucherDTO);
        when(voucherService.searchVouchers("Test")).thenReturn(vouchers);

        // When & Then
        mockMvc.perform(get("/api/vouchers/search")
                .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Voucher"));

        verify(voucherService).searchVouchers("Test");
    }

    @Test
    void testUpdateVoucherStatus() throws Exception {
        // Given
        doNothing().when(voucherService).updateVoucherStatus();

        // When & Then
        mockMvc.perform(post("/api/vouchers/update-status"))
                .andExpect(status().isOk());

        verify(voucherService).updateVoucherStatus();
    }
} 