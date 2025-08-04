package aptech.be.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
    
    public void sendVoucherEmail(String to, String voucherCode, String voucherName, String discountValue) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("New Voucher Available!");
        message.setText("Congratulations! You have received a new voucher:\n\n" +
                       "Voucher Code: " + voucherCode + "\n" +
                       "Voucher Name: " + voucherName + "\n" +
                       "Discount: " + discountValue + "\n\n" +
                       "Use this voucher on your next purchase!");
        mailSender.send(message);
    }
}
