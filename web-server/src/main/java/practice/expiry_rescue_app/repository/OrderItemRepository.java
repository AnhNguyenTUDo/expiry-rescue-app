package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.OrderItem;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {
}
