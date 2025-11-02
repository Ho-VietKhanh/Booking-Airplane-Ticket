package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;
import se196411.booking_ticket.model.dto.FlightsRoutesRequestDTO;
import se196411.booking_ticket.model.dto.FlightsRoutesResponseDTO;

import java.util.List;

public interface FlightsRoutesService {
    boolean insertFlightRoutes(FlightsRoutesRequestDTO flightsRoutesRequestDTO);
    boolean updateFlightRoutesByFlightRouteId(String flightRoutesId, FlightsRoutesRequestDTO flightsRoutesRequestDTO);
    boolean deleteFlightRoutesByFlightRouteId(String flightRoutesId);
    FlightsRoutesResponseDTO getFlightRoutesByFlightRouteId(String flightRoutesId);
    List<FlightsRoutesResponseDTO> getFlightRoutesByAirportId(String startAirportId, String endAirportId);
    List<FlightsRoutesResponseDTO> getAllFlightRoutes();
}
