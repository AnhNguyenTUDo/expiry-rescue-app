package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.AuthBusiness;
import practice.expiry_rescue_app.business.StaffBusiness;
import practice.expiry_rescue_app.exception.InvalidCredentialsException;
import practice.expiry_rescue_app.exception.UnauthorizedException;
import practice.expiry_rescue_app.entity.Staff;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthBusinessImpl implements AuthBusiness {

    private final StaffBusiness staffBusiness;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Staff authenticateStaff(String username, String password) {
        log.debug("Authenticating staff: {}", username);

        Staff staff = staffBusiness.getStaffByUsername(username);

        // Verify password
        if (!passwordEncoder.matches(password, staff.getPasswordHash())) {
            log.warn("Invalid password attempt for username: {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Validate staff is active
        validateStaffIsActive(staff);

        log.info("Staff authenticated successfully: {}", username);
        return staff;
    }

    @Override
    public void validateStaffIsActive(Staff staff) {
        if (!staff.getIsActive()) {
            log.warn("Inactive staff attempted to login: {}", staff.getUsername());
            throw new UnauthorizedException("Staff account is inactive");
        }
    }
}
