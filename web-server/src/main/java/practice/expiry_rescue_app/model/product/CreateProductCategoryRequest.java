package practice.expiry_rescue_app.model.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotNull(message = "Default days before expiry is required")
    @Min(value = 1, message = "Default days before expiry must be at least 1")
    private Integer defaultDaysBeforeExpiry;
}
