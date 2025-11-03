package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.AirPlaneEntity;
import se196411.booking_ticket.model.entity.FlightRoutesEntity;
import se196411.booking_ticket.model.entity.FlightsEntity;
import se196411.booking_ticket.model.dto.FlightsRequestDTO;
import se196411.booking_ticket.model.dto.FlightsResponseDTO;
import se196411.booking_ticket.repository.AirplaneRepository;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.repository.FlightsRoutesRepository;

import java.util.ArrayList;
import java.util.List;

import static se196411.booking_ticket.utils.RandomId.generateRandomId;

@Service
public class FlightsServiceImpl implements FlightsService {
    @Autowired
    private FlightsRepository flightsRepository;
    @Autowired
    private AirplaneRepository airplaneRepository;
    @Autowired
    private FlightsRoutesRepository flightsRoutesRepository;

    private int characterLength = 5;
    private int numberLength = 5;

    public String generateUniqueId() {
        String newId;
        do {
            newId = generateRandomId(characterLength, numberLength);
        } while (flightsRepository.existsById(newId));
        return newId;
    }

    @Override
    public boolean insertFlights(FlightsRequestDTO flightsRequestDTO) {
        if (flightsRequestDTO == null) {
            return false;
        }
        AirPlaneEntity airPlane = airplaneRepository.findById(flightsRequestDTO.getAirplaneId()).orElse(null);
        if(airPlane == null) {
            return false;
        }
        FlightRoutesEntity routesEntity = flightsRoutesRepository.findById(flightsRequestDTO.getFlightRouteId()).orElse(null);
        if(routesEntity == null) {
            return false;
        }
        FlightsEntity flightsEntity = new FlightsEntity();
        flightsEntity.setFlightId(generateUniqueId());
        flightsEntity.setAirplane(airPlane);
        flightsEntity.setFlightRoute(routesEntity);
        flightsEntity.setBasePrice(flightsRequestDTO.getBasePrice());
        flightsEntity.setStartedTime(flightsRequestDTO.getStartedTime());
        flightsEntity.setEndedTime(flightsRequestDTO.getEndedTime());
        flightsEntity.setStatus(flightsRequestDTO.getStatus());

        flightsRepository.save(flightsEntity);
        return true;
    }

    @Override
    public boolean updateFlightsByFlightsById(String flightsId, FlightsRequestDTO flightsRequestDTO) {
        FlightsEntity flightsEntity = flightsRepository.findById(flightsId).orElse(null);
        if(flightsEntity == null) {
            return false;
        }
        AirPlaneEntity airPlane = airplaneRepository.findById(flightsRequestDTO.getAirplaneId()).orElse(null);
        if(airPlane == null) {
            return false;
        }
        FlightRoutesEntity routesEntity = flightsRoutesRepository.findById(flightsRequestDTO.getFlightRouteId()).orElse(null);
        if(routesEntity == null) {
            return false;
        }

        flightsEntity.setAirplane(airPlane);
        flightsEntity.setFlightRoute(routesEntity);
        flightsEntity.setBasePrice(flightsRequestDTO.getBasePrice());
        flightsEntity.setStartedTime(flightsRequestDTO.getStartedTime());
        flightsEntity.setEndedTime(flightsRequestDTO.getEndedTime());
        flightsEntity.setStatus(flightsRequestDTO.getStatus());

        flightsRepository.save(flightsEntity);
        return true;
    }

    @Override
    public boolean deleteFlightByFlightsById(String flightsId) {
        FlightsEntity flightsEntity = flightsRepository.findById(flightsId).orElse(null);
        if(flightsEntity == null) {
            return false;
        }
        flightsRepository.delete(flightsEntity);
        return true;
    }

    @Override
    public FlightsResponseDTO getFlightsByFlightsId(String flightsId) {
        FlightsEntity flightsEntity = flightsRepository.findById(flightsId).orElse(null);
        if(flightsEntity == null) {
            throw new RuntimeException("Not found FlightsEntity");
        }
        FlightsResponseDTO flightsResponseDTO = new FlightsResponseDTO();
        flightsResponseDTO.setFlightId(flightsEntity.getFlightId());
        flightsResponseDTO.setFlightRouteId(flightsEntity.getFlightRoute().getFlightRoutesId());
        flightsResponseDTO.setAirplaneId(flightsEntity.getAirplane().getAirplaneId());
        flightsResponseDTO.setBasePrice(flightsEntity.getBasePrice());
        flightsResponseDTO.setStartedTime(flightsEntity.getStartedTime());
        flightsResponseDTO.setEndedTime(flightsEntity.getEndedTime());
        flightsResponseDTO.setStatus(flightsEntity.getStatus());
        return flightsResponseDTO;
    }

    @Override
    public List<FlightsResponseDTO> getAllFlights() {
        List<FlightsEntity> flightsEntityList = flightsRepository.findAll();
        List<FlightsResponseDTO> flightsResponseDTOList = new ArrayList<>();
        for(FlightsEntity flightsEntity : flightsEntityList) {
            FlightsResponseDTO flightsResponseDTO = new FlightsResponseDTO();
            flightsResponseDTO.setFlightId(flightsEntity.getFlightId());
            flightsResponseDTO.setFlightRouteId(flightsEntity.getFlightRoute().getFlightRoutesId());
            flightsResponseDTO.setAirplaneId(flightsEntity.getAirplane().getAirplaneId());
            flightsResponseDTO.setBasePrice(flightsEntity.getBasePrice());
            flightsResponseDTO.setStartedTime(flightsEntity.getStartedTime());
            flightsResponseDTO.setEndedTime(flightsEntity.getEndedTime());
            flightsResponseDTO.setStatus(flightsEntity.getStatus());
            flightsResponseDTOList.add(flightsResponseDTO);
        }
        return flightsResponseDTOList;
    }

    @Override
    public List<FlightsResponseDTO> getAllFlightsByFlightsRoutes(String flightRoutesId) {
        List<FlightsEntity> flightsEntityList = flightsRepository.findByFlightRoute_FlightRoutesId(flightRoutesId);
        List<FlightsResponseDTO> flightsResponseDTOList = new ArrayList<>();
        for(FlightsEntity flightsEntity : flightsEntityList) {
            FlightsResponseDTO flightsResponseDTO = new FlightsResponseDTO();
            flightsResponseDTO.setFlightId(flightsEntity.getFlightId());
            flightsResponseDTO.setFlightRouteId(flightsEntity.getFlightRoute().getFlightRoutesId());
            flightsResponseDTO.setAirplaneId(flightsEntity.getAirplane().getAirplaneId());
            flightsResponseDTO.setBasePrice(flightsEntity.getBasePrice());
            flightsResponseDTO.setStartedTime(flightsEntity.getStartedTime());
            flightsResponseDTO.setEndedTime(flightsEntity.getEndedTime());
            flightsResponseDTO.setStatus(flightsEntity.getStatus());
            flightsResponseDTOList.add(flightsResponseDTO);
        }
        return flightsResponseDTOList;
    }
}
