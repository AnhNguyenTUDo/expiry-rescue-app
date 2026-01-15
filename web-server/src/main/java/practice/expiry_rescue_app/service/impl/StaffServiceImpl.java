package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.business.StaffBusiness;
import practice.expiry_rescue_app.model.staff.CreateStaffRequest;
import practice.expiry_rescue_app.model.staff.StaffResponse;
import practice.expiry_rescue_app.model.staff.UpdateStaffRequest;
import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.service.StaffService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffBusiness staffBusiness;

    @Override
    public StaffResponse createStaff(CreateStaffRequest request) {
        log.debug("Service: Creating staff with username: {}", request.getUsername());

        Staff staff = staffBusiness.createStaff(
                request.getUsername(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getRole(),
                request.getFullName()
        );

        return mapToResponse(staff);
    }

    @Override
    public StaffResponse updateStaff(UUID id, UpdateStaffRequest request) {
        log.debug("Service: Updating staff with ID: {}", id);

        Staff staff = staffBusiness.updateStaff(
                id,
                request.getEmail(),
                request.getPhone(),
                request.getRole(),
                request.getFullName(),
                request.getIsActive()
        );

        return mapToResponse(staff);
    }

    @Override
    public StaffResponse getStaffById(UUID id) {
        log.debug("Service: Getting staff by ID: {}", id);
        Staff staff = staffBusiness.getActiveStaffById(id);
        return mapToResponse(staff);
    }

    @Override
    public List<StaffResponse> getAllStaffs() {
        log.debug("Service: Getting all active staffs");
        List<Staff> staffs = staffBusiness.getAllActiveStaffs();
        return staffs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStaff(UUID id) {
        log.debug("Service: Deleting staff with ID: {}", id);
        staffBusiness.softDeleteStaff(id);
    }

    @Override
    public void restoreStaff(UUID id) {
        log.debug("Service: Restoring staff with ID: {}", id);
        staffBusiness.restoreStaff(id);
    }

    private StaffResponse mapToResponse(Staff staff) {
        StaffResponse response = new StaffResponse();
        response.setId(staff.getId());
        response.setUsername(staff.getUsername());
        response.setEmail(staff.getEmail());
        response.setPhone(staff.getPhone());
        response.setRole(staff.getRole());
        response.setFullName(staff.getFullName());
        response.setIsActive(staff.getIsActive());
        response.setCreatedAt(staff.getCreatedAt());
        response.setUpdatedAt(staff.getUpdatedAt());
        return response;
    }
}
