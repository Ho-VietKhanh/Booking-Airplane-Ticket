package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.TicketEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,String> {
    List<TicketEntity> findAllTicketsByBookingBookingId(String bookingId);

    @Query("SELECT DISTINCT t FROM TicketEntity t " +
           "LEFT JOIN FETCH t.seat " +
           "LEFT JOIN FETCH t.flight f " +
           "LEFT JOIN FETCH f.flightRoute fr " +
           "LEFT JOIN FETCH fr.startedAirport " +
           "LEFT JOIN FETCH fr.endedAirport " +
           "LEFT JOIN FETCH t.booking " +
           "LEFT JOIN FETCH t.meal " +
           "LEFT JOIN FETCH t.luggage")
    List<TicketEntity> findAllWithDetails();

    // Query để lấy danh sách seat ID đã được đặt cho một chuyến bay cụ thể
    @Query("SELECT t.seat.seatId FROM TicketEntity t WHERE t.flight.flightId = :flightId AND t.status != 'CANCELLED'")
    List<String> findReservedSeatIdsByFlightId(@Param("flightId") String flightId);

    // Query để lấy danh sách seat number đã được đặt cho một chuyến bay cụ thể
    @Query("SELECT t.seat.seatNumber FROM TicketEntity t WHERE t.flight.flightId = :flightId AND t.status != 'CANCELLED'")
    List<String> findReservedSeatNumbersByFlightId(@Param("flightId") String flightId);
}
