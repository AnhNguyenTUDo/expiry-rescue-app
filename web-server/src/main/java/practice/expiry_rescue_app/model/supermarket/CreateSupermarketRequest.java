package practice.expiry_rescue_app.model.supermarket;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupermarketRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private String phone;

    private String contactPerson;

    private LocalTime operatingHoursFrom;

    private LocalTime operatingHoursTo;
}
