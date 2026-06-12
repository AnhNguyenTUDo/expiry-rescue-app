package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.config.security.JwtTokenProvider;
import practice.expiry_rescue_app.entity.OtpCode;
import practice.expiry_rescue_app.entity.User;
import practice.expiry_rescue_app.exception.InvalidCredentialsException;
import practice.expiry_rescue_app.exception.TooManyRequestsException;
import practice.expiry_rescue_app.exception.UnauthorizedException;
import practice.expiry_rescue_app.model.auth.OtpTokenResponse;
import practice.expiry_rescue_app.repository.OtpCodeRepository;
import practice.expiry_rescue_app.repository.UserRepository;
import practice.expiry_rescue_app.service.EmailSender;
import practice.expiry_rescue_app.service.PasswordlessAuthService;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordlessAuthServiceImpl implements PasswordlessAuthService {

    private final OtpCodeRepository otpCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailSender emailSender;

    @Value("${app.otp.length}")
    private int otpLength;

    @Value("${app.otp.ttl-seconds}")
    private long ttlSeconds;

    @Value("${app.otp.max-attempts}")
    private int maxAttempts;

    @Value("${app.otp.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.demo.enabled}")
    private boolean demoEnabled;

    @Value("${app.demo.email}")
    private String demoEmail;

    @Value("${app.demo.code}")
    private String demoCode;

    private final SecureRandom secureRandom = new SecureRandom();

    private static final String GENERIC_INVALID = "Invalid or expired code. Please request a new one.";

    @Override
    @Transactional
    public void requestOtp(String email) {
        String normalizedEmail = normalize(email);

        // Resend cooldown: block if the latest unused code is younger than the cooldown window
        otpCodeRepository.findTopByEmailAndConsumedAtIsNullOrderByCreatedAtDesc(normalizedEmail)
                .ifPresent(latest -> {
                    long ageSeconds = (System.currentTimeMillis() - latest.getCreatedAt()) / 1000;
                    if (ageSeconds < resendCooldownSeconds) {
                        throw new TooManyRequestsException("Please wait before requesting another code.");
                    }
                });

        String code = generateCode();

        OtpCode otpCode = new OtpCode();
        otpCode.setEmail(normalizedEmail);
        otpCode.setCodeHash(passwordEncoder.encode(code));
        otpCode.setExpiresAt(System.currentTimeMillis() + ttlSeconds * 1000);
        otpCode.setAttemptCount(0);
        otpCodeRepository.save(otpCode);

        emailSender.sendOtp(normalizedEmail, code);
        log.info("Issued OTP for {}", normalizedEmail);
    }

    @Override
    // Do not roll back on a failed verification: the attempt-count increment must persist
    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public OtpTokenResponse verifyOtp(String email, String code) {
        String normalizedEmail = normalize(email);

        // Demo mode: the configured demo account accepts a fixed code with no DB-backed OTP.
        // Scoped to the demo email only, so this is never a blanket backdoor.
        if (isDemoLogin(normalizedEmail, code)) {
            return issueTokenFor(normalizedEmail);
        }

        OtpCode otpCode = otpCodeRepository
                .findTopByEmailAndConsumedAtIsNullOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException(GENERIC_INVALID));

        // Expired
        if (System.currentTimeMillis() > otpCode.getExpiresAt()) {
            throw new InvalidCredentialsException(GENERIC_INVALID);
        }

        // Too many failed attempts -> invalidate
        if (otpCode.getAttemptCount() >= maxAttempts) {
            throw new InvalidCredentialsException("Too many incorrect attempts. Please request a new code.");
        }

        // Wrong code -> record the failed attempt
        if (!passwordEncoder.matches(code, otpCode.getCodeHash())) {
            otpCode.setAttemptCount(otpCode.getAttemptCount() + 1);
            otpCodeRepository.save(otpCode);
            throw new InvalidCredentialsException("Incorrect code. Please try again.");
        }

        // Success -> consume the code so it can't be reused
        otpCode.setConsumedAt(System.currentTimeMillis());
        otpCodeRepository.save(otpCode);

        return issueTokenFor(normalizedEmail);
    }

    @Override
    @Transactional
    public OtpTokenResponse demoLogin() {
        if (!demoEnabled) {
            throw new UnauthorizedException("Demo login is not enabled.");
        }
        return issueTokenFor(normalize(demoEmail));
    }

    // True only when demo mode is on and the credentials match the configured demo account.
    private boolean isDemoLogin(String normalizedEmail, String code) {
        return demoEnabled && normalizedEmail.equals(normalize(demoEmail)) && demoCode.equals(code);
    }

    // Find-or-create the user for an email and mint a JWT for them.
    private OtpTokenResponse issueTokenFor(String email) {
        User user = userRepository.findByEmail(email).orElseGet(() -> createUser(email));
        String token = jwtTokenProvider.generateToken(user.getEmail());
        log.info("Passwordless login succeeded for {}", email);
        return new OtpTokenResponse(token, jwtExpiration);
    }

    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // username is unique & required; email is a safe default
        user.setProvider("email");
        user.setIsActive(true);
        return userRepository.save(user);
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    private String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
