package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.AirPlaneEntity;
import se196411.booking_ticket.model.SeatEntity;
import se196411.booking_ticket.repository.AirplaneRepository;
import se196411.booking_ticket.repository.SeatRepository;
import se196411.booking_ticket.utils.RandomId;

import java.util.List;

@Service
public class AirplaneServiceImpl implements AirplaneService {

    @Autowired
    AirplaneRepository airplaneRepository;

    @Autowired
    SeatRepository seatRepository;

    @Override
    public AirPlaneEntity createAirplane(AirPlaneEntity airplane) {
        String id = RandomId.generateRandomId(3, 4);
        airplane.setAirplaneId(id);
        return this.airplaneRepository.save(airplane);
    }

    @Override
    public AirPlaneEntity findById(String id) {
        return this.airplaneRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(String id) {
        this.airplaneRepository.deleteById(id);
    }

    @Override
    public List<AirPlaneEntity> findAll() {
        return this.airplaneRepository.findAll();
    }

    @Override
    public AirPlaneEntity updateById(String id, AirPlaneEntity airplane) {
        AirPlaneEntity existAirplane = this.airplaneRepository.findById(id).orElse(null);
        if(existAirplane == null) {
            throw new IllegalArgumentException("Airplane not found: " + id);
        } else {
            existAirplane.setModel(airplane.getModel());
            existAirplane.setCapacity(airplane.getCapacity());
            existAirplane.setAirline(airplane.getAirline());
            existAirplane.setSeats(airplane.getSeats());
            existAirplane.setFlights(airplane.getFlights());
            return this.airplaneRepository.save(existAirplane);
        }
    }

    @Override
    public List<SeatEntity> findSeatsByAirplaneId(String airplaneId) {
        return this.seatRepository.findSeatsByAirplaneId(airplaneId);
    }
}
