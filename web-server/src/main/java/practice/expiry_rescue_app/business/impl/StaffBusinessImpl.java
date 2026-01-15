package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.StaffBusiness;
import practice.expiry_rescue_app.exception.DuplicateResourceException;
import practice.expiry_rescue_app.exception.ResourceNotFoundException;
import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.entity.enums.StaffRole;
import practice.expiry_rescue_app.repository.StaffRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffBusinessImpl implements StaffBusiness {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Staff createStaff(String username, String email, String phone, String password, StaffRole role, String fullName) {
        log.debug("Creating staff with username: {}", username);

        // Validate uniqueness
        validateUniqueUsername(username);
        if (email != null && !email.isBlank()) {
            validateUniqueEmail(email);
        }

        // Create staff entity
        Staff staff = new Staff();
        staff.setUsername(username);
        staff.setEmail(email);
        staff.setPhone(phone);
        staff.setPasswordHash(passwordEncoder.encode(password));
        staff.setRole(role);
        staff.setFullName(fullName);
        staff.setIsActive(true);

        Staff savedStaff = staffRepository.save(staff);
        log.info("Staff created successfully with ID: {}", savedStaff.getId());

        return savedStaff;
    }

    @Override
    @Transactional
    public Staff updateStaff(UUID id, String email, String phone, StaffRole role, String fullName, Boolean isActive) {
        log.debug("Updating staff with ID: {}", id);

        Staff staff = getActiveStaffById(id);

        // Validate email uniqueness if changed
        if (email != null && !email.equals(staff.getEmail())) {
            validateUniqueEmail(email);
            staff.setEmail(email);
        }

        if (phone != null) {
            staff.setPhone(phone);
        }

        if (role != null) {
            staff.setRole(role);
        }

        if (fullName != null) {
            staff.setFullName(fullName);
        }

        if (isActive != null) {
            staff.setIsActive(isActive);
        }

        Staff updatedStaff = staffRepository.save(staff);
        log.info("Staff updated successfully with ID: {}", updatedStaff.getId());

        return updatedStaff;
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getActiveStaffById(UUID id) {
        return staffRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getStaffByUsername(String username) {
        return staffRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "username", username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> getAllActiveStaffs() {
        return staffRepository.findAllActive();
    }

    @Override
    @Transactional
    public void deactivateStaff(UUID id) {
        log.debug("Deactivating staff with ID: {}", id);
        Staff staff = getActiveStaffById(id);
        staff.setIsActive(false);
        staffRepository.save(staff);
        log.info("Staff deactivated successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void activateStaff(UUID id) {
        log.debug("Activating staff with ID: {}", id);
        Staff staff = getActiveStaffById(id);
        staff.setIsActive(true);
        staffRepository.save(staff);
        log.info("Staff activated successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void softDeleteStaff(UUID id) {
        log.debug("Soft deleting staff with ID: {}", id);
        Staff staff = getActiveStaffById(id);
        staff.setDeletedAt(new Date().getTime());
        staffRepository.save(staff);
        log.info("Staff soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void restoreStaff(UUID id) {
        log.debug("Restoring staff with ID: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        staff.setDeletedAt(null);
        staffRepository.save(staff);
        log.info("Staff restored successfully with ID: {}", id);
    }

    @Override
    public void validateUniqueUsername(String username) {
        if (staffRepository.findByUsernameAndDeletedAtIsNull(username).isPresent()) {
            throw new DuplicateResourceException("Staff", "username", username);
        }
    }

    @Override
    public void validateUniqueEmail(String email) {
        if (staffRepository.findByEmailAndDeletedAtIsNull(email).isPresent()) {
            throw new DuplicateResourceException("Staff", "email", email);
        }
    }

    @Override
    public void validateStaffExists(UUID id) {
        if (!staffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Staff", "id", id);
        }
    }
}
