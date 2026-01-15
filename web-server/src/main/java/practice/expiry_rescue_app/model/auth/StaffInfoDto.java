package practice.expiry_rescue_app.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.expiry_rescue_app.entity.enums.StaffRole;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffInfoDto {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private StaffRole role;
}
