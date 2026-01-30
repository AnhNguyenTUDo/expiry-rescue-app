package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.business.AuthBusiness;
import practice.expiry_rescue_app.config.JwtUtil;
import practice.expiry_rescue_app.model.auth.LoginRequest;
import practice.expiry_rescue_app.model.auth.LoginResponse;
import practice.expiry_rescue_app.model.auth.StaffInfoDto;
import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.entity.User;
import practice.expiry_rescue_app.repository.UserRepository;
import practice.expiry_rescue_app.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthBusiness authBusiness;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.debug("Processing login request for username: {}", request.getUsername());

        // Authenticate via business layer
        Staff staff = authBusiness.authenticateStaff(request.getUsername(), request.getPassword());

        // Generate JWT token
        String token = jwtUtil.generateToken(staff.getUsername(), staff.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(staff.getUsername());

        // Map to DTO
        StaffInfoDto staffInfo = mapToStaffInfoDto(staff);

        log.info("Login successful for username: {}", request.getUsername());

        return new LoginResponse(token, refreshToken, jwtExpiration, staffInfo);
    }

    @Override
    public String generateToken(String username) {
        log.debug("Generating token for username: {}", username);
        return jwtUtil.generateToken(username, null);
    }

    @Override
    public String refreshToken(String token) {
        log.debug("Refreshing token");
        String username = jwtUtil.extractUsername(token);
        return jwtUtil.generateToken(username, null);
    }

    @Override
    public void logout(String token) {
        log.debug("Processing logout");
        // Since JWT is stateless, we don't need to do anything here
        // In production, you might want to blacklist the token
        log.info("Logout successful");
    }

    @Override
    public Object getCurrentUserDetails(String email) {
        log.debug("Fetching user details for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", user.getId());
        userDetails.put("email", user.getEmail());
        userDetails.put("username", user.getUsername());
        userDetails.put("fullName", user.getFullName());
        userDetails.put("profilePictureUrl", user.getProfilePictureUrl());
        userDetails.put("provider", user.getProvider());

        log.info("User details retrieved successfully for email: {}", email);
        return userDetails;
    }

    private StaffInfoDto mapToStaffInfoDto(Staff staff) {
        StaffInfoDto dto = new StaffInfoDto();
        dto.setId(staff.getId());
        dto.setUsername(staff.getUsername());
        dto.setEmail(staff.getEmail());
        dto.setFullName(staff.getFullName());
        dto.setRole(staff.getRole());
        return dto;
    }
}
