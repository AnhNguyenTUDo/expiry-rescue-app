package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.ProductCategoryBusiness;
import practice.expiry_rescue_app.exception.ResourceNotFoundException;
import practice.expiry_rescue_app.entity.ProductCategory;
import practice.expiry_rescue_app.repository.ProductCategoryRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCategoryBusinessImpl implements ProductCategoryBusiness {

    private final ProductCategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductCategory createCategory(String name, Integer defaultDaysBeforeExpiry) {
        log.debug("Creating product category: {}", name);

        ProductCategory category = new ProductCategory();
        category.setName(name);
        category.setDefaultDaysBeforeExpiry(defaultDaysBeforeExpiry);

        ProductCategory saved = categoryRepository.save(category);
        log.info("Product category created successfully with ID: {}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public ProductCategory updateCategory(UUID id, String name, Integer defaultDaysBeforeExpiry) {
        log.debug("Updating product category with ID: {}", id);

        ProductCategory category = getActiveCategoryById(id);

        if (name != null) category.setName(name);
        if (defaultDaysBeforeExpiry != null) category.setDefaultDaysBeforeExpiry(defaultDaysBeforeExpiry);

        ProductCategory updated = categoryRepository.save(category);
        log.info("Product category updated successfully with ID: {}", updated.getId());

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategory getActiveCategoryById(UUID id) {
        return categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategory> getAllActiveCategories() {
        return categoryRepository.findAllActive();
    }

    @Override
    @Transactional
    public void softDeleteCategory(UUID id) {
        log.debug("Soft deleting product category with ID: {}", id);
        ProductCategory category = getActiveCategoryById(id);
        category.setDeletedAt(new Date().getTime());
        categoryRepository.save(category);
        log.info("Product category soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void restoreCategory(UUID id) {
        log.debug("Restoring product category with ID: {}", id);
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", id));
        category.setDeletedAt(null);
        categoryRepository.save(category);
        log.info("Product category restored successfully with ID: {}", id);
    }

    @Override
    public void validateCategoryExists(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductCategory", "id", id);
        }
    }
}
