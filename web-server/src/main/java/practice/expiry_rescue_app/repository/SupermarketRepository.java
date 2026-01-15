package practice.expiry_rescue_app.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.Supermarket;

import java.util.List;

@Repository
public interface SupermarketRepository extends BaseRepository<Supermarket> {

    List<Supermarket> findByIsActiveTrueAndDeletedAtIsNull();

    @Query("SELECT DISTINCT s.district FROM Supermarket s WHERE s.district IS NOT NULL AND s.deletedAt IS NULL ORDER BY s.district")
    List<String> findDistinctDistricts();
}
