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
        List<TicketEntity> tickets = ticketRepository.findAllTicketsByBookingBookingId(bookingId);
        return tickets.stream()
                .map(ticket -> {
                    TicketResponseDTO dto = new TicketResponseDTO();
                    dto.setTicketId(ticket.getTicketId());
                    dto.setPrice(ticket.getPrice());
                    dto.setStatus(ticket.getStatus());
                    dto.setFlightId(ticket.getFlight() != null ? ticket.getFlight().getFlightId() : null);
                    dto.setBookingId(ticket.getBooking() != null ? ticket.getBooking().getBookingId() : null);
                    dto.setSeatId(ticket.getSeat() != null ? ticket.getSeat().getSeatId() : null);
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public TicketResponseDTO getTicketByTicketId(String ticketId) {
        Optional<TicketEntity> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isEmpty()) {
            return null;
        }

        TicketEntity ticket = ticketOpt.get();
        TicketResponseDTO dto = new TicketResponseDTO();

        // Basic ticket information
        dto.setTicketId(ticket.getTicketId());
        dto.setPrice(ticket.getPrice());
        dto.setStatus(ticket.getStatus());

        // Passenger information
        dto.setCccd(ticket.getCccd());
        dto.setFirstName(ticket.getFirstName());
        dto.setSdt(ticket.getSdt());
        dto.setLastName(ticket.getLastName());
        dto.setNationality(ticket.getNationality());
        dto.setTitle(ticket.getTitle());
        dto.setGender(ticket.getGender());
        dto.setBirthDate(ticket.getBirthDate());
        dto.setEmail(ticket.getEmail());

        // Related entities
        dto.setFlightId(ticket.getFlight() != null ? ticket.getFlight().getFlightId() : null);
        dto.setBookingId(ticket.getBooking() != null ? ticket.getBooking().getBookingId() : null);
        dto.setSeatId(ticket.getSeat() != null ? ticket.getSeat().getSeatId() : null);
        dto.setMealId(ticket.getMeal() != null ? String.valueOf(ticket.getMeal().getMealId()) : null);
        dto.setLuggageId(ticket.getLuggage() != null ? String.valueOf(ticket.getLuggage().getLuggageId()) : null);
        return dto;
    }
}
