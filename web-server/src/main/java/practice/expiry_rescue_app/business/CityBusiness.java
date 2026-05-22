package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.City;
import practice.expiry_rescue_app.entity.District;

import java.util.List;
import java.util.UUID;

public interface CityBusiness {

    List<City> getAllActiveCities();

    City getActiveCityById(UUID id);

    List<District> getActiveDistrictsByCityId(UUID cityId);

    District getActiveDistrictById(UUID id);
}
