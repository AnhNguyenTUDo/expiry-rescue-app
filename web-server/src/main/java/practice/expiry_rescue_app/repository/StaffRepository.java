package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.Staff;

import java.util.Optional;

@Repository
public interface StaffRepository extends BaseRepository<Staff> {

    Optional<Staff> findByUsernameAndDeletedAtIsNull(String username);

    Optional<Staff> findByEmailAndDeletedAtIsNull(String email);
}
