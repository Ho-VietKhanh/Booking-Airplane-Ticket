package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.FlightRoutesEntity;

@Repository
public interface FlightRoutesRepository extends JpaRepository<FlightRoutesEntity, String> {
}

