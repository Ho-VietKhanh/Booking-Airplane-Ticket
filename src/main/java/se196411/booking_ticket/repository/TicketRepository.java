package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.TicketEntity;
import se196411.booking_ticket.model.dto.TicketResponseDTO;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,String> {
    public List<TicketResponseDTO> findAllTicketsByBookingId(String bookingId);
}
