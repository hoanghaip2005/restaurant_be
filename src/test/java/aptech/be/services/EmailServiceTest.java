package aptech.be.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Setup mock behavior
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendVerificationCode() {
        // Given
        String to = "test@example.com";
        String code = "123456";

        // When
        emailService.sendVerificationCode(to, code);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // When
        emailService.sendEmail(to, subject, content);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendVoucherEmail() {
        // Given
        String to = "test@example.com";
        String voucherCode = "TEST10";
        String voucherName = "Test Voucher";
        String discountValue = "10%";

        // When
        emailService.sendVoucherEmail(to, voucherCode, voucherName, discountValue);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendVerificationCode_WithNullCode() {
        // Given
        String to = "test@example.com";
        String code = null;

        // When
        emailService.sendVerificationCode(to, code);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WithEmptyContent() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "";

        // When
        emailService.sendEmail(to, subject, content);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendVoucherEmail_WithSpecialCharacters() {
        // Given
        String to = "test@example.com";
        String voucherCode = "TEST-10%";
        String voucherName = "Test Voucher & Special";
        String discountValue = "10% off";

        // When
        emailService.sendVoucherEmail(to, voucherCode, voucherName, discountValue);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
} 