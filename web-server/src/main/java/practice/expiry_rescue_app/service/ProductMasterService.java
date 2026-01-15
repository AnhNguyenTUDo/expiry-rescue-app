package practice.expiry_rescue_app.service;

import org.springframework.web.multipart.MultipartFile;
import practice.expiry_rescue_app.model.product.CreateProductMasterRequest;
import practice.expiry_rescue_app.model.product.ProductMasterResponse;

import java.util.List;
import java.util.UUID;

public interface ProductMasterService {

    ProductMasterResponse createProductMaster(CreateProductMasterRequest request);

    ProductMasterResponse createProductMasterWithImage(CreateProductMasterRequest request, MultipartFile image);

    ProductMasterResponse updateProductMaster(UUID id, CreateProductMasterRequest request);

    ProductMasterResponse uploadProductImage(UUID id, MultipartFile image);

    ProductMasterResponse getProductMasterById(UUID id);

    List<ProductMasterResponse> getAllProductMasters();

    List<ProductMasterResponse> getProductMastersByCategory(UUID categoryId);

    void deleteProductMaster(UUID id);

    void restoreProductMaster(UUID id);
}
