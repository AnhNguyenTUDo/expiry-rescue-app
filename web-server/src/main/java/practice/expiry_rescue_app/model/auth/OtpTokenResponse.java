package practice.expiry_rescue_app.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpTokenResponse {
    private String token;
    private Long expiresIn;
}
