package swp.project.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicle_colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String colorName;

    private String hexCode; // mã màu, VD: #FFFFFF

    @Column(name = "price_adjustment")
    private BigDecimal priceAdjustment; // chênh lệch giá (+/-)

    @ManyToOne
    @JoinColumn(name = "vehicle_model_id")
    private VehicleModel vehicleModel;
}

