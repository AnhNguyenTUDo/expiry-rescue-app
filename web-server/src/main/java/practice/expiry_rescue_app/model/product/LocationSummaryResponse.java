package practice.expiry_rescue_app.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationSummaryResponse {
    private UUID supermarketId;
    private String supermarketName;
    private Long totalQuantity;
    private BigDecimal bestPrice;
    private Long earliestExpiry;
}
