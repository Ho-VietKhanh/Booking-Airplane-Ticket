package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.AirportRequestDTO;
import se196411.booking_ticket.model.dto.AirportResponseDTO;
import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;

import java.util.List;

public interface AirportService {
    boolean insertAirport(AirportRequestDTO airportRequestDTO);
    boolean updateAirportByAirportId(String airportId, AirportRequestDTO airportRequestDTO);
    boolean deleteAirportByAirportId(String airportId);
    AirportResponseDTO getAirportByAirportId(String airportId);
    AirportResponseDTO getAirportByAirportName(String airportName);
    AirportResponseDTO getAirportByAirportPlace(String airportPlace);
    List<AirportResponseDTO> getAllAirports();
}
