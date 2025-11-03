package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.TicketEntity;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity,String> {
}
