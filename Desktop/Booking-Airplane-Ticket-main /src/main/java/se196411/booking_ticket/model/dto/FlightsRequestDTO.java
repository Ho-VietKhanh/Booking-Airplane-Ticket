package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightsRequestDTO {
    private String flightId;
    private String airplaneId;
    private String flightRouteId;
    private BigDecimal basePrice;
    private LocalDateTime startedTime;
    private LocalDateTime endedTime;
    private String status;
}
