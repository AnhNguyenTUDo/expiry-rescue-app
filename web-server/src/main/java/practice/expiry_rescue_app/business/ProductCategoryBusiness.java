package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ProductCategoryBusiness {

    ProductCategory createCategory(String name, Integer defaultDaysBeforeExpiry);

    ProductCategory updateCategory(UUID id, String name, Integer defaultDaysBeforeExpiry);

    ProductCategory getActiveCategoryById(UUID id);

    List<ProductCategory> getAllActiveCategories();

    void softDeleteCategory(UUID id);

    void restoreCategory(UUID id);

    void validateCategoryExists(UUID id);
}
