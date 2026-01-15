package practice.expiry_rescue_app.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {
    private UUID id;
    private String name;
    private Integer defaultDaysBeforeExpiry;
    private Long createdAt;
    private Long updatedAt;
}
