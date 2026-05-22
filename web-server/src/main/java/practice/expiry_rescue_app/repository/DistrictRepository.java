package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.District;

import java.util.List;
import java.util.UUID;

@Repository
public interface DistrictRepository extends BaseRepository<District> {

    List<District> findByCityIdAndIsActiveTrueAndDeletedAtIsNullOrderByName(UUID cityId);
}
