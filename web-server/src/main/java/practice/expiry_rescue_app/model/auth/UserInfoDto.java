package practice.expiry_rescue_app.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private UUID id;
    private String email;
    private String username;
    private String fullName;
    private String phone;
    private String provider;
    private String profilePictureUrl;
    private Boolean isActive;
}
