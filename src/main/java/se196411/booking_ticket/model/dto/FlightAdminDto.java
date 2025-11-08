package se196411.booking_ticket.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightAdminDto {
    private String flightId;
    private String airplaneId;
    private String fromAirport;
    private String toAirport;
    private LocalDateTime startedTime;
    private LocalDateTime endedTime;
    private BigDecimal basePrice;
    private String status;

    // getters & setters
    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }
    public String getAirplaneId() { return airplaneId; }
    public void setAirplaneId(String airplaneId) { this.airplaneId = airplaneId; }
    public String getFromAirport() { return fromAirport; }
    public void setFromAirport(String fromAirport) { this.fromAirport = fromAirport; }
    public String getToAirport() { return toAirport; }
    public void setToAirport(String toAirport) { this.toAirport = toAirport; }
    public LocalDateTime getStartedTime() { return startedTime; }
    public void setStartedTime(LocalDateTime startedTime) { this.startedTime = startedTime; }
    public LocalDateTime getEndedTime() { return endedTime; }
    public void setEndedTime(LocalDateTime endedTime) { this.endedTime = endedTime; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

