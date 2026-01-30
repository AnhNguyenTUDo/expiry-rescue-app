package practice.expiry_rescue_app.model.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.expiry_rescue_app.enums.StaffRole;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private UUID id;
    private String username;
    private String email;
    private String phone;
    private StaffRole role;
    private String fullName;
    private Boolean isActive;
    private Long createdAt;
    private Long updatedAt;
}
