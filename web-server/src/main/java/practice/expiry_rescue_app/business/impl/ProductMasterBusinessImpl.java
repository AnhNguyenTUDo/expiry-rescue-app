package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.ProductCategoryBusiness;
import practice.expiry_rescue_app.business.ProductMasterBusiness;
import practice.expiry_rescue_app.exception.ResourceNotFoundException;
import practice.expiry_rescue_app.entity.ProductCategory;
import practice.expiry_rescue_app.entity.ProductMaster;
import practice.expiry_rescue_app.repository.ProductMasterRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductMasterBusinessImpl implements ProductMasterBusiness {

    private final ProductMasterRepository productMasterRepository;
    private final ProductCategoryBusiness categoryBusiness;

    @Override
    @Transactional
    public ProductMaster createProductMaster(String name, UUID categoryId, String description, String unit, Integer daysBeforeExpiry) {
        log.debug("Creating product master: {}", name);

        // Validate category exists
        ProductCategory category = categoryBusiness.getActiveCategoryById(categoryId);

        ProductMaster product = new ProductMaster();
        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setUnit(unit);
        product.setDaysBeforeExpiry(daysBeforeExpiry != null ? daysBeforeExpiry : category.getDefaultDaysBeforeExpiry());
        product.setIsActive(true);

        ProductMaster saved = productMasterRepository.save(product);
        log.info("Product master created successfully with ID: {}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public ProductMaster updateProductMaster(UUID id, String name, UUID categoryId, String description, String unit, Integer daysBeforeExpiry, Boolean isActive) {
        log.debug("Updating product master with ID: {}", id);

        ProductMaster product = getActiveProductMasterById(id);

        if (name != null) product.setName(name);
        if (categoryId != null) {
            ProductCategory category = categoryBusiness.getActiveCategoryById(categoryId);
            product.setCategory(category);
        }
        if (description != null) product.setDescription(description);
        if (unit != null) product.setUnit(unit);
        if (daysBeforeExpiry != null) product.setDaysBeforeExpiry(daysBeforeExpiry);
        if (isActive != null) product.setIsActive(isActive);

        ProductMaster updated = productMasterRepository.save(product);
        log.info("Product master updated successfully with ID: {}", updated.getId());

        return updated;
    }

    @Override
    @Transactional
    public ProductMaster updateProductImage(UUID id, String imageUrl) {
        log.debug("Updating product image for ID: {}", id);

        ProductMaster product = getActiveProductMasterById(id);
        product.setStandardImageUrl(imageUrl);

        ProductMaster updated = productMasterRepository.save(product);
        log.info("Product image updated successfully for ID: {}", updated.getId());

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductMaster getActiveProductMasterById(UUID id) {
        return productMasterRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductMaster", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductMaster> getAllActiveProductMasters() {
        return productMasterRepository.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductMaster> getActiveProductMastersByCategory(UUID categoryId) {
        categoryBusiness.validateCategoryExists(categoryId);
        return productMasterRepository.findByCategoryIdAndIsActiveTrueAndDeletedAtIsNull(categoryId);
    }

    @Override
    @Transactional
    public void softDeleteProductMaster(UUID id) {
        log.debug("Soft deleting product master with ID: {}", id);
        ProductMaster product = getActiveProductMasterById(id);
        product.setDeletedAt(new Date().getTime());
        productMasterRepository.save(product);
        log.info("Product master soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void restoreProductMaster(UUID id) {
        log.debug("Restoring product master with ID: {}", id);
        ProductMaster product = productMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductMaster", "id", id));
        product.setDeletedAt(null);
        productMasterRepository.save(product);
        log.info("Product master restored successfully with ID: {}", id);
    }

    @Override
    public void validateProductMasterExists(UUID id) {
        if (!productMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductMaster", "id", id);
        }
    }
}
