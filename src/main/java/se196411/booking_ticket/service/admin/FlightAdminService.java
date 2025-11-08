package se196411.booking_ticket.service.admin;

import se196411.booking_ticket.model.entity.FlightsEntity;

import java.time.LocalDateTime;
import java.util.List;


public interface FlightAdminService {
    List<FlightsEntity> findAll();
    FlightsEntity findById(String id);
    FlightsEntity save(FlightsEntity flight);
    void deleteById(String id);

    // Search flights by query string and/or date range
    List<FlightsEntity> search(String q, LocalDateTime from, LocalDateTime to);
}