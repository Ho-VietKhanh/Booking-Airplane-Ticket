package se196411.booking_ticket.service;

import se196411.booking_ticket.model.SeatEntity;

import java.util.List;

public interface SeatService {
    SeatEntity create(SeatEntity seat);
    SeatEntity findById(String id);
    SeatEntity updateById(String id, SeatEntity seat);
    void deleteById(String id);
    List<SeatEntity> findAll();
    List<SeatEntity> findAvailableSeatsByAirplaneId(String airplaneId, String status);
}
