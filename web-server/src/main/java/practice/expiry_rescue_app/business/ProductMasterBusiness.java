package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.ProductMaster;

import java.util.List;
import java.util.UUID;

public interface ProductMasterBusiness {

    ProductMaster createProductMaster(String name, UUID categoryId, String description, String unit, Integer daysBeforeExpiry);

    ProductMaster updateProductMaster(UUID id, String name, UUID categoryId, String description, String unit, Integer daysBeforeExpiry, Boolean isActive);

    ProductMaster updateProductImage(UUID id, String imageUrl);

    ProductMaster getActiveProductMasterById(UUID id);

    List<ProductMaster> getAllActiveProductMasters();

    List<ProductMaster> getActiveProductMastersByCategory(UUID categoryId);

    void softDeleteProductMaster(UUID id);

    void restoreProductMaster(UUID id);

    void validateProductMasterExists(UUID id);
}
