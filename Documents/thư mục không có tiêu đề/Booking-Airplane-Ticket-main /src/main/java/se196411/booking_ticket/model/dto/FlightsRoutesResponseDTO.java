package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightsRoutesResponseDTO {
    private String flightRoutesId;
    private String startedAirportId;
    private String endedAirportId;
}
