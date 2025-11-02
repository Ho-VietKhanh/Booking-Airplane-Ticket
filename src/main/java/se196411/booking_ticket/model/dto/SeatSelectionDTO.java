package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatSelectionDTO {
    private String seatId;
    private String seatNumber;
    private String seatClass;
    private boolean isAvailable;
    private int row;
    private String column;
}

