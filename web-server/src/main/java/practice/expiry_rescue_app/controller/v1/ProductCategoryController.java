package practice.expiry_rescue_app.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.model.product.CreateProductCategoryRequest;
import practice.expiry_rescue_app.model.product.ProductCategoryResponse;
import practice.expiry_rescue_app.service.ProductCategoryService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> createCategory(@Valid @RequestBody CreateProductCategoryRequest request) {
        log.info("Create product category request received: {}", request.getName());
        ProductCategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product category created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductCategoryResponse>>> getAllCategories() {
        log.info("Get all product categories request received");
        List<ProductCategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> getCategoryById(@PathVariable UUID id) {
        log.info("Get product category by ID request received: {}", id);
        ProductCategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductCategoryRequest request) {
        log.info("Update product category request received for ID: {}", id);
        ProductCategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product category updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        log.info("Delete product category request received for ID: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Product category deleted successfully", null));
    }
}
