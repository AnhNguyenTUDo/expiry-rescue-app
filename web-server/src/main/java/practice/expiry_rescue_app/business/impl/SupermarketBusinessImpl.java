package practice.expiry_rescue_app.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.business.SupermarketBusiness;
import practice.expiry_rescue_app.exception.ResourceNotFoundException;
import practice.expiry_rescue_app.entity.Supermarket;
import practice.expiry_rescue_app.repository.SupermarketRepository;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupermarketBusinessImpl implements SupermarketBusiness {

    private final SupermarketRepository supermarketRepository;

    @Override
    @Transactional
    public Supermarket createSupermarket(String name, String address, String phone, String contactPerson,
                                         LocalTime operatingHoursFrom, LocalTime operatingHoursTo) {
        log.debug("Creating supermarket: {}", name);

        Supermarket supermarket = new Supermarket();
        supermarket.setName(name);
        supermarket.setAddress(address);
        supermarket.setPhone(phone);
        supermarket.setContactPerson(contactPerson);
        supermarket.setOperatingHoursFrom(operatingHoursFrom);
        supermarket.setOperatingHoursTo(operatingHoursTo);
        supermarket.setIsActive(true);

        Supermarket saved = supermarketRepository.save(supermarket);
        log.info("Supermarket created successfully with ID: {}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public Supermarket updateSupermarket(UUID id, String name, String address, String phone, String contactPerson,
                                         LocalTime operatingHoursFrom, LocalTime operatingHoursTo, Boolean isActive) {
        log.debug("Updating supermarket with ID: {}", id);

        Supermarket supermarket = getActiveSupermarketById(id);

        if (name != null) supermarket.setName(name);
        if (address != null) supermarket.setAddress(address);
        if (phone != null) supermarket.setPhone(phone);
        if (contactPerson != null) supermarket.setContactPerson(contactPerson);
        if (operatingHoursFrom != null) supermarket.setOperatingHoursFrom(operatingHoursFrom);
        if (operatingHoursTo != null) supermarket.setOperatingHoursTo(operatingHoursTo);
        if (isActive != null) supermarket.setIsActive(isActive);

        Supermarket updated = supermarketRepository.save(supermarket);
        log.info("Supermarket updated successfully with ID: {}", updated.getId());

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Supermarket getActiveSupermarketById(UUID id) {
        return supermarketRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supermarket", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supermarket> getAllActiveSupermarkets() {
        return supermarketRepository.findByIsActiveTrueAndDeletedAtIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supermarket> getAllSupermarkets() {
        return supermarketRepository.findAllActive();
    }

    @Override
    @Transactional
    public void softDeleteSupermarket(UUID id) {
        log.debug("Soft deleting supermarket with ID: {}", id);
        Supermarket supermarket = getActiveSupermarketById(id);
        supermarket.setDeletedAt(new Date().getTime());
        supermarketRepository.save(supermarket);
        log.info("Supermarket soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void restoreSupermarket(UUID id) {
        log.debug("Restoring supermarket with ID: {}", id);
        Supermarket supermarket = supermarketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supermarket", "id", id));
        supermarket.setDeletedAt(null);
        supermarketRepository.save(supermarket);
        log.info("Supermarket restored successfully with ID: {}", id);
    }

    @Override
    public void validateSupermarketExists(UUID id) {
        if (!supermarketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supermarket", "id", id);
        }
    }
}
