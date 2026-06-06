package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.OtpCode;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends BaseRepository<OtpCode> {

    // Latest still-unused code for an email (used for both verify and resend-cooldown checks)
    Optional<OtpCode> findTopByEmailAndConsumedAtIsNullOrderByCreatedAtDesc(String email);
}
