package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.SeatEntity;
import se196411.booking_ticket.model.dto.SeatSelectionDTO;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.repository.SeatRepository;
import se196411.booking_ticket.repository.TicketRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private FlightsRepository flightsRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public List<SeatSelectionDTO> getSeatsByFlightId(String flightId) {
        var flight = flightsRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return new ArrayList<>();
        }

        String airplaneId = flight.getAirplane().getAirplaneId();
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneId(airplaneId);

        // Lấy danh sách seat ID đã được đặt cho chuyến bay này
        Set<String> reservedSeatIds = getReservedSeatIdsByFlightId(flightId);

        return seats.stream()
                .map(seat -> convertToDTO(seat, reservedSeatIds.contains(seat.getSeatId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatSelectionDTO> getAvailableSeatsByFlightId(String flightId) {
        var flight = flightsRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return new ArrayList<>();
        }

        String airplaneId = flight.getAirplane().getAirplaneId();
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneId(airplaneId);

        // Lấy danh sách seat ID đã được đặt cho chuyến bay này
        Set<String> reservedSeatIds = getReservedSeatIdsByFlightId(flightId);

        return seats.stream()
                .filter(seat -> !reservedSeatIds.contains(seat.getSeatId()))
                .map(seat -> convertToDTO(seat, false))
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getReservedSeatIdsByFlightId(String flightId) {
        List<String> reservedSeats = ticketRepository.findReservedSeatIdsByFlightId(flightId);
        return new HashSet<>(reservedSeats);
    }

    @Override
    public boolean reserveSeat(String seatId, String bookingId) {
        var seat = seatRepository.findById(seatId).orElse(null);
        if (seat != null) {
            // Không cần thay đổi isAvailable nữa vì chúng ta kiểm tra theo flight_id
            return true;
        }
        return false;
    }

    @Override
    public SeatSelectionDTO getSeatById(String seatId) {
        var seat = seatRepository.findById(seatId).orElse(null);
        return seat != null ? convertToDTO(seat, false) : null;
    }

    private SeatSelectionDTO convertToDTO(SeatEntity seat, boolean isReserved) {
        SeatSelectionDTO dto = new SeatSelectionDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatClass(seat.getSeatClass());
        // Ghế available khi không bị reserved cho chuyến bay này
        dto.setAvailable(!isReserved);

        // Parse seat number to get row and column (e.g., "12A" -> row=12, column="A")
        String seatNumber = seat.getSeatNumber();
        try {
            int row = Integer.parseInt(seatNumber.replaceAll("[^0-9]", ""));
            String column = seatNumber.replaceAll("[0-9]", "");
            dto.setRow(row);
            dto.setColumn(column);
        } catch (Exception e) {
            dto.setRow(0);
            dto.setColumn("");
        }

        return dto;
    }
}
