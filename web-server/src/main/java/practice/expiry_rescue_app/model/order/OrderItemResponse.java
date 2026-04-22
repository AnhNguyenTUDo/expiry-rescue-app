package practice.expiry_rescue_app.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private UUID id;
    private UUID productInventoryId;
    private UUID productMasterId;
    private String productName;
    private Integer quantity;
    private String unit;
    private BigDecimal subtotal;
    private UUID supermarketId;
    private String supermarketName;
    private Long expiryDate;
}
