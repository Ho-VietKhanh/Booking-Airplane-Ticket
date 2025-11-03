package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.dto.AirportRequestDTO;
import se196411.booking_ticket.model.dto.AirportResponseDTO;
import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;
import se196411.booking_ticket.service.AirportService;

import java.util.List;

@RestController
@RequestMapping("/api/airport")
public class AirportController {
    @Autowired
    private AirportService airportService;

    @PostMapping("/create")
    ResponseEntity<String> insertAirport(@RequestBody AirportRequestDTO airportRequestDTO) {
        if (airportRequestDTO == null) {
            return ResponseEntity.notFound().build();
        }
        airportService.insertAirport(airportRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{airportId}")
    ResponseEntity<String> updateAirports(@PathVariable String airportId, @RequestBody AirportRequestDTO  airportRequestDTO) {
        AirportResponseDTO airport = airportService.getAirportByAirportId(airportId);
        if(airport == null) {
            return ResponseEntity.notFound().build();
        }
        airportService.updateAirportByAirportId(airportId, airportRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{airportId}")
    ResponseEntity<String> deleteAirport(@PathVariable String airportId) {
        AirportResponseDTO airport = airportService.getAirportByAirportId(airportId);
        if(airport == null) {
            return ResponseEntity.notFound().build();
        }
        airportService.deleteAirportByAirportId(airportId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAirportById/{airportId}")
    ResponseEntity<AirportResponseDTO> getAirportById(@PathVariable String airportId) {
        AirportResponseDTO airport = airportService.getAirportByAirportId(airportId);
        if(airport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(airport);
    }

    @PostMapping("/getAirportByName")
    ResponseEntity<AirportResponseDTO> getAirportByName(@RequestBody AirportRequestDTO request) {
        AirportResponseDTO airport = airportService.getAirportByAirportName(request.getName());
        if(airport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(airport);
    }

    @PostMapping("/getAirportByPlace")
    ResponseEntity<AirportResponseDTO> getAirportByPlace(@RequestBody AirportRequestDTO request) {
        AirportResponseDTO airport = airportService.getAirportByAirportPlace(request.getPlace());
        if(airport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(airport);
    }

    @GetMapping("/getAllAirport")
    ResponseEntity<List<AirportResponseDTO>> getAllAirport(){
        List<AirportResponseDTO> airport = airportService.getAllAirports();
        if(airport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(airport);
    }



}
