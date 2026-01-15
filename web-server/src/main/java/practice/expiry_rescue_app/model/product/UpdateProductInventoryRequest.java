package practice.expiry_rescue_app.model.product;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.expiry_rescue_app.entity.enums.InventoryStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductInventoryRequest {

    @Min(value = 0, message = "Original price must be positive")
    private BigDecimal originalPrice;

    @Min(value = 0, message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantityAvailable;

    private Long expiryDate;

    private InventoryStatus status;
}
