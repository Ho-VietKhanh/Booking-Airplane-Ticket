package swp.project.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_models")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "model_code", unique = true, nullable = false)
    private String modelCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "brand")
    private String brand;

    @Column(name = "year")
    private Integer year;

    @Column(name = "battery_capacity")
    private Integer batteryCapacity; // kWh

    @Column(name = "range_km")
    private Integer rangeKm;

    @Column(name = "charging_time")
    private Integer chargingTime; // minutes

    @Column(name = "max_speed")
    private Integer maxSpeed; // km/h

    @Column(name = "acceleration")
    private BigDecimal acceleration; // 0-100 km/h in seconds

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "cargo_volume")
    private BigDecimal cargoVolume; // liters

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Mối quan hệ với Vehicle (One-to-Many)
    @OneToMany(mappedBy = "vehicleModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "vehicleModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();

}
