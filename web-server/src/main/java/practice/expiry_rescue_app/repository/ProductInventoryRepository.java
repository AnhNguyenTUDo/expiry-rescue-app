package practice.expiry_rescue_app.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.ProductInventory;
import practice.expiry_rescue_app.entity.enums.InventoryStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductInventoryRepository extends BaseRepository<ProductInventory>, JpaSpecificationExecutor<ProductInventory> {

    List<ProductInventory> findByStatusAndDeletedAtIsNull(InventoryStatus status);

    @Query("SELECT pi FROM ProductInventory pi " +
           "JOIN FETCH pi.productMaster pm " +
           "JOIN FETCH pm.category " +
           "JOIN FETCH pi.supermarket " +
           "WHERE pi.supermarket.id = :supermarketId " +
           "AND pi.deletedAt IS NULL")
    List<ProductInventory> findBySupermarketIdAndDeletedAtIsNull(@Param("supermarketId") UUID supermarketId);

    @Query("SELECT pi FROM ProductInventory pi " +
           "JOIN FETCH pi.productMaster pm " +
           "JOIN FETCH pm.category " +
           "JOIN FETCH pi.supermarket " +
           "WHERE pm.id = :productMasterId " +
           "AND pi.deletedAt IS NULL")
    List<ProductInventory> findByProductMasterIdAndDeletedAtIsNull(@Param("productMasterId") UUID productMasterId);

    List<ProductInventory> findByExpiryDateBeforeAndDeletedAtIsNull(Long date);
}
