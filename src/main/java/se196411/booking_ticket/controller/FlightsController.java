package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;
import se196411.booking_ticket.service.FlightsService;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightsController {
    @Autowired
    private FlightsService flightsService;

    @PostMapping("/create")
    ResponseEntity<String> insertFlights(@RequestBody FlightsRequestDTO  flightsRequestDTO) {
        if (flightsRequestDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        flightsService.insertFlights(flightsRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{flightId}")
    ResponseEntity<String> updateFlights(@PathVariable String flightId, @RequestBody FlightsRequestDTO  flightsRequestDTO) {
        FlightsResponseDTO findFlight = flightsService.getFlightsByFlightsId(flightId);
        if(findFlight == null) {
            return ResponseEntity.badRequest().build();
        }
        flightsService.updateFlightsByFlightsById(flightId, flightsRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{flightId}")
    ResponseEntity<String> deleteFlights(@PathVariable String flightId) {
        FlightsResponseDTO flightsResponseDTO = flightsService.getFlightsByFlightsId(flightId);
        if(flightsResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        flightsService.deleteFlightByFlightsById(flightId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getFlightById/{flightId}")
    ResponseEntity<FlightsResponseDTO> getFlightById(@PathVariable String flightId) {
        FlightsResponseDTO flightsResponseDTO = flightsService.getFlightsByFlightsId(flightId);
        if(flightsResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(flightsResponseDTO);
    }

    @GetMapping("/getFlightByRoutes/{flightRouteId}")
    ResponseEntity<List<FlightsResponseDTO>> getFlightByRoutes(@PathVariable String flightRouteId) {
        List<FlightsResponseDTO> flightsResponseDTOList = flightsService.getAllFlightsByFlightsRoutes(flightRouteId);
        if(flightsResponseDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(flightsResponseDTOList);
    }

    @GetMapping("/getAllFlight")
    ResponseEntity<List<FlightsResponseDTO>> getAllFlights() {
        List<FlightsResponseDTO> flightsResponseDTOList = flightsService.getAllFlights();
        if(flightsResponseDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(flightsResponseDTOList);
    }

}
