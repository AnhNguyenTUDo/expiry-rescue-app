package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.ProductMaster;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductMasterRepository extends BaseRepository<ProductMaster> {

    List<ProductMaster> findByCategoryIdAndIsActiveTrueAndDeletedAtIsNull(UUID categoryId);
}
