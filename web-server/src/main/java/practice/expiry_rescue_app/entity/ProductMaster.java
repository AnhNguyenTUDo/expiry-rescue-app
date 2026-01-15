package practice.expiry_rescue_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_master")
public class ProductMaster extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String unit;

    private String standardImageUrl;

    private Integer daysBeforeExpiry;

    @Column(nullable = false)
    private Boolean isActive = true;
}
