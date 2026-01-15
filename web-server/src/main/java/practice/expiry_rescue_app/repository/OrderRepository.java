package practice.expiry_rescue_app.repository;

import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.Order;

@Repository
public interface OrderRepository extends BaseRepository<Order> {
}
