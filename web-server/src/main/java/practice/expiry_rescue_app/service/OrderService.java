package practice.expiry_rescue_app.service;

import practice.expiry_rescue_app.enums.OrderStatus;
import practice.expiry_rescue_app.model.order.CreateOrderRequest;
import practice.expiry_rescue_app.model.order.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    
    /**
     * Create a new order from cart items
     */
    OrderResponse createOrder(CreateOrderRequest request, String userEmail);
    
    /**
     * Get all orders for a user
     */
    List<OrderResponse> getUserOrders(String userEmail);
    
    /**
     * Get orders with filters
     */
    List<OrderResponse> searchOrders(String userEmail, OrderStatus status, String searchQuery);
    
    /**
     * Get order by ID
     */
    OrderResponse getOrderById(UUID orderId, String userEmail);
    
    /**
     * Cancel an order
     */
    OrderResponse cancelOrder(UUID orderId, String userEmail);
    
    /**
     * Soft delete an order
     */
    void deleteOrder(UUID orderId, String userEmail);
}
