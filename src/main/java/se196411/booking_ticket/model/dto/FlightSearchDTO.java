package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightSearchDTO {
    private String flightId;
    private BigDecimal basePrice;
    private LocalDateTime startedTime;
    private LocalDateTime endedTime;
    private String status;

    // Airplane info
    private AirplaneDTO airplane;

    // Flight route info
    private FlightRouteDTO flightRoute;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AirplaneDTO {
        private String airplaneId;
        private String model;
        private String airline;
        private Integer capacity;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlightRouteDTO {
        private String flightRoutesId;
        private AirportDTO startedAirport;
        private AirportDTO endedAirport;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AirportDTO {
        private String airportId;
        private String code;
        private String name;
        private String place;
    }
}

