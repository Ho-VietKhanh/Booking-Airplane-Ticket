package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.SeatSelectionDTO;

import java.util.List;

public interface SeatService {
    List<SeatSelectionDTO> getSeatsByFlightId(String flightId);
    List<SeatSelectionDTO> getAvailableSeatsByFlightId(String flightId);
    boolean reserveSeat(String seatId, String bookingId);
    SeatSelectionDTO getSeatById(String seatId);
}

