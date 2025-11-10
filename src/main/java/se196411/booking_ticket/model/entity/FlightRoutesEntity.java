package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "flight_routes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightRoutesEntity {
    @Id
    @Column(name = "flight_routes_id", nullable = false)
    String flightRoutesId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "started_airport_id", nullable = false)
    private AirportsEntity startedAirport;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ended_airport_id", nullable = false)
    private AirportsEntity endedAirport;
    @OneToMany(mappedBy = "flightRoute")
    private List<FlightsEntity> flights;
}
