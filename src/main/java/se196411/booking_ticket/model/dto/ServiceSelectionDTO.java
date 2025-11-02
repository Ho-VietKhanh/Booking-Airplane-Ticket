package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSelectionDTO {
    private String bookingId;
    private String flightId;
    private int passengerCount;
    private boolean needSeatSelection;
    private boolean needBaggageSelection;
    private boolean needMealSelection;
}

