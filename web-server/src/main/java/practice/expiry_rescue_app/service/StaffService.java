package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.staff.CreateStaffRequest;
import practice.expiry_rescue_app.model.staff.StaffResponse;
import practice.expiry_rescue_app.model.staff.UpdateStaffRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for Staff - handles orchestration and DTO mapping
 */
public interface StaffService {

    StaffResponse createStaff(CreateStaffRequest request);

    StaffResponse updateStaff(UUID id, UpdateStaffRequest request);

    StaffResponse getStaffById(UUID id);

    List<StaffResponse> getAllStaffs();

    void deleteStaff(UUID id);

    void restoreStaff(UUID id);
}
