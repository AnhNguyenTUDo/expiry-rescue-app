package practice.expiry_rescue_app.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.Supermarket;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupermarketRepository extends BaseRepository<Supermarket> {

    List<Supermarket> findByIsActiveTrueAndDeletedAtIsNull();

    @Query("SELECT s FROM Supermarket s " +
           "WHERE s.isActive = true AND s.deletedAt IS NULL " +
           "AND (:cityId IS NULL OR s.district.city.id = :cityId) " +
           "AND (:districtId IS NULL OR s.district.id = :districtId)")
    List<Supermarket> findActiveByCityAndDistrict(
            @Param("cityId") UUID cityId,
            @Param("districtId") UUID districtId);
}
