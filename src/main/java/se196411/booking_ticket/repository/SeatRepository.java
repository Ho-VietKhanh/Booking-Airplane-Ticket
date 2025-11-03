package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.SeatEntity;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, String> {
    List<SeatEntity> findSeatsByAirplaneAirplaneId(String airplaneId);

    List<SeatEntity> findSeatsByAirplaneAirplaneIdAndIsAvailable(String airplaneAirplaneId, boolean isAvailable);
}
