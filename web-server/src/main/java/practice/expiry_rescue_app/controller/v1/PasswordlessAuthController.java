package practice.expiry_rescue_app.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.model.auth.OtpRequestRequest;
import practice.expiry_rescue_app.model.auth.OtpTokenResponse;
import practice.expiry_rescue_app.model.auth.OtpVerifyRequest;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.service.PasswordlessAuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/passwordless")
@RequiredArgsConstructor
public class PasswordlessAuthController {

    private final PasswordlessAuthService passwordlessAuthService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> requestOtp(@Valid @RequestBody OtpRequestRequest request) {
        log.info("OTP request received for email: {}", request.getEmail());
        passwordlessAuthService.requestOtp(request.getEmail());
        // Generic message regardless of outcome, to avoid leaking whether the email exists
        return ResponseEntity.ok(ApiResponse.success(
                "If that email can sign in, a code has been sent.", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<OtpTokenResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        log.info("OTP verify received for email: {}", request.getEmail());
        OtpTokenResponse response = passwordlessAuthService.verifyOtp(request.getEmail(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
