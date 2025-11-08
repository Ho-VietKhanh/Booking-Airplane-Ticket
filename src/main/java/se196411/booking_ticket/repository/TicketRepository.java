package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.model.dto.TicketResponseDTO;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,String> {
    public List<TicketResponseDTO> findAllTicketsByBookingBookingId(String bookingId);

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
}
