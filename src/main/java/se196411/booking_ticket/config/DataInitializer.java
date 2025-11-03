package se196411.booking_ticket.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import se196411.booking_ticket.model.entity.AirPlaneEntity;
import se196411.booking_ticket.model.entity.FlightRoutesEntity;
import se196411.booking_ticket.model.entity.FlightsEntity;
import se196411.booking_ticket.model.entity.AirportsEntity;
import se196411.booking_ticket.model.entity.SeatEntity;
import se196411.booking_ticket.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final AirplaneRepository airplaneRepository;
    private final FlightsRepository flightsRepository;
    private final SeatRepository seatRepository;
    private final FlightsRoutesRepository flightsRoutesRepository;
    private final AirportsRepository airportsRepository;

    public DataInitializer(AirplaneRepository airplaneRepository,
                           FlightsRepository flightsRepository,
                           SeatRepository seatRepository,
                           FlightsRoutesRepository flightsRoutesRepository,
                           AirportsRepository airportsRepository) {
        this.airplaneRepository = airplaneRepository;
        this.flightsRepository = flightsRepository;
        this.seatRepository = seatRepository;
        this.flightsRoutesRepository = flightsRoutesRepository;
        this.airportsRepository = airportsRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // If seats already exist, skip initialization
        if (!seatRepository.findAll().isEmpty()) {
            return;
        }

        // Create a sample airplane
        String airplaneId = "AP-001";
        if (!airplaneRepository.existsByAirplaneId(airplaneId)) {
            AirPlaneEntity airplane = new AirPlaneEntity();
            airplane.setAirplaneId(airplaneId);
            airplane.setModel("Boeing 737");
            airplane.setCapacity(30);
            airplane.setAirline("Example Air");
            airplaneRepository.save(airplane);
        }

        // Create sample airports and flight route so flight.route is not null
        String startAirportId = "APORT-001";
        String endAirportId = "APORT-002";
        if (airportsRepository.findById(startAirportId).isEmpty()) {
            AirportsEntity a1 = new AirportsEntity();
            a1.setAirportId(startAirportId);
            a1.setCode("HNO");
            a1.setName("Noi Bai International");
            a1.setPlace("Hanoi");
            airportsRepository.save(a1);
        }
        if (airportsRepository.findById(endAirportId).isEmpty()) {
            AirportsEntity a2 = new AirportsEntity();
            a2.setAirportId(endAirportId);
            a2.setCode("SGN");
            a2.setName("Tan Son Nhat");
            a2.setPlace("Ho Chi Minh City");
            airportsRepository.save(a2);
        }

        String flightRouteId = "FR-001";
        if (flightsRoutesRepository.findById(flightRouteId).isEmpty()) {
            AirportsEntity started = airportsRepository.findById(startAirportId).orElseThrow();
            AirportsEntity ended = airportsRepository.findById(endAirportId).orElseThrow();
            FlightRoutesEntity route = new FlightRoutesEntity();
            route.setFlightRoutesId(flightRouteId);
            route.setStartedAirport(started);
            route.setEndedAirport(ended);
            flightsRoutesRepository.save(route);
        }

        // Create a sample flight using the airplane
        String flightId = "FL-001";
        if (flightsRepository.findById(flightId).isEmpty()) {
            AirPlaneEntity airplane = airplaneRepository.findById(airplaneId).orElseThrow();
            FlightsEntity flight = new FlightsEntity();
            flight.setFlightId(flightId);
            flight.setAirplane(airplane);
            flight.setFlightRoute(flightsRoutesRepository.findById("FR-001").orElseThrow());
            flight.setBasePrice(new BigDecimal("100"));
            flight.setStartedTime(LocalDateTime.now().plusDays(1));
            flight.setEndedTime(LocalDateTime.now().plusDays(1).plusHours(2));
            flight.setStatus("SCHEDULED");
            flightsRepository.save(flight);
        }

        // Create seats for the airplane (e.g., 5 rows x 6 columns = 30 seats)
        AirPlaneEntity airplane = airplaneRepository.findById(airplaneId).orElseThrow();
        List<SeatEntity> seats = new ArrayList<>();
        String[] columns = {"A", "B", "C", "D", "E", "F"};
        for (int row = 1; row <= 5; row++) {
            for (String col : columns) {
                SeatEntity seat = new SeatEntity();
                seat.setSeatId(UUID.randomUUID().toString());
                String seatNumber = row + col;
                seat.setSeatNumber(seatNumber);
                // Simple rule: row 1 is Business, others Economy
                seat.setSeatClass(row == 1 ? "Business" : "Economy");
                seat.setAvailable(true);
                seat.setAirplane(airplane);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }
}
