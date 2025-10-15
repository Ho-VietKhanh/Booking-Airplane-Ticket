package se196411.booking_ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "airport")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirportsEntity {
    @Id
    @Column(name = "airport_id", nullable = false)
    private String airportId;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "country", nullable = false)
    private String country;

    @OneToMany(mappedBy = "startedAirport")
    List<FlightRoutesEntity> startedFlightRoutes;
    @OneToMany(mappedBy = "endedAirport")
    List<FlightRoutesEntity> endedFlightRoutes;
}
