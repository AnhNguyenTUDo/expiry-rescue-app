package practice.expiry_rescue_app.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.model.city.CityResponse;
import practice.expiry_rescue_app.model.city.DistrictResponse;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.service.CityService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CityResponse>>> getAllCities() {
        log.info("Get all cities request received");
        List<CityResponse> response = cityService.getAllActiveCities();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{cityId}/districts")
    public ResponseEntity<ApiResponse<List<DistrictResponse>>> getDistrictsByCity(@PathVariable UUID cityId) {
        log.info("Get districts request received for city: {}", cityId);
        List<DistrictResponse> response = cityService.getDistrictsByCityId(cityId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
