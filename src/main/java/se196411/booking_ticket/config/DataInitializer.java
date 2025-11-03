package se196411.booking_ticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import se196411.booking_ticket.model.entity.*;
import se196411.booking_ticket.repository.AirplaneRepository;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.repository.SeatRepository;
import se196411.booking_ticket.repository.AirportsRepository;
import se196411.booking_ticket.repository.FlightsRoutesRepository;
import se196411.booking_ticket.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final AirplaneRepository airplaneRepository;
    private final FlightsRepository flightsRepository;
    private final SeatRepository seatRepository;
    private final FlightsRoutesRepository flightsRoutesRepository;
    private final AirportsRepository airportsRepository;
    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MealService mealService;

    @Autowired
    private LuggageService luggageService;

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



        RoleEntity roleEntity = this.roleService.findAllRoles().stream().findFirst().orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setRoleId("role-user");
            role.setRoleName("USER");
            role.setDescription("Default role for users");
            return this.roleService.createRole(role);
        });

        UserEntity user = this.userService.findAll().stream().findFirst().orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setUserId("user-test-1");
            newUser.setFullName("Test User");
            newUser.setEmail("user@gmail.com");
            newUser.setPassword("password");
            newUser.setPhone("0123456789");
            newUser.setRole(roleEntity);
            newUser.setCreateAt(LocalDateTime.now());
            return this.userService.createUser(newUser);
        });

        if (this.paymentMethodService.getAllPaymentMethods().isEmpty()) {
            PaymentMethodEntity momo = new PaymentMethodEntity();
            momo.setPaymentMethodId("pm-momo");
            momo.setPaymentMethodName("MOMO");
            momo.setDescription("Momo e-wallet");
            this.paymentMethodService.createPaymentMethod(momo);

            PaymentMethodEntity vnpay = new PaymentMethodEntity();
            vnpay.setPaymentMethodId("pm-vnpay");
            vnpay.setPaymentMethodName("VNPAY");
            vnpay.setDescription("VNPAY / QR");
            this.paymentMethodService.createPaymentMethod(vnpay);

            PaymentMethodEntity card = new PaymentMethodEntity();
            card.setPaymentMethodId("pm-card");
            card.setPaymentMethodName("Credit Card");
            card.setDescription("Visa / Mastercard");
            this.paymentMethodService.createPaymentMethod(card);
        }

        // Create a sample payment + booking if not present
        Optional<PaymentEntity> existing = this.paymentService.getPaymentById("pay-0001");
        if (existing.isEmpty()) {
            PaymentEntity p = new PaymentEntity();
            p.setPaymentId("pay-0001");
            p.setCreatedAt(LocalDateTime.now());
            p.setPaidAt(null);
            p.setStatus("PENDING");
            p.setAmount(new BigDecimal("8008000.00"));
            this.paymentMethodService.getPaymentMethodById("pm-momo").ifPresent(p::setPaymentMethod);
            this.paymentService.createPayment(p);
        }

        // Booking: ensure not duplicated. NOTE: BookingEntity has a non-null user reference in your model.
        // This loader will create a booking that references the payment id but will set user to null.
        // If your DB enforces user_id NOT NULL, change 'userId' below to an existing user id and set booking.setUser(...)
        Optional<BookingEntity> bOpt = this.bookingService.findById("bk-0001");
        if (bOpt.isEmpty()) {
            BookingEntity b = new BookingEntity();
            b.setBookingId("bk-0001");
            b.setBookingTime(LocalDateTime.now());
            b.setTotalAmount(new BigDecimal("8008000.00"));
            b.setStatus("PENDING_PAYMENT");
            // set payment relation
            PaymentEntity paymentLink = this.paymentService.getPaymentById("pay-0001").orElse(null);
            b.setPayment(paymentLink);
            b.setUser(user);
            this.bookingService.create(b);
        }

        // Initialize Meal data
        if (this.mealService.findAll().isEmpty()) {
            MealEntity meal1 = new MealEntity();
            meal1.setName("Xôi Thịt Kho Trứng");
            meal1.setPrice(new BigDecimal("80000"));
            meal1.setNote("Xôi thịt kho trứng truyền thống");
            this.mealService.create(meal1);

            MealEntity meal2 = new MealEntity();
            meal2.setName("Mỳ Ý Sốt Bò Bằm");
            meal2.setPrice(new BigDecimal("80000"));
            meal2.setNote("Mỳ Ý sốt bò bằm thơm ngon");
            this.mealService.create(meal2);

            MealEntity meal3 = new MealEntity();
            meal3.setName("Mì Ly Ăn Liền Và 1 Lon Nước Ngọt Có Ga (7 Up/Pepsi)");
            meal3.setPrice(new BigDecimal("55000"));
            meal3.setNote("Combo mì ly và nước ngọt");
            this.mealService.create(meal3);

            MealEntity meal4 = new MealEntity();
            meal4.setName("Bánh Mì Pate");
            meal4.setPrice(new BigDecimal("45000"));
            meal4.setNote("Bánh mì pate truyền thống");
            this.mealService.create(meal4);
        }

        // Initialize Luggage data
        if (this.luggageService.findAll().isEmpty()) {
            LuggageEntity luggage1 = new LuggageEntity();
            luggage1.setLuggageAllowance(15.0);
            luggage1.setPrice(new BigDecimal("200000"));
            luggage1.setNote("Hành lý ký gửi 15kg");
            this.luggageService.create(luggage1);

            LuggageEntity luggage2 = new LuggageEntity();
            luggage2.setLuggageAllowance(20.0);
            luggage2.setPrice(new BigDecimal("300000"));
            luggage2.setNote("Hành lý ký gửi 20kg");
            this.luggageService.create(luggage2);

            LuggageEntity luggage3 = new LuggageEntity();
            luggage3.setLuggageAllowance(25.0);
            luggage3.setPrice(new BigDecimal("400000"));
            luggage3.setNote("Hành lý ký gửi 25kg");
            this.luggageService.create(luggage3);

            LuggageEntity luggage4 = new LuggageEntity();
            luggage4.setLuggageAllowance(30.0);
            luggage4.setPrice(new BigDecimal("500000"));
            luggage4.setNote("Hành lý ký gửi 30kg");
            this.luggageService.create(luggage4);
        }

    }
}
