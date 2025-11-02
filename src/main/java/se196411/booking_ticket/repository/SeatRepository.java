package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.SeatEntity;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, String> {
    @Query("SELECT s FROM SeatEntity s WHERE s.airplane.airplaneId = :airplaneId")
    List<SeatEntity> findSeatsByAirplaneId(@Param("airplaneId") String airplaneId);

    @Query("SELECT s FROM SeatEntity s WHERE s.airplane.airplaneId = :airplaneId AND s.isAvailable = :status")
    List<SeatEntity> findSeatsByAirplaneIdAndStatus(@Param("airplaneId") String airplaneId, @Param("status") boolean status);
}
