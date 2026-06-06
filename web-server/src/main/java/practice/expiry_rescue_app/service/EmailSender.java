package practice.expiry_rescue_app.service;

/**
 * Sends transactional emails. Implementation is selected at runtime via the
 * {@code app.mail.enabled} property:
 * <ul>
 *   <li>{@code false} (default) -> {@code ConsoleEmailSender} (logs to console, no SMTP)</li>
 *   <li>{@code true} -> {@code SmtpEmailSender} (real delivery via SMTP)</li>
 * </ul>
 */
public interface EmailSender {

    void sendOtp(String email, String code);
}
