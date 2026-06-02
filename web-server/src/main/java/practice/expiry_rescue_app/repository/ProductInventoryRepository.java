package practice.expiry_rescue_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.ProductInventory;
import practice.expiry_rescue_app.enums.InventoryStatus;
import practice.expiry_rescue_app.repository.projection.LocationSummaryProjection;

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

    @Query("SELECT pi FROM ProductInventory pi " +
           "JOIN FETCH pi.productMaster pm " +
           "JOIN FETCH pm.category " +
           "JOIN FETCH pi.supermarket " +
           "WHERE pi.supermarket.id = :supermarketId " +
           "AND pm.id = :productMasterId " +
           "AND pi.deletedAt IS NULL " +
           "ORDER BY pi.expiryDate ASC")
    List<ProductInventory> findBySupermarketIdAndProductMasterIdAndDeletedAtIsNull(
            @Param("supermarketId") UUID supermarketId,
            @Param("productMasterId") UUID productMasterId);

    @Query(value = "SELECT pi.supermarket.id AS supermarketId, " +
           "pi.supermarket.name AS supermarketName, " +
           "SUM(pi.quantityAvailable) AS totalQuantity, " +
           "MIN(pi.sellingPrice) AS bestPrice, " +
           "MIN(pi.expiryDate) AS earliestExpiry " +
           "FROM ProductInventory pi " +
           "WHERE pi.productMaster.id = :productMasterId " +
           "AND pi.supermarket.id <> :excludeSupermarketId " +
           "AND pi.supermarket.district.city.id = :cityId " +
           "AND pi.deletedAt IS NULL " +
           "GROUP BY pi.supermarket.id, pi.supermarket.name " +
           "ORDER BY pi.supermarket.name ASC",
           countQuery = "SELECT COUNT(DISTINCT pi.supermarket.id) " +
           "FROM ProductInventory pi " +
           "WHERE pi.productMaster.id = :productMasterId " +
           "AND pi.supermarket.id <> :excludeSupermarketId " +
           "AND pi.supermarket.district.city.id = :cityId " +
           "AND pi.deletedAt IS NULL")
    Page<LocationSummaryProjection> findOtherLocationSummariesInCity(
            @Param("productMasterId") UUID productMasterId,
            @Param("excludeSupermarketId") UUID excludeSupermarketId,
            @Param("cityId") UUID cityId,
            Pageable pageable);

    List<ProductInventory> findByExpiryDateBeforeAndDeletedAtIsNull(Long date);
}
