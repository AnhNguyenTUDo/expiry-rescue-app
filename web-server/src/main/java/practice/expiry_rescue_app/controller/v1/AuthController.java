package practice.expiry_rescue_app.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.model.auth.LoginRequest;
import practice.expiry_rescue_app.model.auth.LoginResponse;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for username: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestHeader("Authorization") String token) {
        log.info("Token refresh request received");
        String jwt = token.substring(7); // Remove "Bearer " prefix
        String newToken = authService.refreshToken(jwt);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", newToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request received");
        String jwt = token.substring(7); // Remove "Bearer " prefix
        authService.logout(jwt);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}
