package practice.expiry_rescue_app.model.city;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityResponse {
    private UUID id;
    private String name;
    private Boolean isActive;
}
