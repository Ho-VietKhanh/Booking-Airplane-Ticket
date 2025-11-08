package se196411.booking_ticket.service.admin;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.FlightsEntity;
import se196411.booking_ticket.repository.FlightsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightAdminServiceImpl implements FlightAdminService {
    private final FlightsRepository flightsRepository;

    public FlightAdminServiceImpl(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    @Override
    public List<FlightsEntity> findAll() {
        return flightsRepository.findAll();
    }

    @Override
    public FlightsEntity findById(String id) {
        return flightsRepository.findById(id).orElse(null);
    }

    @Override
    public FlightsEntity save(FlightsEntity flight) {
        return flightsRepository.save(flight);
    }

    @Override
    public void deleteById(String id) {
        flightsRepository.deleteById(id);
    }

    @Override
    public List<FlightsEntity> search(String q, LocalDateTime from, LocalDateTime to) {
        // If search query is provided, search by flight ID or airplane model
        if (q != null && !q.trim().isEmpty()) {
            return flightsRepository.findByFlightIdContainingIgnoreCaseOrAirplane_ModelContainingIgnoreCase(q.trim(), q.trim());
        }
        // If date range is provided, search by date range
        if (from != null && to != null) {
            return flightsRepository.findByStartedTimeBetween(from, to);
        }
        // If no filters, return all flights
        return flightsRepository.findAll();
    }
}
