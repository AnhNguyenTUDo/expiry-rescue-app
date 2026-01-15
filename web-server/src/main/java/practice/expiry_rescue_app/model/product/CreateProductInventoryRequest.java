package practice.expiry_rescue_app.model.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductInventoryRequest {

    @NotNull(message = "Product master ID is required")
    private UUID productMasterId;

    @NotNull(message = "Supermarket ID is required")
    private UUID supermarketId;

    @NotNull(message = "Original price is required")
    @Min(value = 0, message = "Original price must be positive")
    private BigDecimal originalPrice;

    @NotNull(message = "Selling price is required")
    @Min(value = 0, message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantityAvailable;

    @NotNull(message = "Expiry date is required")
    private Long expiryDate;
}
