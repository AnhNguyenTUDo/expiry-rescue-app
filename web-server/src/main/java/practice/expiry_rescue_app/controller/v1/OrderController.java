package practice.expiry_rescue_app.controller.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import practice.expiry_rescue_app.enums.OrderStatus;
import practice.expiry_rescue_app.model.order.CreateOrderRequest;
import practice.expiry_rescue_app.model.order.OrderResponse;
import practice.expiry_rescue_app.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.createOrder(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        String userEmail = authentication.getName();
        
        List<OrderResponse> orders;
        if (status != null || search != null) {
            orders = orderService.searchOrders(userEmail, status, search);
        } else {
            orders = orderService.getUserOrders(userEmail);
        }
        
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.getOrderById(id, userEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        OrderResponse response = orderService.cancelOrder(id, userEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        orderService.deleteOrder(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
