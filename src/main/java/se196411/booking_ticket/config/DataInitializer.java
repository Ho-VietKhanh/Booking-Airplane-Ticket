package se196411.booking_ticket.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import se196411.booking_ticket.model.entity.*;
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
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AirplaneRepository airplaneRepository,
                           FlightsRepository flightsRepository,
                           SeatRepository seatRepository,
                           FlightsRoutesRepository flightsRoutesRepository,
                           AirportsRepository airportsRepository,
                           RoleRepository roleRepository,
                           UserRepository userRepository,
                           PaymentRepository paymentRepository,
                           PaymentMethodRepository paymentMethodRepository,
                           BookingRepository bookingRepository,
                           TicketRepository ticketRepository,
                           PasswordEncoder passwordEncoder) {
        this.airplaneRepository = airplaneRepository;
        this.flightsRepository = flightsRepository;
        this.seatRepository = seatRepository;
        this.flightsRoutesRepository = flightsRoutesRepository;
        this.airportsRepository = airportsRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // If data already exists, skip initialization
        if (!seatRepository.findAll().isEmpty()) {
            return;
        }

        // Create roles
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = roleRepository.save(new RoleEntity("ROLE_USER"));
        }

        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = roleRepository.save(new RoleEntity("ROLE_ADMIN"));
        }

        // Create users
        UserEntity user1 = new UserEntity();
        user1.setUserId("USER-001");
        user1.setFullName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setPhone("0123456789");
        user1.setRole(userRole);
        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setUserId("USER-002");
        user2.setFullName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setPhone("0987654321");
        user2.setRole(adminRole);
        userRepository.save(user2);

        // Create airplane
        String airplaneId = "AP-001";
        AirPlaneEntity airplane = new AirPlaneEntity();
        airplane.setAirplaneId(airplaneId);
        airplane.setModel("Boeing 737");
        airplane.setCapacity(30);
        airplane.setAirline("Example Air");
        airplaneRepository.save(airplane);

        // Create airports
        AirportsEntity airport1 = new AirportsEntity();
        airport1.setAirportId("APORT-001");
        airport1.setCode("HNO");
        airport1.setName("Noi Bai International");
        airport1.setPlace("Hanoi");
        airportsRepository.save(airport1);

        AirportsEntity airport2 = new AirportsEntity();
        airport2.setAirportId("APORT-002");
        airport2.setCode("SGN");
        airport2.setName("Tan Son Nhat");
        airport2.setPlace("Ho Chi Minh City");
        airportsRepository.save(airport2);

        // Create flight route
        FlightRoutesEntity route = new FlightRoutesEntity();
        route.setFlightRoutesId("FR-001");
        route.setStartedAirport(airport1);
        route.setEndedAirport(airport2);
        flightsRoutesRepository.save(route);

        // Create flight
        FlightsEntity flight = new FlightsEntity();
        flight.setFlightId("FL-001");
        flight.setAirplane(airplane);
        flight.setFlightRoute(route);
        flight.setBasePrice(new BigDecimal("100"));
        flight.setStartedTime(LocalDateTime.now().plusDays(1));
        flight.setEndedTime(LocalDateTime.now().plusDays(1).plusHours(2));
        flight.setStatus("SCHEDULED");
        flightsRepository.save(flight);

        // Create seats
        List<SeatEntity> seats = new ArrayList<>();
        String[] columns = {"A", "B", "C", "D", "E", "F"};
        for (int row = 1; row <= 5; row++) {
            for (String col : columns) {
                SeatEntity seat = new SeatEntity();
                seat.setSeatId(UUID.randomUUID().toString());
                seat.setSeatNumber(row + col);
                seat.setSeatClass(row == 1 ? "Business" : "Economy");
                seat.setAvailable(true);
                seat.setAirplane(airplane);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);

        // Create payment methods first
        PaymentMethodEntity creditCardMethod = new PaymentMethodEntity();
        creditCardMethod.setPaymentMethodId("PM-001");
        creditCardMethod.setPaymentMethodName("Credit Card");
        creditCardMethod.setDescription("Payment via credit card");
        paymentMethodRepository.save(creditCardMethod);

        PaymentMethodEntity paypalMethod = new PaymentMethodEntity();
        paypalMethod.setPaymentMethodId("PM-002");
        paypalMethod.setPaymentMethodName("PayPal");
        paypalMethod.setDescription("Payment via PayPal");
        paymentMethodRepository.save(paypalMethod);

        // Create payments
        PaymentEntity payment1 = new PaymentEntity();
        payment1.setPaymentId("PAY-001");
        payment1.setPaymentMethod(creditCardMethod);
        payment1.setAmount(new BigDecimal("250.00"));
        payment1.setStatus("COMPLETED");
        payment1.setCreatedAt(LocalDateTime.now().minusDays(1));
        payment1.setPaidAt(LocalDateTime.now().minusDays(1).plusMinutes(5));
        paymentRepository.save(payment1);

        PaymentEntity payment2 = new PaymentEntity();
        payment2.setPaymentId("PAY-002");
        payment2.setPaymentMethod(paypalMethod);
        payment2.setAmount(new BigDecimal("150.00"));
        payment2.setStatus("COMPLETED");
        payment2.setCreatedAt(LocalDateTime.now().minusHours(5));
        payment2.setPaidAt(LocalDateTime.now().minusHours(5).plusMinutes(2));
        paymentRepository.save(payment2);

        // Create bookings
        BookingEntity booking1 = new BookingEntity();
        booking1.setBookingId("BK-001");
        booking1.setBookingTime(LocalDateTime.now().minusDays(1));
        booking1.setTotalAmount(new BigDecimal("250.00"));
        booking1.setStatus("CONFIRMED");
        booking1.setPayment(payment1);
        booking1.setUser(user1);
        bookingRepository.save(booking1);

        BookingEntity booking2 = new BookingEntity();
        booking2.setBookingId("BK-002");
        booking2.setBookingTime(LocalDateTime.now().minusHours(5));
        booking2.setTotalAmount(new BigDecimal("150.00"));
        booking2.setStatus("PENDING");
        booking2.setPayment(payment2);
        booking2.setUser(user1);
        bookingRepository.save(booking2);

        // Create tickets
        SeatEntity seat1 = seats.get(0);
        SeatEntity seat2 = seats.get(1);

        TicketEntity ticket1 = new TicketEntity();
        ticket1.setTicketId("TK-001");
        ticket1.setPrice(new BigDecimal("125.00"));
        ticket1.setStatus("BOOKED");
        ticket1.setFlight(flight);
        ticket1.setBooking(booking1);
        ticket1.setSeat(seat1);
        ticketRepository.save(ticket1);

        TicketEntity ticket2 = new TicketEntity();
        ticket2.setTicketId("TK-002");
        ticket2.setPrice(new BigDecimal("125.00"));
        ticket2.setStatus("BOOKED");
        ticket2.setFlight(flight);
        ticket2.setBooking(booking1);
        ticket2.setSeat(seat2);
        ticketRepository.save(ticket2);

        TicketEntity ticket3 = new TicketEntity();
        ticket3.setTicketId("TK-003");
        ticket3.setPrice(new BigDecimal("150.00"));
        ticket3.setStatus("PENDING");
        ticket3.setFlight(flight);
        ticket3.setBooking(booking2);
        ticket3.setSeat(seats.get(6));
        ticketRepository.save(ticket3);

        // Update seat availability
        seat1.setAvailable(false);
        seat2.setAvailable(false);
        seats.get(6).setAvailable(false);
        seatRepository.saveAll(List.of(seat1, seat2, seats.get(6)));
    }
}