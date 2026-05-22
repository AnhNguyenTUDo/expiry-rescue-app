package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.City;

import java.util.List;

@Repository
public interface CityRepository extends BaseRepository<City> {

    List<City> findByIsActiveTrueAndDeletedAtIsNullOrderByName();
}
