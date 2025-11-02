package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.AirplaneEntity;
import se196411.booking_ticket.model.SeatEntity;

import java.util.List;

@Repository
public interface AirplaneRepository extends JpaRepository<AirplaneEntity, String> {
    List<SeatEntity> findSeatsByAirplaneId(String airplaneId);
    boolean existsByAirplaneId(String airplaneId);
}
