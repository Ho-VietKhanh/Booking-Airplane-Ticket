package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.model.dto.TicketResponseDTO;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,String> {
    @Query("SELECT new se196411.booking_ticket.model.dto.TicketResponseDTO(" +
           "t.ticketId, t.price, t.status, t.flight.flightId, t.booking.bookingId, t.seat.seatId) " +
           "FROM TicketEntity t WHERE t.booking.bookingId = :bookingId")
    List<TicketResponseDTO> findAllTicketsByBookingBookingId(@Param("bookingId") String bookingId);
}


