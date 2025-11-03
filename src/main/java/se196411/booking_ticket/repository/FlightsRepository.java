package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.FlightsEntity;

import java.util.List;

@Repository
public interface FlightsRepository extends JpaRepository<FlightsEntity, String> {
    List<FlightsEntity> findByFlightRoute_FlightRoutesId(String flightRoutesId);
}
