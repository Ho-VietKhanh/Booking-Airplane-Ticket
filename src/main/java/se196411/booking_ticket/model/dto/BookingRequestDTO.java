package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private String bookingId;
    private LocalDateTime bookingTime;
    private BigDecimal totalAmount;
    private String status;
    private String paymentId;
    private String userId;
}