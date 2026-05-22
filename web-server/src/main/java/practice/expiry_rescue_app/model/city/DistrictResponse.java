package practice.expiry_rescue_app.model.city;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResponse {
    private UUID id;
    private String name;
    private UUID cityId;
    private String cityName;
    private Boolean isActive;
}
