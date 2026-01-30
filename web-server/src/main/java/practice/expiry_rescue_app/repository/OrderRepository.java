package practice.expiry_rescue_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.expiry_rescue_app.entity.Order;
import practice.expiry_rescue_app.enums.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Find orders by user ID
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Find orders by user ID and status
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatus status);

    // Find orders by user ID excluding a specific status (e.g., exclude DELETED)
    List<Order> findByUserIdAndStatusNotOrderByCreatedAtDesc(UUID userId, OrderStatus status);

    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find order by ID and user ID (for security)
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    // Search orders by user ID with filters
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN o.items oi WHERE o.user.id = :userId " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:searchQuery IS NULL OR " +
           "     LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "     LOWER(oi.productName) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           "     LOWER(oi.supermarketName) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
           "ORDER BY o.createdAt DESC")
    List<Order> searchOrders(@Param("userId") UUID userId,
                            @Param("status") OrderStatus status,
                            @Param("searchQuery") String searchQuery);
}

