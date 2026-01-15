package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.expiry_rescue_app.business.SupermarketBusiness;
import practice.expiry_rescue_app.model.product.ProductInventoryResponse;
import practice.expiry_rescue_app.model.supermarket.CreateSupermarketRequest;
import practice.expiry_rescue_app.model.supermarket.SupermarketResponse;
import practice.expiry_rescue_app.model.supermarket.SupermarketWithProductsResponse;
import practice.expiry_rescue_app.model.supermarket.UpdateSupermarketRequest;
import practice.expiry_rescue_app.entity.ProductInventory;
import practice.expiry_rescue_app.entity.Supermarket;
import practice.expiry_rescue_app.repository.ProductInventoryRepository;
import practice.expiry_rescue_app.repository.SupermarketRepository;
import practice.expiry_rescue_app.service.SupermarketService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupermarketServiceImpl implements SupermarketService {

    private final SupermarketBusiness supermarketBusiness;
    private final SupermarketRepository supermarketRepository;
    private final ProductInventoryRepository productInventoryRepository;

    @Override
    public SupermarketResponse createSupermarket(CreateSupermarketRequest request) {
        log.debug("Service: Creating supermarket: {}", request.getName());

        Supermarket supermarket = supermarketBusiness.createSupermarket(
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getContactPerson(),
                request.getOperatingHoursFrom(),
                request.getOperatingHoursTo()
        );

        return mapToResponse(supermarket);
    }

    @Override
    public SupermarketResponse updateSupermarket(UUID id, UpdateSupermarketRequest request) {
        log.debug("Service: Updating supermarket with ID: {}", id);

        Supermarket supermarket = supermarketBusiness.updateSupermarket(
                id,
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getContactPerson(),
                request.getOperatingHoursFrom(),
                request.getOperatingHoursTo(),
                request.getIsActive()
        );

        return mapToResponse(supermarket);
    }

    @Override
    public SupermarketResponse getSupermarketById(UUID id) {
        log.debug("Service: Getting supermarket by ID: {}", id);
        Supermarket supermarket = supermarketBusiness.getActiveSupermarketById(id);
        return mapToResponse(supermarket);
    }

    @Override
    public SupermarketWithProductsResponse getSupermarketWithProducts(UUID id) {
        log.debug("Service: Getting supermarket with products by ID: {}", id);

        // Get supermarket details
        Supermarket supermarket = supermarketBusiness.getActiveSupermarketById(id);

        // Get products for this supermarket
        List<ProductInventory> inventories = productInventoryRepository.findBySupermarketIdAndDeletedAtIsNull(id);
        List<ProductInventoryResponse> products = inventories.stream()
                .map(this::mapToProductInventoryResponse)
                .collect(Collectors.toList());

        // Map to combined response
        return mapToSupermarketWithProductsResponse(supermarket, products);
    }

    @Override
    public List<SupermarketResponse> getAllSupermarkets() {
        log.debug("Service: Getting all supermarkets");
        List<Supermarket> supermarkets = supermarketBusiness.getAllSupermarkets();
        return supermarkets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupermarketResponse> getAllActiveSupermarkets() {
        log.debug("Service: Getting all active supermarkets");
        List<Supermarket> supermarkets = supermarketBusiness.getAllActiveSupermarkets();
        return supermarkets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSupermarket(UUID id) {
        log.debug("Service: Deleting supermarket with ID: {}", id);
        supermarketBusiness.softDeleteSupermarket(id);
    }

    @Override
    public void restoreSupermarket(UUID id) {
        log.debug("Service: Restoring supermarket with ID: {}", id);
        supermarketBusiness.restoreSupermarket(id);
    }

    @Override
    public List<String> getDistinctDistricts() {
        log.debug("Service: Getting distinct districts");
        return supermarketRepository.findDistinctDistricts();
    }

    private SupermarketResponse mapToResponse(Supermarket supermarket) {
        SupermarketResponse response = new SupermarketResponse();
        response.setId(supermarket.getId());
        response.setName(supermarket.getName());
        response.setAddress(supermarket.getAddress());
        response.setDistrict(supermarket.getDistrict());
        response.setPhone(supermarket.getPhone());
        response.setContactPerson(supermarket.getContactPerson());
        response.setOperatingHoursFrom(supermarket.getOperatingHoursFrom());
        response.setOperatingHoursTo(supermarket.getOperatingHoursTo());
        response.setIsActive(supermarket.getIsActive());
        response.setCreatedAt(supermarket.getCreatedAt());
        response.setUpdatedAt(supermarket.getUpdatedAt());
        return response;
    }

    private SupermarketWithProductsResponse mapToSupermarketWithProductsResponse(
            Supermarket supermarket,
            List<ProductInventoryResponse> products) {
        SupermarketWithProductsResponse response = new SupermarketWithProductsResponse();
        response.setId(supermarket.getId());
        response.setName(supermarket.getName());
        response.setAddress(supermarket.getAddress());
        response.setDistrict(supermarket.getDistrict());
        response.setPhone(supermarket.getPhone());
        response.setContactPerson(supermarket.getContactPerson());
        response.setOperatingHoursFrom(supermarket.getOperatingHoursFrom());
        response.setOperatingHoursTo(supermarket.getOperatingHoursTo());
        response.setIsActive(supermarket.getIsActive());
        response.setCreatedAt(supermarket.getCreatedAt());
        response.setUpdatedAt(supermarket.getUpdatedAt());
        response.setProducts(products);
        return response;
    }

    private ProductInventoryResponse mapToProductInventoryResponse(ProductInventory inventory) {
        ProductInventoryResponse response = new ProductInventoryResponse();
        response.setId(inventory.getId());
        response.setProductMasterId(inventory.getProductMaster().getId());
        response.setProductName(inventory.getProductMaster().getName());
        response.setProductDescription(inventory.getProductMaster().getDescription());
        response.setCategoryId(inventory.getProductMaster().getCategory().getId());
        response.setCategoryName(inventory.getProductMaster().getCategory().getName());
        response.setSupermarketId(inventory.getSupermarket().getId());
        response.setSupermarketName(inventory.getSupermarket().getName());
        response.setOriginalPrice(inventory.getOriginalPrice());
        response.setSellingPrice(inventory.getSellingPrice());
        response.setQuantityAvailable(inventory.getQuantityAvailable());
        response.setExpiryDate(inventory.getExpiryDate());
        response.setStatus(inventory.getStatus());
        if (inventory.getCreatedBy() != null) {
            response.setCreatedById(inventory.getCreatedBy().getId());
            response.setCreatedByName(inventory.getCreatedBy().getFullName());
        }
        response.setCreatedAt(inventory.getCreatedAt());
        response.setUpdatedAt(inventory.getUpdatedAt());
        return response;
    }
}
