package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.ProductInventory;
import practice.expiry_rescue_app.enums.InventoryStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductInventoryBusiness {

    ProductInventory createInventory(UUID productMasterId, UUID supermarketId, BigDecimal originalPrice,
                                     BigDecimal sellingPrice, Integer quantityAvailable, Long expiryDate, UUID createdById);

    ProductInventory updateInventory(UUID id, BigDecimal originalPrice, BigDecimal sellingPrice,
                                     Integer quantityAvailable, Long expiryDate, InventoryStatus status);

    ProductInventory getActiveInventoryById(UUID id);

    List<ProductInventory> getAllActiveInventories();

    List<ProductInventory> getInventoriesByStatus(InventoryStatus status);

    List<ProductInventory> getInventoriesBySupermarket(UUID supermarketId);

    List<ProductInventory> getInventoriesByProductMaster(UUID productMasterId);

    List<ProductInventory> getExpiringInventories(Long beforeDate);

    void softDeleteInventory(UUID id);

    void restoreInventory(UUID id);

    void validateInventoryExists(UUID id);

    // Business logic
    void validateDiscountPrice(BigDecimal originalPrice, BigDecimal sellingPrice);

    void updateInventoryStatus(UUID id, InventoryStatus status);

    void markAsNotAvailable(UUID id);
}
