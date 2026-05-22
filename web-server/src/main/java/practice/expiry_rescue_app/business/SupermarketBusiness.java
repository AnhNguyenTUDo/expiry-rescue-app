package practice.expiry_rescue_app.business;

import practice.expiry_rescue_app.entity.Supermarket;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Business layer for Supermarket - handles core business logic and data access
 */
public interface SupermarketBusiness {

    Supermarket createSupermarket(String name, String address, UUID districtId, String phone, String contactPerson,
                                  LocalTime operatingHoursFrom, LocalTime operatingHoursTo);

    Supermarket updateSupermarket(UUID id, String name, String address, UUID districtId, String phone, String contactPerson,
                                  LocalTime operatingHoursFrom, LocalTime operatingHoursTo, Boolean isActive);

    Supermarket getActiveSupermarketById(UUID id);

    List<Supermarket> getAllActiveSupermarkets();

    List<Supermarket> getAllActiveSupermarketsFiltered(UUID cityId, UUID districtId);

    List<Supermarket> getAllSupermarkets();

    void softDeleteSupermarket(UUID id);

    void restoreSupermarket(UUID id);

    void validateSupermarketExists(UUID id);
}
