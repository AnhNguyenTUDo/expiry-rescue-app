package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.service.EmailSender;

/**
 * Production email sender: delivers the OTP via SMTP. Active only when
 * {@code app.mail.enabled=true}; the SMTP connection is configured through the
 * {@code spring.mail.*} properties.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Override
    public void sendOtp(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Your Expiry Rescue sign-in code");
        message.setText(
                "Your sign-in code is: " + code
                        + "\n\nIt expires in 10 minutes."
                        + "\nIf you didn't request this, you can safely ignore this email.");
        mailSender.send(message);
        log.info("Sent OTP email to {}", email);
    }
}
