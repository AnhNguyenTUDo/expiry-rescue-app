package practice.expiry_rescue_app.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;


public interface LocationSummaryProjection {
    UUID getSupermarketId();

    String getSupermarketName();

    Long getTotalQuantity();

    BigDecimal getBestPrice();

    Long getEarliestExpiry();
}
