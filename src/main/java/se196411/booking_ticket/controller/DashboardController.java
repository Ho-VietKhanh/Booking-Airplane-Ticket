package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import se196411.booking_ticket.model.dto.FlightSearchDTO;
import se196411.booking_ticket.model.entity.AirportsEntity;
import se196411.booking_ticket.model.entity.FlightsEntity;
import se196411.booking_ticket.repository.AirportsRepository;
import se196411.booking_ticket.repository.FlightsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private AirportsRepository airportsRepository;

    @Autowired
    private FlightsRepository flightsRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Get all airports for the dropdown
        List<AirportsEntity> airports = airportsRepository.findAll();
        model.addAttribute("airports", airports);
        return "dashboard";
    }

    // API endpoint for AJAX search (trả về JSON, không refresh trang)
    @PostMapping("/dashboard/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchFlightsAjax(
            @RequestParam("departureAirport") String departureAirportId,
            @RequestParam("arrivalAirport") String arrivalAirportId,
            @RequestParam("departureDate") String departureDateStr,
            @RequestParam(value = "returnDate", required = false) String returnDateStr,
            @RequestParam("tripType") String tripType) {

        logger.info("========== AJAX FLIGHT SEARCH ==========");
        logger.info("Departure Airport ID: {}", departureAirportId);
        logger.info("Arrival Airport ID: {}", arrivalAirportId);
        logger.info("Departure Date: {}", departureDateStr);
        logger.info("Trip Type: {}", tripType);

        // Parse dates
        LocalDate departureDate = LocalDate.parse(departureDateStr);
        LocalDateTime departureStart = departureDate.atStartOfDay();
        LocalDateTime departureEnd = departureDate.plusDays(1).atStartOfDay();

        // Search for outbound flights
        List<FlightsEntity> outboundFlightsEntities = flightsRepository.findAll().stream()
                .filter(f -> f.getFlightRoute().getStartedAirport().getAirportId().equals(departureAirportId))
                .filter(f -> f.getFlightRoute().getEndedAirport().getAirportId().equals(arrivalAirportId))
                .filter(f -> !f.getStartedTime().isBefore(departureStart) && f.getStartedTime().isBefore(departureEnd))
                .filter(f -> "AVAILABLE".equalsIgnoreCase(f.getStatus()) || "SCHEDULED".equalsIgnoreCase(f.getStatus()))
                .collect(Collectors.toList());

        // Convert to DTO to avoid circular reference
        List<FlightSearchDTO> outboundFlights = outboundFlightsEntities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        logger.info("Found {} outbound flights", outboundFlights.size());

        Map<String, Object> response = new HashMap<>();
        response.put("outboundFlights", outboundFlights);
        response.put("tripType", tripType);

        // If round trip, search for return flights
        if ("roundTrip".equals(tripType) && returnDateStr != null && !returnDateStr.isEmpty()) {
            LocalDate returnDate = LocalDate.parse(returnDateStr);
            LocalDateTime returnStart = returnDate.atStartOfDay();
            LocalDateTime returnEnd = returnDate.plusDays(1).atStartOfDay();

            List<FlightsEntity> returnFlightsEntities = flightsRepository.findAll().stream()
                    .filter(f -> f.getFlightRoute().getStartedAirport().getAirportId().equals(arrivalAirportId))
                    .filter(f -> f.getFlightRoute().getEndedAirport().getAirportId().equals(departureAirportId))
                    .filter(f -> !f.getStartedTime().isBefore(returnStart) && f.getStartedTime().isBefore(returnEnd))
                    .filter(f -> "AVAILABLE".equalsIgnoreCase(f.getStatus()) || "SCHEDULED".equalsIgnoreCase(f.getStatus()))
                    .collect(Collectors.toList());

            // Convert to DTO
            List<FlightSearchDTO> returnFlights = returnFlightsEntities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            logger.info("Found {} return flights", returnFlights.size());
            response.put("returnFlights", returnFlights);
        }

        logger.info("========================================");

        return ResponseEntity.ok(response);
    }

    // Helper method to convert Entity to DTO
    private FlightSearchDTO convertToDTO(FlightsEntity flight) {
        FlightSearchDTO dto = new FlightSearchDTO();
        dto.setFlightId(flight.getFlightId());
        dto.setBasePrice(flight.getBasePrice());
        dto.setStartedTime(flight.getStartedTime());
        dto.setEndedTime(flight.getEndedTime());
        dto.setStatus(flight.getStatus());

        // Airplane info
        FlightSearchDTO.AirplaneDTO airplaneDTO = new FlightSearchDTO.AirplaneDTO();
        airplaneDTO.setAirplaneId(flight.getAirplane().getAirplaneId());
        airplaneDTO.setModel(flight.getAirplane().getModel());
        airplaneDTO.setAirline(flight.getAirplane().getAirline());
        airplaneDTO.setCapacity(flight.getAirplane().getCapacity());
        dto.setAirplane(airplaneDTO);

        // Flight route info
        FlightSearchDTO.FlightRouteDTO routeDTO = new FlightSearchDTO.FlightRouteDTO();
        routeDTO.setFlightRoutesId(flight.getFlightRoute().getFlightRoutesId());

        // Started airport
        FlightSearchDTO.AirportDTO startedAirportDTO = new FlightSearchDTO.AirportDTO();
        startedAirportDTO.setAirportId(flight.getFlightRoute().getStartedAirport().getAirportId());
        startedAirportDTO.setCode(flight.getFlightRoute().getStartedAirport().getCode());
        startedAirportDTO.setName(flight.getFlightRoute().getStartedAirport().getName());
        startedAirportDTO.setPlace(flight.getFlightRoute().getStartedAirport().getPlace());
        routeDTO.setStartedAirport(startedAirportDTO);

        // Ended airport
        FlightSearchDTO.AirportDTO endedAirportDTO = new FlightSearchDTO.AirportDTO();
        endedAirportDTO.setAirportId(flight.getFlightRoute().getEndedAirport().getAirportId());
        endedAirportDTO.setCode(flight.getFlightRoute().getEndedAirport().getCode());
        endedAirportDTO.setName(flight.getFlightRoute().getEndedAirport().getName());
        endedAirportDTO.setPlace(flight.getFlightRoute().getEndedAirport().getPlace());
        routeDTO.setEndedAirport(endedAirportDTO);

        dto.setFlightRoute(routeDTO);

        return dto;
    }

    // Old endpoint giữ lại cho backward compatibility (nếu cần)
    @PostMapping("/dashboard/search-page")
    public String searchFlightsPage(
            @RequestParam("departureAirport") String departureAirportId,
            @RequestParam("arrivalAirport") String arrivalAirportId,
            @RequestParam("departureDate") String departureDateStr,
            @RequestParam(value = "returnDate", required = false) String returnDateStr,
            @RequestParam("tripType") String tripType,
            Model model) {

        logger.info("========== FLIGHT SEARCH DEBUG ==========");
        logger.info("Departure Airport ID: {}", departureAirportId);
        logger.info("Arrival Airport ID: {}", arrivalAirportId);
        logger.info("Departure Date: {}", departureDateStr);
        logger.info("Trip Type: {}", tripType);

        // Get all airports for the dropdown (in case we need to show the form again)
        List<AirportsEntity> airports = airportsRepository.findAll();
        model.addAttribute("airports", airports);

        // Parse dates
        LocalDate departureDate = LocalDate.parse(departureDateStr);
        LocalDateTime departureStart = departureDate.atStartOfDay();
        LocalDateTime departureEnd = departureDate.plusDays(1).atStartOfDay();

        logger.info("Search Date Range: {} to {}", departureStart, departureEnd);

        // Get all flights first for debugging
        List<FlightsEntity> allFlights = flightsRepository.findAll();
        logger.info("Total flights in DB: {}", allFlights.size());

        allFlights.forEach(f -> {
            logger.info("Flight: {} | From: {} ({}) | To: {} ({}) | Date: {} | Status: {}",
                f.getFlightId(),
                f.getFlightRoute().getStartedAirport().getName(),
                f.getFlightRoute().getStartedAirport().getAirportId(),
                f.getFlightRoute().getEndedAirport().getName(),
                f.getFlightRoute().getEndedAirport().getAirportId(),
                f.getStartedTime(),
                f.getStatus()
            );
        });

        // Search for flights - FIX: Use >= and < for proper date range matching
        List<FlightsEntity> outboundFlights = flightsRepository.findAll().stream()
                .filter(f -> f.getFlightRoute().getStartedAirport().getAirportId().equals(departureAirportId))
                .filter(f -> f.getFlightRoute().getEndedAirport().getAirportId().equals(arrivalAirportId))
                .filter(f -> !f.getStartedTime().isBefore(departureStart) && f.getStartedTime().isBefore(departureEnd))
                .filter(f -> "AVAILABLE".equalsIgnoreCase(f.getStatus()) || "SCHEDULED".equalsIgnoreCase(f.getStatus()))
                .collect(Collectors.toList());

        logger.info("Found {} matching flights", outboundFlights.size());

        model.addAttribute("outboundFlights", outboundFlights);
        model.addAttribute("tripType", tripType);

        // If round trip, search for return flights
        if ("roundTrip".equals(tripType) && returnDateStr != null && !returnDateStr.isEmpty()) {
            LocalDate returnDate = LocalDate.parse(returnDateStr);
            LocalDateTime returnStart = returnDate.atStartOfDay();
            LocalDateTime returnEnd = returnDate.plusDays(1).atStartOfDay();

            List<FlightsEntity> returnFlights = flightsRepository.findAll().stream()
                    .filter(f -> f.getFlightRoute().getStartedAirport().getAirportId().equals(arrivalAirportId))
                    .filter(f -> f.getFlightRoute().getEndedAirport().getAirportId().equals(departureAirportId))
                    .filter(f -> !f.getStartedTime().isBefore(returnStart) && f.getStartedTime().isBefore(returnEnd))
                    .filter(f -> "AVAILABLE".equalsIgnoreCase(f.getStatus()) || "SCHEDULED".equalsIgnoreCase(f.getStatus()))
                    .collect(Collectors.toList());

            logger.info("Found {} return flights", returnFlights.size());
            model.addAttribute("returnFlights", returnFlights);
        }

        // Pass search params back to view
        model.addAttribute("departureAirportId", departureAirportId);
        model.addAttribute("arrivalAirportId", arrivalAirportId);
        model.addAttribute("departureDate", departureDateStr);
        model.addAttribute("returnDate", returnDateStr);

        logger.info("========================================");

        return "dashboard";
    }
}
