package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequestDTO {
    private String ticketId;
    private BigDecimal price;
    private String status;
    private String flightId;
    private String bookingId;
    private String seatId;
}