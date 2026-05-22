package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.business.CityBusiness;
import practice.expiry_rescue_app.entity.City;
import practice.expiry_rescue_app.entity.District;
import practice.expiry_rescue_app.model.city.CityResponse;
import practice.expiry_rescue_app.model.city.DistrictResponse;
import practice.expiry_rescue_app.service.CityService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityBusiness cityBusiness;

    @Override
    public List<CityResponse> getAllActiveCities() {
        log.debug("Service: Getting all active cities");
        return cityBusiness.getAllActiveCities().stream()
                .map(this::toCityResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DistrictResponse> getDistrictsByCityId(UUID cityId) {
        log.debug("Service: Getting districts for city ID: {}", cityId);
        return cityBusiness.getActiveDistrictsByCityId(cityId).stream()
                .map(this::toDistrictResponse)
                .collect(Collectors.toList());
    }

    private CityResponse toCityResponse(City city) {
        return new CityResponse(city.getId(), city.getName(), city.getIsActive());
    }

    private DistrictResponse toDistrictResponse(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getName(),
                district.getCity().getId(),
                district.getCity().getName(),
                district.getIsActive()
        );
    }
}
