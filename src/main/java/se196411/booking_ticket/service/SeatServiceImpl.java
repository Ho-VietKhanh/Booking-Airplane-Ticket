package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.SeatEntity;
import se196411.booking_ticket.model.dto.SeatSelectionDTO;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.repository.SeatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private FlightsRepository flightsRepository;

    @Override
    public List<SeatSelectionDTO> getSeatsByFlightId(String flightId) {
        var flight = flightsRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return new ArrayList<>();
        }

        String airplaneId = flight.getAirplane().getAirplaneId();
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneId(airplaneId);

        return seats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatSelectionDTO> getAvailableSeatsByFlightId(String flightId) {
        var flight = flightsRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return new ArrayList<>();
        }

        String airplaneId = flight.getAirplane().getAirplaneId();
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneIdAndStatus(airplaneId, true);

        return seats.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean reserveSeat(String seatId, String bookingId) {
        var seat = seatRepository.findById(seatId).orElse(null);
        if (seat != null && seat.isAvailable()) {
            seat.setAvailable(false);
            seatRepository.save(seat);
            return true;
        }
        return false;
    }

    @Override
    public SeatSelectionDTO getSeatById(String seatId) {
        var seat = seatRepository.findById(seatId).orElse(null);
        return seat != null ? convertToDTO(seat) : null;
    }

    private SeatSelectionDTO convertToDTO(SeatEntity seat) {
        SeatSelectionDTO dto = new SeatSelectionDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatClass(seat.getSeatClass());
        dto.setAvailable(seat.isAvailable());

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

