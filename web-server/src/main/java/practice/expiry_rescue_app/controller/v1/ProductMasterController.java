package practice.expiry_rescue_app.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.model.product.CreateProductMasterRequest;
import practice.expiry_rescue_app.model.product.ProductMasterResponse;
import practice.expiry_rescue_app.service.ProductMasterService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products/master")
@RequiredArgsConstructor
public class ProductMasterController {

    private final ProductMasterService productMasterService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductMasterResponse>> createProductMaster(
            @Valid @RequestBody CreateProductMasterRequest request) {
        log.info("Create product master request received: {}", request.getName());
        ProductMasterResponse response = productMasterService.createProductMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product master created successfully", response));
    }

    @PostMapping(value = "/with-image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<ProductMasterResponse>> createProductMasterWithImage(
            @Valid @RequestPart("request") CreateProductMasterRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        log.info("Create product master with image request received: {}", request.getName());
        ProductMasterResponse response = productMasterService.createProductMasterWithImage(request, image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product master created successfully", response));
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<ApiResponse<ProductMasterResponse>> uploadProductImage(
            @PathVariable UUID id,
            @RequestParam("image") MultipartFile image) {
        log.info("Upload product image request received for ID: {}", id);
        ProductMasterResponse response = productMasterService.uploadProductImage(id, image);
        return ResponseEntity.ok(ApiResponse.success("Product image uploaded successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductMasterResponse>>> getAllProductMasters() {
        log.info("Get all product masters request received");
        List<ProductMasterResponse> response = productMasterService.getAllProductMasters();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductMasterResponse>> getProductMasterById(@PathVariable UUID id) {
        log.info("Get product master by ID request received: {}", id);
        ProductMasterResponse response = productMasterService.getProductMasterById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductMasterResponse>>> getProductMastersByCategory(@PathVariable UUID categoryId) {
        log.info("Get product masters by category ID request received: {}", categoryId);
        List<ProductMasterResponse> response = productMasterService.getProductMastersByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductMasterResponse>> updateProductMaster(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductMasterRequest request) {
        log.info("Update product master request received for ID: {}", id);
        ProductMasterResponse response = productMasterService.updateProductMaster(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product master updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductMaster(@PathVariable UUID id) {
        log.info("Delete product master request received for ID: {}", id);
        productMasterService.deleteProductMaster(id);
        return ResponseEntity.ok(ApiResponse.success("Product master deleted successfully", null));
    }
}
