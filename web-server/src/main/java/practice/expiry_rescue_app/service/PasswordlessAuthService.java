package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.auth.OtpTokenResponse;

public interface PasswordlessAuthService {

    /**
     * Generates a one-time code for the email and sends it (subject to the resend cooldown).
     */
    void requestOtp(String email);

    /**
     * Verifies the code for the email and, on success, returns a JWT for the user
     * (creating the user if they don't exist yet).
     */
    OtpTokenResponse verifyOtp(String email, String code);

    /**
     * Demo-mode shortcut: returns a JWT for the configured demo user without any OTP.
     * Only available when app.demo.enabled is true.
     */
    OtpTokenResponse demoLogin();
}
