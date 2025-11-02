package se196411.booking_ticket.model.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingSessionDTO {
    private String email;
    private String phone;
    private String flightId;
    private List<PassengerInfoDTO> passengers;

    public BookingSessionDTO() {
        this.passengers = new ArrayList<>();
    }

    public BookingSessionDTO(String email, String phone, String flightId) {
        this.email = email;
        this.phone = phone;
        this.flightId = flightId;
        this.passengers = new ArrayList<>();
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public List<PassengerInfoDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerInfoDTO> passengers) {
        this.passengers = passengers;
    }

    public void addPassenger(PassengerInfoDTO passenger) {
        this.passengers.add(passenger);
    }

    public int getPassengerCount() {
        return passengers.size();
    }
}



