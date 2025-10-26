package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.SeatEntity;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, String> {
    List<SeatEntity> findSeatsByAirplaneId(String airplaneId);

    List<SeatEntity> findSeatsByAirplaneIdAndStatus(String airplaneId, String status);
}
