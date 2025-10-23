package se196411.booking_ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flight")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightsEntity {
    @Id
    @Column(name = "flight_id", nullable = false)
    String flightId;
    @ManyToOne
    @JoinColumn(name = "airplane_id", nullable = false)
    private AirPlaneEntity airplane;
    @ManyToOne
    @JoinColumn(name = "flight_routes_id", nullable = false)
    private FlightRoutesEntity flightRoute;
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;
    @Column(name = "started_time", nullable = false)
    private LocalDateTime startedTime;
    @Column(name = "ended_time", nullable = false)
    private LocalDateTime endedTime;
    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "flight")
    private List<TicketEntity> tickets;
}
