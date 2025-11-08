package se196411.booking_ticket.model.dto;

public class AirplaneAdminDto {
    private String airplaneId;
    private String model;
    private int capacity;
    private String airline;

    public String getAirplaneId() { return airplaneId; }
    public void setAirplaneId(String airplaneId) { this.airplaneId = airplaneId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }
}

