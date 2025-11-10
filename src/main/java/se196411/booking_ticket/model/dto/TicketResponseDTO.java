package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {
    private String ticketId;
    private BigDecimal price;
    private String status;
    private String flightId;
    private String bookingId;
    private String seatId;

    // Passenger information
    private String cccd;
    private String firstName;
    private String sdt;
    private String lastName;
    private String nationality;
    private String title;
    private Boolean gender;
    private Date birthDate;
    private String email;

    // Additional services
    private String mealId;
    private String luggageId;
}