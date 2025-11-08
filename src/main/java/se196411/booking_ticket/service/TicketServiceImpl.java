package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.model.dto.TicketResponseDTO;
import se196411.booking_ticket.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public TicketEntity create(TicketEntity ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public TicketEntity findById(String ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    @Override
    public List<TicketEntity> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public TicketEntity update(String ticketId, TicketEntity ticket) {
        Optional<TicketEntity> existing = ticketRepository.findById(ticketId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Ticket not found: " + ticketId);
        }
        TicketEntity t = existing.get();
        // update fields except id
        t.setPrice(ticket.getPrice());
        t.setCccd(ticket.getCccd());
        t.setFirstName(ticket.getFirstName());
        t.setSdt(ticket.getSdt());
        t.setLastName(ticket.getLastName());
        t.setNationality(ticket.getNationality());
        t.setTitle(ticket.getTitle());
        t.setGender(ticket.getGender());
        t.setBirthDate(ticket.getBirthDate());
        t.setEmail(ticket.getEmail());
        t.setStatus(ticket.getStatus());
        t.setFlight(ticket.getFlight());
        t.setBooking(ticket.getBooking());
        t.setSeat(ticket.getSeat());
        t.setMeal(ticket.getMeal());
        t.setLuggage(ticket.getLuggage());
        return ticketRepository.save(t);
    }

    @Override
    public void deleteById(String ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsByBookingId(String bookingId) {
        return ticketRepository.findAllTicketsByBookingBookingId(bookingId);
    }
}
