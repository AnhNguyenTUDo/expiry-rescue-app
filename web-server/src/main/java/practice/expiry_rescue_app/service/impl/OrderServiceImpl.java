package practice.expiry_rescue_app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.expiry_rescue_app.entity.Order;
import practice.expiry_rescue_app.entity.OrderItem;
import practice.expiry_rescue_app.entity.User;
import practice.expiry_rescue_app.enums.OrderStatus;
import practice.expiry_rescue_app.model.order.CreateOrderRequest;
import practice.expiry_rescue_app.model.order.OrderItemResponse;
import practice.expiry_rescue_app.model.order.OrderResponse;
import practice.expiry_rescue_app.repository.OrderRepository;
import practice.expiry_rescue_app.repository.UserRepository;
import practice.expiry_rescue_app.service.OrderService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);

        // Calculate total and create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductInventoryId(itemRequest.getInventoryId());
            orderItem.setProductMasterId(itemRequest.getProductMasterId());
            orderItem.setProductName(itemRequest.getProductName());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(itemRequest.getUnitPrice());
            
            BigDecimal subtotal = itemRequest.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            orderItem.setSupermarketId(itemRequest.getSupermarketId());
            orderItem.setSupermarketName(itemRequest.getSupermarketName());
            orderItem.setExpiryDate(itemRequest.getExpiryDate());

            order.addItem(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(
                user.getId(), OrderStatus.DELETED);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> searchOrders(String userEmail, OrderStatus status, String searchQuery) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.searchOrders(user.getId(), status, searchQuery);

        // Filter out deleted orders
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.DELETED)
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(UUID orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELETED) {
            throw new RuntimeException("Order not found");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELETED) {
            throw new RuntimeException("Cannot cancel this order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.DELETED);
        orderRepository.save(order);
    }

    // Helper methods
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getItems().size())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productInventoryId(item.getProductInventoryId())
                .productMasterId(item.getProductMasterId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .supermarketId(item.getSupermarketId())
                .supermarketName(item.getSupermarketName())
                .expiryDate(item.getExpiryDate())
                .build();
    }
}
