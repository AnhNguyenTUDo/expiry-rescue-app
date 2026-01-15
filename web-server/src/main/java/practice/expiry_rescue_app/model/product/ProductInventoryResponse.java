package practice.expiry_rescue_app.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.expiry_rescue_app.entity.enums.InventoryStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryResponse {
    private UUID id;
    private UUID productMasterId;
    private String productName;
    private String productDescription;
    private UUID categoryId;
    private String categoryName;
    private UUID supermarketId;
    private String supermarketName;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private Integer quantityAvailable;
    private Long expiryDate;
    private InventoryStatus status;
    private UUID createdById;
    private String createdByName;
    private Long createdAt;
    private Long updatedAt;
}
