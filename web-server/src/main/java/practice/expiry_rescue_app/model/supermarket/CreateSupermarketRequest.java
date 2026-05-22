package practice.expiry_rescue_app.model.supermarket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupermarketRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "District is required")
    private UUID districtId;

    private String phone;

    private String contactPerson;

    private LocalTime operatingHoursFrom;

    private LocalTime operatingHoursTo;
}
