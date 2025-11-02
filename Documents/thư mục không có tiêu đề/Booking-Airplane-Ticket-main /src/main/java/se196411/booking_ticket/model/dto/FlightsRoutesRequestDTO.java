package se196411.booking_ticket.model.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightsRoutesRequestDTO {
    private String flightRoutesId;
    private String startedAirportId;
    private String endedAirportId;
}
