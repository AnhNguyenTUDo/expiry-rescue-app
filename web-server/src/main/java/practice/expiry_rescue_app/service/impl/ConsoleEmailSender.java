package practice.expiry_rescue_app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.service.EmailSender;

/**
 * Development email sender: logs the OTP to the console instead of sending a real
 * email. Active by default (when {@code app.mail.enabled} is false or unset), so the
 * passwordless flow can be tested end-to-end without any SMTP setup.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "false", matchIfMissing = true)
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void sendOtp(String email, String code) {
        log.info("==================== DEV OTP ====================");
        log.info("  To:   {}", email);
        log.info("  Code: {}", code);
        log.info("================================================");
    }
}
