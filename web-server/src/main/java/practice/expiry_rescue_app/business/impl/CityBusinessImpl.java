package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.CityBusiness;
import practice.expiry_rescue_app.entity.City;
import practice.expiry_rescue_app.entity.District;
import practice.expiry_rescue_app.exception.ResourceNotFoundException;
import practice.expiry_rescue_app.repository.CityRepository;
import practice.expiry_rescue_app.repository.DistrictRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CityBusinessImpl implements CityBusiness {

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;

    @Override
    @Transactional(readOnly = true)
    public List<City> getAllActiveCities() {
        return cityRepository.findByIsActiveTrueAndDeletedAtIsNullOrderByName();
    }

    @Override
    @Transactional(readOnly = true)
    public City getActiveCityById(UUID id) {
        return cityRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<District> getActiveDistrictsByCityId(UUID cityId) {
        getActiveCityById(cityId);
        return districtRepository.findByCityIdAndIsActiveTrueAndDeletedAtIsNullOrderByName(cityId);
    }

    @Override
    @Transactional(readOnly = true)
    public District getActiveDistrictById(UUID id) {
        return districtRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", id));
    }
}
