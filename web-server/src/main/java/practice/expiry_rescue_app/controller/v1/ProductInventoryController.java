package practice.expiry_rescue_app.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.business.StaffBusiness;
import practice.expiry_rescue_app.model.common.ApiResponse;
import practice.expiry_rescue_app.model.product.CreateProductInventoryRequest;
import practice.expiry_rescue_app.model.product.ProductInventoryResponse;
import practice.expiry_rescue_app.model.product.UpdateProductInventoryRequest;
import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.entity.enums.InventoryStatus;
import practice.expiry_rescue_app.service.ProductInventoryService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products/inventory")
@RequiredArgsConstructor
public class ProductInventoryController {

    private final ProductInventoryService inventoryService;
    private final StaffBusiness staffBusiness;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductInventoryResponse>> createInventory(
            @Valid @RequestBody CreateProductInventoryRequest request,
            Authentication authentication) {
        log.info("Create product inventory request received for product: {}", request.getProductMasterId());

        // Get current logged-in staff
        String username = authentication.getName();
        Staff staff = staffBusiness.getStaffByUsername(username);

        ProductInventoryResponse response = inventoryService.createInventory(request, staff.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product inventory created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductInventoryResponse>>> getAllInventories() {
        log.info("Get all product inventories request received");
        List<ProductInventoryResponse> response = inventoryService.getAllInventories();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductInventoryResponse>> getInventoryById(@PathVariable UUID id) {
        log.info("Get product inventory by ID request received: {}", id);
        ProductInventoryResponse response = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/supermarket/{supermarketId}")
    public ResponseEntity<ApiResponse<List<ProductInventoryResponse>>> getInventoriesBySupermarket(@PathVariable UUID supermarketId) {
        log.info("Get inventories by supermarket ID request received: {}", supermarketId);
        List<ProductInventoryResponse> response = inventoryService.getInventoriesBySupermarket(supermarketId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/product-master/{productMasterId}")
    public ResponseEntity<ApiResponse<List<ProductInventoryResponse>>> getInventoriesByProductMaster(@PathVariable UUID productMasterId) {
        log.info("Get inventories by product master ID request received: {}", productMasterId);
        List<ProductInventoryResponse> response = inventoryService.getInventoriesByProductMaster(productMasterId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ProductInventoryResponse>>> getInventoriesByStatus(@PathVariable InventoryStatus status) {
        log.info("Get inventories by status request received: {}", status);
        List<ProductInventoryResponse> response = inventoryService.getInventoriesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/expiring")
    public ResponseEntity<ApiResponse<List<ProductInventoryResponse>>> getExpiringInventories(
            @RequestParam(defaultValue = "7") Integer daysBeforeExpiry) {
        log.info("Get expiring inventories request received for {} days", daysBeforeExpiry);
        List<ProductInventoryResponse> response = inventoryService.getExpiringInventories(daysBeforeExpiry);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductInventoryResponse>> updateInventory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductInventoryRequest request) {
        log.info("Update product inventory request received for ID: {}", id);
        ProductInventoryResponse response = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product inventory updated successfully", response));
    }

    @PutMapping("/{id}/mark-unavailable")
    public ResponseEntity<ApiResponse<Void>> markAsNotAvailable(@PathVariable UUID id) {
        log.info("Mark inventory as not available request received for ID: {}", id);
        inventoryService.markAsNotAvailable(id);
        return ResponseEntity.ok(ApiResponse.success("Product inventory marked as not available", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable UUID id) {
        log.info("Delete product inventory request received for ID: {}", id);
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok(ApiResponse.success("Product inventory deleted successfully", null));
    }
}
