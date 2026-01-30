package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.enums.StaffRole;

import java.util.List;
import java.util.UUID;

/**
 * Business layer for Staff - handles core business logic and data access
 */
public interface StaffBusiness {

    // Business logic methods
    Staff createStaff(String username, String email, String phone, String password, StaffRole role, String fullName);

    Staff updateStaff(UUID id, String email, String phone, StaffRole role, String fullName, Boolean isActive);

    Staff getActiveStaffById(UUID id);

    Staff getStaffByUsername(String username);

    List<Staff> getAllActiveStaffs();

    void deactivateStaff(UUID id);

    void activateStaff(UUID id);

    void softDeleteStaff(UUID id);

    void restoreStaff(UUID id);

    // Validation methods
    void validateUniqueUsername(String username);

    void validateUniqueEmail(String email);

    void validateStaffExists(UUID id);
}
