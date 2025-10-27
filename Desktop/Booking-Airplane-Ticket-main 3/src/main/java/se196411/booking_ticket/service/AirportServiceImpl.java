package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.AirportsEntity;
import se196411.booking_ticket.model.FlightRoutesEntity;
import se196411.booking_ticket.model.FlightsEntity;
import se196411.booking_ticket.model.dto.AirportRequestDTO;
import se196411.booking_ticket.model.dto.AirportResponseDTO;
import se196411.booking_ticket.repository.AirportsRepository;

import java.util.ArrayList;
import java.util.List;

import static se196411.booking_ticket.utils.RandomId.generateRandomId;

@Service
public class AirportServiceImpl implements AirportService {
    @Autowired
    private AirportsRepository airportsRepository;

    private int characterLength = 5;
    private int numberLength = 5;

    public String generateUniqueId() {
        String newId;
        do {
            newId = generateRandomId(characterLength, numberLength);
        } while (airportsRepository.existsById(newId));
        return newId;
    }

    @Override
    public boolean insertAirport(AirportRequestDTO airportRequestDTO) {
        if (airportRequestDTO == null) {
            return false;
        }
        AirportsEntity airportsEntity = new AirportsEntity();
        airportsEntity.setAirportId(generateUniqueId());
        airportsEntity.setCode(airportRequestDTO.getCode());
        airportsEntity.setName(airportRequestDTO.getName());
        airportsEntity.setPlace(airportRequestDTO.getPlace());

        airportsRepository.save(airportsEntity);
        return true;
    }

    @Override
    public boolean updateAirportByAirportId(String airportId, AirportRequestDTO airportRequestDTO) {
        AirportsEntity airportsEntity = airportsRepository.findById(airportId).orElse(null);
        if (airportsEntity == null) {
            return false;
        }
        airportsEntity.setCode(airportRequestDTO.getCode());
        airportsEntity.setName(airportRequestDTO.getName());
        airportsEntity.setPlace(airportRequestDTO.getPlace());
        airportsRepository.save(airportsEntity);
        return true;
    }

    @Override
    public boolean deleteAirportByAirportId(String airportId) {
        AirportsEntity airportsEntity = airportsRepository.findById(airportId).orElse(null);
        if (airportsEntity == null) {
            return false;
        }
        airportsRepository.delete(airportsEntity);
        return true;
    }

    @Override
    public AirportResponseDTO getAirportByAirportId(String airportId) {
        AirportsEntity airportsEntity = airportsRepository.findById(airportId).orElse(null);
        if (airportsEntity == null) {
            throw new RuntimeException("Airport not found");
        }
        AirportResponseDTO airportResponseDTO = new AirportResponseDTO();
        airportResponseDTO.setAirportId(airportsEntity.getAirportId());
        airportResponseDTO.setCode(airportsEntity.getCode());
        airportResponseDTO.setName(airportsEntity.getName());
        airportResponseDTO.setPlace(airportsEntity.getPlace());
        return airportResponseDTO;
    }

    @Override
    public AirportResponseDTO getAirportByAirportName(String airportName) {
        AirportsEntity airportsEntity = airportsRepository.findByNameContainingIgnoreCase(airportName);
        if (airportsEntity == null) {
            throw new RuntimeException("Airport not found");
        }
        AirportResponseDTO airportResponseDTO = new AirportResponseDTO();
        airportResponseDTO.setAirportId(airportsEntity.getAirportId());
        airportResponseDTO.setCode(airportsEntity.getCode());
        airportResponseDTO.setName(airportsEntity.getName());
        airportResponseDTO.setPlace(airportsEntity.getPlace());
        return airportResponseDTO;
    }

    @Override
    public AirportResponseDTO getAirportByAirportPlace(String airportPlace) {
        AirportsEntity airportsEntity = airportsRepository.findByPlaceContainingIgnoreCase(airportPlace);
        if (airportsEntity == null) {
            throw new RuntimeException("Airport not found");
        }
        AirportResponseDTO airportResponseDTO = new AirportResponseDTO();
        airportResponseDTO.setAirportId(airportsEntity.getAirportId());
        airportResponseDTO.setCode(airportsEntity.getCode());
        airportResponseDTO.setName(airportsEntity.getName());
        airportResponseDTO.setPlace(airportsEntity.getPlace());
        return airportResponseDTO;
    }

    @Override
    public List<AirportResponseDTO> getAllAirports() {
        List<AirportsEntity> airportsEntities = airportsRepository.findAll();
        List<AirportResponseDTO> airportResponseDTOList = new ArrayList<>();
        for (AirportsEntity airportsEntity : airportsEntities) {
            AirportResponseDTO airportResponseDTO = new AirportResponseDTO();
            airportResponseDTO.setAirportId(airportsEntity.getAirportId());
            airportResponseDTO.setCode(airportsEntity.getCode());
            airportResponseDTO.setName(airportsEntity.getName());
            airportResponseDTO.setPlace(airportsEntity.getPlace());
            airportResponseDTOList.add(airportResponseDTO);
        }
        return airportResponseDTOList;
    }
}
