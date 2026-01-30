package practice.expiry_rescue_app.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private List<OrderItemRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        private Long inventoryId;
        private Long productMasterId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private Long supermarketId;
        private String supermarketName;
        private Long expiryDate;
    }
}
