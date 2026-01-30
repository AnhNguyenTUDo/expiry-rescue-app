package practice.expiry_rescue_app.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.expiry_rescue_app.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Integer itemCount;
    private Long createdAt;
    private Long updatedAt;
    private List<OrderItemResponse> items;
}
