package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.product.CreateProductCategoryRequest;
import practice.expiry_rescue_app.model.product.ProductCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface ProductCategoryService {

    ProductCategoryResponse createCategory(CreateProductCategoryRequest request);

    ProductCategoryResponse updateCategory(UUID id, CreateProductCategoryRequest request);

    ProductCategoryResponse getCategoryById(UUID id);

    List<ProductCategoryResponse> getAllCategories();

    void deleteCategory(UUID id);

    void restoreCategory(UUID id);
}
