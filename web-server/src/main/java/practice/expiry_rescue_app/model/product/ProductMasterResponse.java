package practice.expiry_rescue_app.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMasterResponse {
    private UUID id;
    private String name;
    private UUID categoryId;
    private String categoryName;
    private String description;
    private String unit;
    private String standardImageUrl;
    private Integer daysBeforeExpiry;
    private Boolean isActive;
    private Long createdAt;
    private Long updatedAt;
}
