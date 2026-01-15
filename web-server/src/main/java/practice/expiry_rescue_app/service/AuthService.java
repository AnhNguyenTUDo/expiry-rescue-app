package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.auth.LoginRequest;
import practice.expiry_rescue_app.model.auth.LoginResponse;

/**
 * Service layer for Authentication - handles orchestration and DTO mapping
 */
public interface AuthService {

    LoginResponse login(LoginRequest request);

    String generateToken(String username);

    String refreshToken(String token);

    void logout(String token);
}
