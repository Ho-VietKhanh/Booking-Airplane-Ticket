package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.AirPlaneEntity;
import se196411.booking_ticket.model.entity.SeatEntity;

import java.util.List;

public interface AirplaneService {
    AirPlaneEntity createAirplane(AirPlaneEntity airplane);
    AirPlaneEntity findById(String id);
    void deleteById(String id);
    List<AirPlaneEntity> findAll();
    AirPlaneEntity updateById(String id, AirPlaneEntity airplane);
    List<SeatEntity> findSeatsByAirplaneId(String airplaneId);
}
