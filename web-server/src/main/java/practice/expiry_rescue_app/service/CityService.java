package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.city.CityResponse;
import practice.expiry_rescue_app.model.city.DistrictResponse;

import java.util.List;
import java.util.UUID;

public interface CityService {

    List<CityResponse> getAllActiveCities();

    List<DistrictResponse> getDistrictsByCityId(UUID cityId);
}
