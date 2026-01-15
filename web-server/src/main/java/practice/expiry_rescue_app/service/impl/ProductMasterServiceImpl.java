package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import practice.expiry_rescue_app.business.ProductMasterBusiness;
import practice.expiry_rescue_app.model.product.CreateProductMasterRequest;
import practice.expiry_rescue_app.model.product.ProductMasterResponse;
import practice.expiry_rescue_app.entity.ProductMaster;
import practice.expiry_rescue_app.service.FirebaseStorageService;
import practice.expiry_rescue_app.service.ProductMasterService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductMasterServiceImpl implements ProductMasterService {

    private final ProductMasterBusiness productMasterBusiness;
    private final FirebaseStorageService firebaseStorageService;

    @Override
    public ProductMasterResponse createProductMaster(CreateProductMasterRequest request) {
        log.debug("Service: Creating product master: {}", request.getName());

        ProductMaster product = productMasterBusiness.createProductMaster(
                request.getName(),
                request.getCategoryId(),
                request.getDescription(),
                request.getUnit(),
                request.getDaysBeforeExpiry()
        );

        return mapToResponse(product);
    }

    @Override
    public ProductMasterResponse createProductMasterWithImage(CreateProductMasterRequest request, MultipartFile image) {
        log.debug("Service: Creating product master with image: {}", request.getName());

        // Create product first
        ProductMaster product = productMasterBusiness.createProductMaster(
                request.getName(),
                request.getCategoryId(),
                request.getDescription(),
                request.getUnit(),
                request.getDaysBeforeExpiry()
        );

        // Upload image if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = firebaseStorageService.uploadImage(image, "products");
            product = productMasterBusiness.updateProductImage(product.getId(), imageUrl);
        }

        return mapToResponse(product);
    }

    @Override
    public ProductMasterResponse updateProductMaster(UUID id, CreateProductMasterRequest request) {
        log.debug("Service: Updating product master with ID: {}", id);

        ProductMaster product = productMasterBusiness.updateProductMaster(
                id,
                request.getName(),
                request.getCategoryId(),
                request.getDescription(),
                request.getUnit(),
                request.getDaysBeforeExpiry(),
                null
        );

        return mapToResponse(product);
    }

    @Override
    public ProductMasterResponse uploadProductImage(UUID id, MultipartFile image) {
        log.debug("Service: Uploading image for product master with ID: {}", id);

        String imageUrl = firebaseStorageService.uploadImage(image, "products");
        ProductMaster product = productMasterBusiness.updateProductImage(id, imageUrl);

        return mapToResponse(product);
    }

    @Override
    public ProductMasterResponse getProductMasterById(UUID id) {
        log.debug("Service: Getting product master by ID: {}", id);
        ProductMaster product = productMasterBusiness.getActiveProductMasterById(id);
        return mapToResponse(product);
    }

    @Override
    public List<ProductMasterResponse> getAllProductMasters() {
        log.debug("Service: Getting all product masters");
        List<ProductMaster> products = productMasterBusiness.getAllActiveProductMasters();
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductMasterResponse> getProductMastersByCategory(UUID categoryId) {
        log.debug("Service: Getting product masters by category ID: {}", categoryId);
        List<ProductMaster> products = productMasterBusiness.getActiveProductMastersByCategory(categoryId);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProductMaster(UUID id) {
        log.debug("Service: Deleting product master with ID: {}", id);
        productMasterBusiness.softDeleteProductMaster(id);
    }

    @Override
    public void restoreProductMaster(UUID id) {
        log.debug("Service: Restoring product master with ID: {}", id);
        productMasterBusiness.restoreProductMaster(id);
    }

    private ProductMasterResponse mapToResponse(ProductMaster product) {
        ProductMasterResponse response = new ProductMasterResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setDescription(product.getDescription());
        response.setUnit(product.getUnit());
        response.setStandardImageUrl(product.getStandardImageUrl());
        response.setDaysBeforeExpiry(product.getDaysBeforeExpiry());
        response.setIsActive(product.getIsActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
