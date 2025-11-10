package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.SeatSelectionDTO;

import java.util.List;
import java.util.Set;

public interface SeatService {
    List<SeatSelectionDTO> getSeatsByFlightId(String flightId);
    List<SeatSelectionDTO> getAvailableSeatsByFlightId(String flightId);
    boolean reserveSeat(String seatId, String bookingId);
    SeatSelectionDTO getSeatById(String seatId);

    // Lấy danh sách seat ID đã được đặt cho chuyến bay cụ thể
    Set<String> getReservedSeatIdsByFlightId(String flightId);
}
