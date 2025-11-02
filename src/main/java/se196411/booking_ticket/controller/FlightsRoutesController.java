package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.FlightRoutesEntity;
import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;
import se196411.booking_ticket.model.dto.FlightsRoutesRequestDTO;
import se196411.booking_ticket.model.dto.FlightsRoutesResponseDTO;
import se196411.booking_ticket.service.FlightsRoutesService;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class FlightsRoutesController {
    @Autowired
    private FlightsRoutesService flightsRoutesService;

    @PostMapping("/create")
    ResponseEntity<String> insertFlightsRoutes(@RequestBody FlightsRoutesRequestDTO flightsRoutesRequestDTO) {
        if (flightsRoutesRequestDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        flightsRoutesService.insertFlightRoutes(flightsRoutesRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{flightRoutesId}")
    ResponseEntity<String> updateFlightsRoutes(@PathVariable String flightRoutesId, @RequestBody FlightsRoutesRequestDTO flightsRoutesRequestDTO) {
        FlightsRoutesResponseDTO flightsRoutesResponseDTO = flightsRoutesService.getFlightRoutesByFlightRouteId(flightRoutesId);
        if(flightsRoutesResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        flightsRoutesService.updateFlightRoutesByFlightRouteId(flightRoutesId, flightsRoutesRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{flightRoutesId}")
    ResponseEntity<String> deleteFlightsRoutes(@PathVariable String flightRoutesId) {
        FlightsRoutesResponseDTO flightsRoutesResponseDTO = flightsRoutesService.getFlightRoutesByFlightRouteId(flightRoutesId);
        if(flightsRoutesResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        flightsRoutesService.deleteFlightRoutesByFlightRouteId(flightRoutesId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getRoutes/{flightRoutesId}")
    ResponseEntity<String> getRoutedFlightsRoutes(@PathVariable String flightRoutesId) {
        FlightsRoutesResponseDTO flightsRoutesResponseDTO = flightsRoutesService.getFlightRoutesByFlightRouteId(flightRoutesId);
        if(flightsRoutesResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(flightsRoutesResponseDTO.toString());
    }

    @GetMapping("/getRoutesByAirport")
    ResponseEntity<List<FlightsRoutesResponseDTO>> getRoutesByAirport(@RequestParam FlightsRoutesRequestDTO request) {
        List<FlightsRoutesResponseDTO> flightsRoutesResponseDTOList =
            flightsRoutesService.getFlightRoutesByAirportId(request.getStartedAirportId(), request.getEndedAirportId());
        if(flightsRoutesResponseDTOList == null || flightsRoutesResponseDTOList.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(flightsRoutesResponseDTOList);
    }

    @GetMapping("/getAllRoutes")
    ResponseEntity<List<FlightsRoutesResponseDTO>> getAllRoutes() {
        List<FlightsRoutesResponseDTO>  flightsRoutesResponseDTOList = flightsRoutesService.getAllFlightRoutes();
        if(flightsRoutesResponseDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(flightsRoutesResponseDTOList);
    }


}
