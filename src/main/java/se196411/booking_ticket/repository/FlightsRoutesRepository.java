package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.FlightRoutesEntity;

import java.util.List;

@Repository
public interface FlightsRoutesRepository extends JpaRepository<FlightRoutesEntity, String> {
    List<FlightRoutesEntity> findByStartedAirport_AirportIdAndEndedAirport_AirportId(String startedAirportId, String endedAirportId);
}
