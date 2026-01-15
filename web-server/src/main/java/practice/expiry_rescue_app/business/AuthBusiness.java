package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.Staff;

/**
 * Business layer for Authentication - handles core authentication business logic
 */
public interface AuthBusiness {

    Staff authenticateStaff(String username, String password);

    void validateStaffIsActive(Staff staff);
}
