package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.ProductInventoryBusiness;
import practice.expiry_rescue_app.business.ProductMasterBusiness;
import practice.expiry_rescue_app.business.StaffBusiness;
import practice.expiry_rescue_app.business.SupermarketBusiness;
import practice.expiry_rescue_app.exception.ResourceNotFoundException;
import practice.expiry_rescue_app.entity.ProductInventory;
import practice.expiry_rescue_app.entity.ProductMaster;
import practice.expiry_rescue_app.entity.Staff;
import practice.expiry_rescue_app.entity.Supermarket;
import practice.expiry_rescue_app.entity.enums.InventoryStatus;
import practice.expiry_rescue_app.repository.ProductInventoryRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductInventoryBusinessImpl implements ProductInventoryBusiness {

    private final ProductInventoryRepository inventoryRepository;
    private final ProductMasterBusiness productMasterBusiness;
    private final SupermarketBusiness supermarketBusiness;
    private final StaffBusiness staffBusiness;

    @Override
    @Transactional
    public ProductInventory createInventory(UUID productMasterId, UUID supermarketId, BigDecimal originalPrice,
                                            BigDecimal sellingPrice, Integer quantityAvailable, Long expiryDate, UUID createdById) {
        log.debug("Creating product inventory for product: {} at supermarket: {}", productMasterId, supermarketId);

        // Validate dependencies
        ProductMaster productMaster = productMasterBusiness.getActiveProductMasterById(productMasterId);
        Supermarket supermarket = supermarketBusiness.getActiveSupermarketById(supermarketId);
        Staff createdBy = staffBusiness.getActiveStaffById(createdById);

        // Validate business rules
        validateDiscountPrice(originalPrice, sellingPrice);

        ProductInventory inventory = new ProductInventory();
        inventory.setProductMaster(productMaster);
        inventory.setSupermarket(supermarket);
        inventory.setOriginalPrice(originalPrice);
        inventory.setSellingPrice(sellingPrice);
        inventory.setQuantityAvailable(quantityAvailable);
        inventory.setExpiryDate(expiryDate);
        inventory.setStatus(InventoryStatus.AVAILABLE);
        inventory.setCreatedBy(createdBy);

        ProductInventory saved = inventoryRepository.save(inventory);
        log.info("Product inventory created successfully with ID: {}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public ProductInventory updateInventory(UUID id, BigDecimal originalPrice, BigDecimal sellingPrice,
                                            Integer quantityAvailable, Long expiryDate, InventoryStatus status) {
        log.debug("Updating product inventory with ID: {}", id);

        ProductInventory inventory = getActiveInventoryById(id);

        if (originalPrice != null && sellingPrice != null) {
            validateDiscountPrice(originalPrice, sellingPrice);
            inventory.setOriginalPrice(originalPrice);
            inventory.setSellingPrice(sellingPrice);
        } else if (originalPrice != null) {
            validateDiscountPrice(originalPrice, inventory.getSellingPrice());
            inventory.setOriginalPrice(originalPrice);
        } else if (sellingPrice != null) {
            validateDiscountPrice(inventory.getOriginalPrice(), sellingPrice);
            inventory.setSellingPrice(sellingPrice);
        }

        if (quantityAvailable != null) {
            inventory.setQuantityAvailable(quantityAvailable);
            // Auto mark as not available if quantity is 0
            if (quantityAvailable == 0) {
                inventory.setStatus(InventoryStatus.NOT_AVAILABLE);
            }
        }

        if (expiryDate != null) inventory.setExpiryDate(expiryDate);
        if (status != null) inventory.setStatus(status);

        ProductInventory updated = inventoryRepository.save(inventory);
        log.info("Product inventory updated successfully with ID: {}", updated.getId());

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInventory getActiveInventoryById(UUID id) {
        return inventoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductInventory", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInventory> getAllActiveInventories() {
        return inventoryRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInventory> getInventoriesByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInventory> getInventoriesBySupermarket(UUID supermarketId) {
        supermarketBusiness.validateSupermarketExists(supermarketId);
        return inventoryRepository.findBySupermarketIdAndDeletedAtIsNull(supermarketId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInventory> getInventoriesByProductMaster(UUID productMasterId) {
        productMasterBusiness.validateProductMasterExists(productMasterId);
        return inventoryRepository.findByProductMasterIdAndDeletedAtIsNull(productMasterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductInventory> getExpiringInventories(Long beforeDate) {
        return inventoryRepository.findByExpiryDateBeforeAndDeletedAtIsNull(beforeDate);
    }

    @Override
    @Transactional
    public void softDeleteInventory(UUID id) {
        log.debug("Soft deleting product inventory with ID: {}", id);
        ProductInventory inventory = getActiveInventoryById(id);
        inventory.setDeletedAt(new Date().getTime());
        inventoryRepository.save(inventory);
        log.info("Product inventory soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void restoreInventory(UUID id) {
        log.debug("Restoring product inventory with ID: {}", id);
        ProductInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductInventory", "id", id));
        inventory.setDeletedAt(null);
        inventoryRepository.save(inventory);
        log.info("Product inventory restored successfully with ID: {}", id);
    }

    @Override
    public void validateInventoryExists(UUID id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductInventory", "id", id);
        }
    }

    @Override
    public void validateDiscountPrice(BigDecimal originalPrice, BigDecimal sellingPrice) {
        if (sellingPrice.compareTo(originalPrice) > 0) {
            throw new IllegalArgumentException("Selling price cannot be greater than original price");
        }
    }

    @Override
    @Transactional
    public void updateInventoryStatus(UUID id, InventoryStatus status) {
        log.debug("Updating inventory status for ID: {} to {}", id, status);
        ProductInventory inventory = getActiveInventoryById(id);
        inventory.setStatus(status);
        inventoryRepository.save(inventory);
        log.info("Inventory status updated successfully for ID: {}", id);
    }

    @Override
    @Transactional
    public void markAsNotAvailable(UUID id) {
        updateInventoryStatus(id, InventoryStatus.NOT_AVAILABLE);
    }
}
