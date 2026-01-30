package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.product.CreateProductInventoryRequest;
import practice.expiry_rescue_app.model.product.ProductInventoryResponse;
import practice.expiry_rescue_app.model.product.UpdateProductInventoryRequest;
import practice.expiry_rescue_app.enums.InventoryStatus;

import java.util.List;
import java.util.UUID;

public interface ProductInventoryService {

    ProductInventoryResponse createInventory(CreateProductInventoryRequest request, UUID createdById);

    ProductInventoryResponse updateInventory(UUID id, UpdateProductInventoryRequest request);

    ProductInventoryResponse getInventoryById(UUID id);

    List<ProductInventoryResponse> getAllInventories();

    List<ProductInventoryResponse> getInventoriesByStatus(InventoryStatus status);

    List<ProductInventoryResponse> getInventoriesBySupermarket(UUID supermarketId);

    List<ProductInventoryResponse> getInventoriesByProductMaster(UUID productMasterId);

    List<ProductInventoryResponse> getExpiringInventories(Integer daysBeforeExpiry);

    void deleteInventory(UUID id);

    void restoreInventory(UUID id);

    void markAsNotAvailable(UUID id);
}
