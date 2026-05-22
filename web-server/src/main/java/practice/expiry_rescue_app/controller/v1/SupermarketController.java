package practice.expiry_rescue_app.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.model.supermarket.CreateSupermarketRequest;
import practice.expiry_rescue_app.model.supermarket.SupermarketResponse;
import practice.expiry_rescue_app.model.supermarket.SupermarketWithProductsResponse;
import practice.expiry_rescue_app.model.supermarket.UpdateSupermarketRequest;
import practice.expiry_rescue_app.service.SupermarketService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/supermarkets")
@RequiredArgsConstructor
public class SupermarketController {

    private final SupermarketService supermarketService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupermarketResponse>> createSupermarket(@Valid @RequestBody CreateSupermarketRequest request) {
        log.info("Create supermarket request received: {}", request.getName());
        SupermarketResponse response = supermarketService.createSupermarket(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supermarket created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupermarketResponse>>> getAllSupermarkets() {
        log.info("Get all supermarkets request received");
        List<SupermarketResponse> response = supermarketService.getAllSupermarkets();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SupermarketResponse>>> getAllActiveSupermarkets(
            @RequestParam(required = false) UUID cityId,
            @RequestParam(required = false) UUID districtId) {
        log.info("Get active supermarkets request received: cityId={}, districtId={}", cityId, districtId);
        List<SupermarketResponse> response = supermarketService.getAllActiveSupermarkets(cityId, districtId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupermarketResponse>> getSupermarketById(@PathVariable UUID id) {
        log.info("Get supermarket by ID request received: {}", id);
        SupermarketResponse response = supermarketService.getSupermarketById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/with-products")
    public ResponseEntity<ApiResponse<SupermarketWithProductsResponse>> getSupermarketWithProducts(@PathVariable UUID id) {
        log.info("Get supermarket with products request received for ID: {}", id);
        SupermarketWithProductsResponse response = supermarketService.getSupermarketWithProducts(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupermarketResponse>> updateSupermarket(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupermarketRequest request) {
        log.info("Update supermarket request received for ID: {}", id);
        SupermarketResponse response = supermarketService.updateSupermarket(id, request);
        return ResponseEntity.ok(ApiResponse.success("Supermarket updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupermarket(@PathVariable UUID id) {
        log.info("Delete supermarket request received for ID: {}", id);
        supermarketService.deleteSupermarket(id);
        return ResponseEntity.ok(ApiResponse.success("Supermarket deleted successfully", null));
    }
}
