package se196411.booking_ticket.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.dto.TicketDetailDto;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.repository.TicketRepository;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {
    private final TicketRepository ticketRepository;

    public AdminApiController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<?> getTicketDetail(@PathVariable String ticketId) {
        return ticketRepository.findById(ticketId)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<?> getTicketDetailAlt(@PathVariable String ticketId) {
        return ticketRepository.findById(ticketId)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private TicketDetailDto toDto(TicketEntity t) {
        TicketDetailDto dto = new TicketDetailDto();
        dto.setTicketId(t.getTicketId());
        dto.setFirstName(t.getFirstName());
        dto.setLastName(t.getLastName());
        dto.setEmail(t.getEmail());
        dto.setCccd(t.getCccd());
        dto.setPhone(t.getSdt());
        if(t.getSeat() != null) {
            dto.setSeatId(t.getSeat().getSeatId());
            dto.setSeatNumber(t.getSeat().getSeatNumber());
        }
        if(t.getBooking() != null) dto.setBookingId(t.getBooking().getBookingId());
        dto.setStatus(t.getStatus());
        dto.setPrice(t.getPrice());
        return dto;
    }
}
