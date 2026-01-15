package practice.expiry_rescue_app.model.staff;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.expiry_rescue_app.entity.enums.StaffRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStaffRequest {

    @Email(message = "Email should be valid")
    private String email;

    private String phone;

    private StaffRole role;

    private String fullName;

    private Boolean isActive;
}
