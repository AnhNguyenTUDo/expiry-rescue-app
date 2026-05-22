package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.model.supermarket.CreateSupermarketRequest;
import practice.expiry_rescue_app.model.supermarket.SupermarketResponse;
import practice.expiry_rescue_app.model.supermarket.SupermarketWithProductsResponse;
import practice.expiry_rescue_app.model.supermarket.UpdateSupermarketRequest;

import java.util.List;
import java.util.UUID;

public interface SupermarketService {

    SupermarketResponse createSupermarket(CreateSupermarketRequest request);

    SupermarketResponse updateSupermarket(UUID id, UpdateSupermarketRequest request);

    SupermarketResponse getSupermarketById(UUID id);

    SupermarketWithProductsResponse getSupermarketWithProducts(UUID id);

    List<SupermarketResponse> getAllSupermarkets();

    List<SupermarketResponse> getAllActiveSupermarkets(UUID cityId, UUID districtId);

    void deleteSupermarket(UUID id);

    void restoreSupermarket(UUID id);
}
