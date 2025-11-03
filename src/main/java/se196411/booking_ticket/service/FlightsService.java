package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;

import java.util.List;

public interface FlightsService {
    boolean insertFlights(FlightsRequestDTO flightsRequestDTO);
    boolean updateFlightsByFlightsById(String flightsId, FlightsRequestDTO flightsRequestDTO);
    boolean deleteFlightByFlightsById(String flightsId);
    FlightsResponseDTO getFlightsByFlightsId(String flightsId);
    List<FlightsResponseDTO> getAllFlights();
    List<FlightsResponseDTO> getAllFlightsByFlightsRoutes(String flightRoutesId);
}
