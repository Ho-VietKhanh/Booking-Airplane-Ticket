package se196411.booking_ticket.service;

import se196411.booking_ticket.model.AirplaneEntity;
import se196411.booking_ticket.model.SeatEntity;

import java.util.List;

public interface AirplaneService {
    AirplaneEntity createAirplane(AirplaneEntity airplane);
    AirplaneEntity findById(String id);
    void deleteById(String id);
    List<AirplaneEntity> findAll();
    AirplaneEntity updateById(String id, AirplaneEntity airplane);
    List<SeatEntity> findSeatsByAirplaneId(String airplaneId);
}
