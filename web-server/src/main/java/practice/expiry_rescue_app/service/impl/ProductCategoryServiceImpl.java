package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.business.ProductCategoryBusiness;
import practice.expiry_rescue_app.model.product.CreateProductCategoryRequest;
import practice.expiry_rescue_app.model.product.ProductCategoryResponse;
import practice.expiry_rescue_app.entity.ProductCategory;
import practice.expiry_rescue_app.service.ProductCategoryService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryBusiness categoryBusiness;

    @Override
    public ProductCategoryResponse createCategory(CreateProductCategoryRequest request) {
        log.debug("Service: Creating product category: {}", request.getName());

        ProductCategory category = categoryBusiness.createCategory(
                request.getName(),
                request.getDefaultDaysBeforeExpiry()
        );

        return mapToResponse(category);
    }

    @Override
    public ProductCategoryResponse updateCategory(UUID id, CreateProductCategoryRequest request) {
        log.debug("Service: Updating product category with ID: {}", id);

        ProductCategory category = categoryBusiness.updateCategory(
                id,
                request.getName(),
                request.getDefaultDaysBeforeExpiry()
        );

        return mapToResponse(category);
    }

    @Override
    public ProductCategoryResponse getCategoryById(UUID id) {
        log.debug("Service: Getting product category by ID: {}", id);
        ProductCategory category = categoryBusiness.getActiveCategoryById(id);
        return mapToResponse(category);
    }

    @Override
    public List<ProductCategoryResponse> getAllCategories() {
        log.debug("Service: Getting all product categories");
        List<ProductCategory> categories = categoryBusiness.getAllActiveCategories();
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(UUID id) {
        log.debug("Service: Deleting product category with ID: {}", id);
        categoryBusiness.softDeleteCategory(id);
    }

    @Override
    public void restoreCategory(UUID id) {
        log.debug("Service: Restoring product category with ID: {}", id);
        categoryBusiness.restoreCategory(id);
    }

    private ProductCategoryResponse mapToResponse(ProductCategory category) {
        ProductCategoryResponse response = new ProductCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDefaultDaysBeforeExpiry(category.getDefaultDaysBeforeExpiry());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
