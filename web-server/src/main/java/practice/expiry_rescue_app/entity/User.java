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
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(nullable = true)
    private String passwordHash;

    private String fullName;

    @Column(nullable = false)
    private Boolean isActive = true;

    private String provider;
    private String providerId;
    private String profilePictureUrl;
}
