package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.AirportsEntity;
import se196411.booking_ticket.model.FlightRoutesEntity;
import se196411.booking_ticket.model.dto.FlightsRoutesRequestDTO;
import se196411.booking_ticket.model.dto.FlightsRoutesResponseDTO;
import se196411.booking_ticket.repository.AirportsRepository;
import se196411.booking_ticket.repository.FlightsRoutesRepository;

import java.util.ArrayList;
import java.util.List;

import static se196411.booking_ticket.utils.RandomId.generateRandomId;

@Service
public class FlightsRouteServiceImpl implements FlightsRoutesService{
    @Autowired
    FlightsRoutesRepository flightsRoutesRepository;
    @Autowired
    AirportsRepository airportsRepository;

    private int characterLength = 5;
    private int numberLength = 5;

    public String generateUniqueId() {
        String newId;
        do {
            newId = generateRandomId(characterLength, numberLength);
        } while (flightsRoutesRepository.existsById(newId));
        return newId;
    }


    @Override
    public boolean insertFlightRoutes(FlightsRoutesRequestDTO flightsRoutesRequestDTO) {
        if (flightsRoutesRequestDTO == null) {
            return false;
        }
        AirportsEntity startedAirportsEntity = airportsRepository.findById(flightsRoutesRequestDTO.getStartedAirportId()).orElse(null);
        if (startedAirportsEntity == null) {
            return false;
        }
        AirportsEntity endedAirportsEntity = airportsRepository.findById(flightsRoutesRequestDTO.getEndedAirportId()).orElse(null);
        if (endedAirportsEntity == null) {
            return false;
        }

        FlightRoutesEntity  flightRoutesEntity = new FlightRoutesEntity();
        flightRoutesEntity.setFlightRoutesId(generateUniqueId());
        flightRoutesEntity.setStartedAirport(startedAirportsEntity);
        flightRoutesEntity.setEndedAirport(endedAirportsEntity);
        flightsRoutesRepository.save(flightRoutesEntity);
        return true;
    }

    @Override
    public boolean updateFlightRoutesByFlightRouteId(String flightRoutesId, FlightsRoutesRequestDTO flightsRoutesRequestDTO) {
        FlightRoutesEntity  flightRoutesEntity = flightsRoutesRepository.findById(flightRoutesId).orElse(null);
        if (flightRoutesEntity == null) {
            return false;
        }
        AirportsEntity startedAirportsEntity = airportsRepository.findById(flightsRoutesRequestDTO.getStartedAirportId()).orElse(null);
        if (startedAirportsEntity == null) {
            return false;
        }
        AirportsEntity endedAirportsEntity = airportsRepository.findById(flightsRoutesRequestDTO.getEndedAirportId()).orElse(null);
        if (endedAirportsEntity == null) {
            return false;
        }

        flightRoutesEntity.setStartedAirport(startedAirportsEntity);
        flightRoutesEntity.setEndedAirport(endedAirportsEntity);
        flightsRoutesRepository.save(flightRoutesEntity);
        return true;
    }

    @Override
    public boolean deleteFlightRoutesByFlightRouteId(String flightRoutesId) {
        FlightRoutesEntity  flightRoutesEntity = flightsRoutesRepository.findById(flightRoutesId).orElse(null);
        if (flightRoutesEntity == null) {
            return false;
        }
        flightsRoutesRepository.delete(flightRoutesEntity);
        return true;
    }

    @Override
    public FlightsRoutesResponseDTO getFlightRoutesByFlightRouteId(String flightRoutesId) {
        FlightRoutesEntity  flightRoutesEntity = flightsRoutesRepository.findById(flightRoutesId).orElse(null);
        if (flightRoutesEntity == null) {
            throw new RuntimeException("FlightRoutesEntity is null");
        }
        FlightsRoutesResponseDTO flightsRoutesResponseDTO = new FlightsRoutesResponseDTO();
        flightsRoutesResponseDTO.setFlightRoutesId(flightRoutesEntity.getFlightRoutesId());
        flightsRoutesResponseDTO.setStartedAirportId(flightRoutesEntity.getStartedAirport().getAirportId());
        flightsRoutesResponseDTO.setEndedAirportId(flightRoutesEntity.getEndedAirport().getAirportId());

        return flightsRoutesResponseDTO;
    }

    @Override
    public List<FlightsRoutesResponseDTO> getFlightRoutesByAirportId(String startAirportId, String endAirportId) {
        List<FlightRoutesEntity> flightRoutesEntityList = flightsRoutesRepository.findByStartedAirport_AirportIdAndEndedAirport_AirportId(startAirportId, endAirportId);
        List<FlightsRoutesResponseDTO> flightsRoutesResponseDTOList = new ArrayList<>();
        for (FlightRoutesEntity flightRoutesEntity : flightRoutesEntityList) {
            FlightsRoutesResponseDTO flightsRoutesResponseDTO = new FlightsRoutesResponseDTO();
            flightsRoutesResponseDTO.setFlightRoutesId(flightRoutesEntity.getFlightRoutesId());
            flightsRoutesResponseDTO.setStartedAirportId(flightRoutesEntity.getStartedAirport().getAirportId());
            flightsRoutesResponseDTO.setEndedAirportId(flightRoutesEntity.getEndedAirport().getAirportId());
            flightsRoutesResponseDTOList.add(flightsRoutesResponseDTO);
        }
        return flightsRoutesResponseDTOList;
    }

    @Override
    public List<FlightsRoutesResponseDTO> getAllFlightRoutes() {
        List<FlightRoutesEntity> flightRoutesEntityList = flightsRoutesRepository.findAll();
        List<FlightsRoutesResponseDTO> flightsRoutesResponseDTOList = new ArrayList<>();
        for (FlightRoutesEntity flightRoutesEntity : flightRoutesEntityList) {
            FlightsRoutesResponseDTO flightsRoutesResponseDTO = new FlightsRoutesResponseDTO();
            flightsRoutesResponseDTO.setFlightRoutesId(flightRoutesEntity.getFlightRoutesId());
            flightsRoutesResponseDTO.setStartedAirportId(flightRoutesEntity.getStartedAirport().getAirportId());
            flightsRoutesResponseDTO.setEndedAirportId(flightRoutesEntity.getEndedAirport().getAirportId());
            flightsRoutesResponseDTOList.add(flightsRoutesResponseDTO);
        }
        return flightsRoutesResponseDTOList;
    }
}
