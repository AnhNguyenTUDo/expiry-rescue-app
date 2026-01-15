package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.business.ProductInventoryBusiness;
import practice.expiry_rescue_app.model.product.CreateProductInventoryRequest;
import practice.expiry_rescue_app.model.product.ProductInventoryResponse;
import practice.expiry_rescue_app.model.product.UpdateProductInventoryRequest;
import practice.expiry_rescue_app.entity.ProductInventory;
import practice.expiry_rescue_app.entity.enums.InventoryStatus;
import practice.expiry_rescue_app.service.ProductInventoryService;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private final ProductInventoryBusiness inventoryBusiness;

    @Override
    public ProductInventoryResponse createInventory(CreateProductInventoryRequest request, UUID createdById) {
        log.debug("Service: Creating product inventory for product: {}", request.getProductMasterId());

        ProductInventory inventory = inventoryBusiness.createInventory(
                request.getProductMasterId(),
                request.getSupermarketId(),
                request.getOriginalPrice(),
                request.getSellingPrice(),
                request.getQuantityAvailable(),
                request.getExpiryDate(),
                createdById
        );

        return mapToResponse(inventory);
    }

    @Override
    public ProductInventoryResponse updateInventory(UUID id, UpdateProductInventoryRequest request) {
        log.debug("Service: Updating product inventory with ID: {}", id);

        ProductInventory inventory = inventoryBusiness.updateInventory(
                id,
                request.getOriginalPrice(),
                request.getSellingPrice(),
                request.getQuantityAvailable(),
                request.getExpiryDate(),
                request.getStatus()
        );

        return mapToResponse(inventory);
    }

    @Override
    public ProductInventoryResponse getInventoryById(UUID id) {
        log.debug("Service: Getting product inventory by ID: {}", id);
        ProductInventory inventory = inventoryBusiness.getActiveInventoryById(id);
        return mapToResponse(inventory);
    }

    @Override
    public List<ProductInventoryResponse> getAllInventories() {
        log.debug("Service: Getting all product inventories");
        List<ProductInventory> inventories = inventoryBusiness.getAllActiveInventories();
        return inventories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductInventoryResponse> getInventoriesByStatus(InventoryStatus status) {
        log.debug("Service: Getting inventories by status: {}", status);
        List<ProductInventory> inventories = inventoryBusiness.getInventoriesByStatus(status);
        return inventories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductInventoryResponse> getInventoriesBySupermarket(UUID supermarketId) {
        log.debug("Service: Getting inventories by supermarket ID: {}", supermarketId);
        List<ProductInventory> inventories = inventoryBusiness.getInventoriesBySupermarket(supermarketId);
        return inventories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductInventoryResponse> getInventoriesByProductMaster(UUID productMasterId) {
        log.debug("Service: Getting inventories by product master ID: {}", productMasterId);
        List<ProductInventory> inventories = inventoryBusiness.getInventoriesByProductMaster(productMasterId);
        return inventories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductInventoryResponse> getExpiringInventories(Integer daysBeforeExpiry) {
        log.debug("Service: Getting inventories expiring within {} days", daysBeforeExpiry);
        long expiryDate = new Date().getTime() + (daysBeforeExpiry * 24L * 60 * 60 * 1000);
        List<ProductInventory> inventories = inventoryBusiness.getExpiringInventories(expiryDate);
        return inventories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteInventory(UUID id) {
        log.debug("Service: Deleting product inventory with ID: {}", id);
        inventoryBusiness.softDeleteInventory(id);
    }

    @Override
    public void restoreInventory(UUID id) {
        log.debug("Service: Restoring product inventory with ID: {}", id);
        inventoryBusiness.restoreInventory(id);
    }

    @Override
    public void markAsNotAvailable(UUID id) {
        log.debug("Service: Marking inventory as not available with ID: {}", id);
        inventoryBusiness.markAsNotAvailable(id);
    }

    private ProductInventoryResponse mapToResponse(ProductInventory inventory) {
        ProductInventoryResponse response = new ProductInventoryResponse();
        response.setId(inventory.getId());
        response.setProductMasterId(inventory.getProductMaster().getId());
        response.setProductName(inventory.getProductMaster().getName());
        response.setProductDescription(inventory.getProductMaster().getDescription());
        response.setCategoryId(inventory.getProductMaster().getCategory().getId());
        response.setCategoryName(inventory.getProductMaster().getCategory().getName());
        response.setSupermarketId(inventory.getSupermarket().getId());
        response.setSupermarketName(inventory.getSupermarket().getName());
        response.setOriginalPrice(inventory.getOriginalPrice());
        response.setSellingPrice(inventory.getSellingPrice());
        response.setQuantityAvailable(inventory.getQuantityAvailable());
        response.setExpiryDate(inventory.getExpiryDate());
        response.setStatus(inventory.getStatus());
        if (inventory.getCreatedBy() != null) {
            response.setCreatedById(inventory.getCreatedBy().getId());
            response.setCreatedByName(inventory.getCreatedBy().getFullName());
        }
        response.setCreatedAt(inventory.getCreatedAt());
        response.setUpdatedAt(inventory.getUpdatedAt());
        return response;
    }
}
