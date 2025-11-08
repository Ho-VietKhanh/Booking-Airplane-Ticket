package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSessionDTO {
    private String email;
    private String phone;
    private String flightId;
    private List<PassengerInfoDTO> passengers = new ArrayList<>();

    public void addPassenger(PassengerInfoDTO passenger) {
        if (this.passengers == null) {
            this.passengers = new ArrayList<>();
        }
        this.passengers.add(passenger);
    }

    public int getPassengerCount() {
        return this.passengers != null ? this.passengers.size() : 0;
    }
}



