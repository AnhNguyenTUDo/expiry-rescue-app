package practice.expiry_rescue_app.model.supermarket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupermarketResponse {
    private UUID id;
    private String name;
    private String address;
    private String district;
    private String phone;
    private String contactPerson;
    private LocalTime operatingHoursFrom;
    private LocalTime operatingHoursTo;
    private Boolean isActive;
    private Long createdAt;
    private Long updatedAt;
}
