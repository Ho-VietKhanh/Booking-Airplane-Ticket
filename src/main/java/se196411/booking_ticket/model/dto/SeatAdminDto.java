package se196411.booking_ticket.model.dto;

public class SeatAdminDto {
    private String seatId;
    private String airplaneId;
    private String seatNumber;
    private String status; // EMPTY, BOOKED, HELD, MAINTENANCE
    private String ticketId; // if booked

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public String getAirplaneId() { return airplaneId; }
    public void setAirplaneId(String airplaneId) { this.airplaneId = airplaneId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
}

