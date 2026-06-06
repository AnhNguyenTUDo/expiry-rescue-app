package practice.expiry_rescue_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp_codes", indexes = {
        @Index(name = "idx_otp_codes_email", columnList = "email")
})
public class OtpCode extends BaseEntity {

    @Column(nullable = false)
    private String email;

    // BCrypt hash of the OTP code - never store the plaintext code
    @Column(nullable = false)
    private String codeHash;

    // Epoch millis after which the code is no longer valid
    @Column(nullable = false)
    private Long expiresAt;

    // Epoch millis when the code was successfully used; null while still unused
    private Long consumedAt;

    // Number of failed verification attempts against this code
    @Column(nullable = false)
    private int attemptCount = 0;
}
