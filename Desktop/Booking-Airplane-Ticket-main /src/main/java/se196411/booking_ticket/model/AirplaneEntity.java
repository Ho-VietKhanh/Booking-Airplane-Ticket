package se196411.booking_ticket.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "airplane")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirplaneEntity {
    @Id
    @Column(name = "airplane_id", nullable = false)
    private String airplaneId;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "airline", nullable = false)
    private String airline;

    @OneToMany(mappedBy = "airplane")
    private List<SeatEntity> seats;

    @OneToMany(mappedBy = "airplane")
    private List<FlightsEntity> flights;

}