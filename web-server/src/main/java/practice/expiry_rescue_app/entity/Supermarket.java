package practice.expiry_rescue_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supermarkets")
public class Supermarket extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String district;

    private String phone;

    private String contactPerson;

    private LocalTime operatingHoursFrom;

    private LocalTime operatingHoursTo;

    @Column(nullable = false)
    private Boolean isActive = true;
}
