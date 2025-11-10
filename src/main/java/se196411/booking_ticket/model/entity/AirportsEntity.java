package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @Column(name = "place", nullable = false)
    private String place;

    @OneToMany(mappedBy = "startedAirport")
    @ToString.Exclude
    List<FlightRoutesEntity> startedFlightRoutes;
    @OneToMany(mappedBy = "endedAirport")
    @ToString.Exclude
    List<FlightRoutesEntity> endedFlightRoutes;
}
