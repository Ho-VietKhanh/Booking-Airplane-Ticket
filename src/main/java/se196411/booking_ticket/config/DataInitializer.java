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
import org.springframework.security.crypto.password.PasswordEncoder;
import se196411.booking_ticket.model.enums.Role;

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
    private MealService mealService;

    @Autowired
    private LuggageService luggageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        createAirports();
        createAirplanes();
        createFlightRoutes();
        createFlights();
        createSeats();
        createUsers();
        createPaymentMethods();
        createSamplePaymentAndBooking();
        createMeals();
        createLuggage();
    }

    // ============== USERS WITH ENUM ROLE ==============
    private void createUsers() {
        // Create normal user if missing
        boolean hasUser = userService.findAll().stream().anyMatch(u -> "user@gmail.com".equals(u.getEmail()));
        if (!hasUser) {
            UserEntity newUser = new UserEntity();
            newUser.setUserId("user-test-1");
            newUser.setFullName("Test User");
            newUser.setEmail("user@gmail.com");
            newUser.setPassword(passwordEncoder.encode("password"));
            newUser.setPhone("0123456789");
            newUser.setRole(Role.USER);
            newUser.setCreateAt(LocalDateTime.now());
            userService.createUser(newUser);
        }
        // Create admin user if missing
        boolean hasAdmin = userService.findAll().stream().anyMatch(u -> "admin@gmail.com".equals(u.getEmail()));
        if (!hasAdmin) {
            UserEntity admin = new UserEntity();
            admin.setUserId("admin-1");
            admin.setFullName("System Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("0999999999");
            admin.setRole(Role.ADMIN);
            admin.setCreateAt(LocalDateTime.now());
            userService.createUser(admin);
        }
    }

    // ============== PAYMENT METHODS ==============
    private void createPaymentMethods() {
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
    }

    // ============== SAMPLE PAYMENT & BOOKING ==============
    private void createSamplePaymentAndBooking() {
        if (this.paymentService.getPaymentById("pay-0001").isEmpty()) {
            PaymentEntity p = new PaymentEntity();
            p.setPaymentId("pay-0001");
            p.setCreatedAt(LocalDateTime.now());
            p.setPaidAt(null);
            p.setStatus("PENDING_PAYMENT");
            p.setAmount(new BigDecimal("1500000")); // ví dụ số tiền chờ thanh toán
            this.paymentMethodService.getPaymentMethodById("pm-momo").ifPresent(p::setPaymentMethod);
            this.paymentService.createPayment(p);
        }

        if (this.bookingService.findById("bk-0001").isEmpty()) {
            BookingEntity b = new BookingEntity();
            b.setBookingId("bk-0001");
            b.setBookingTime(LocalDateTime.now());
            b.setTotalAmount(new BigDecimal("1500000"));
            b.setStatus("PENDING_PAYMENT");
            PaymentEntity paymentLink = this.paymentService.getPaymentById("pay-0001").orElse(null);
            b.setPayment(paymentLink);
            // Gán user test nếu có
            UserEntity user = this.userService.findAll().stream()
                    .filter(u -> "user@gmail.com".equals(u.getEmail()))
                    .findFirst().orElse(null);
            b.setUser(user);
            this.bookingService.create(b);
        }
    }

    // ============== MEALS ==============
    private void createMeals() {
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
    }

    // ============== LUGGAGE ==============
    private void createLuggage() {
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

    private void createAirports() {
        // Hanoi
        if (airportsRepository.findById("APORT-001").isEmpty()) {
            AirportsEntity a = new AirportsEntity();
            a.setAirportId("APORT-001");
            a.setCode("HAN");
            a.setName("Sân bay Quốc tế Nội Bài");
            a.setPlace("Hà Nội");
            airportsRepository.save(a);
        }

        // Ho Chi Minh City
        if (airportsRepository.findById("APORT-002").isEmpty()) {
            AirportsEntity a = new AirportsEntity();
            a.setAirportId("APORT-002");
            a.setCode("SGN");
            a.setName("Sân bay Quốc tế Tân Sơn Nhất");
            a.setPlace("TP. Hồ Chí Minh");
            airportsRepository.save(a);
        }

        // Da Nang
        if (airportsRepository.findById("APORT-003").isEmpty()) {
            AirportsEntity a = new AirportsEntity();
            a.setAirportId("APORT-003");
            a.setCode("DAD");
            a.setName("Sân bay Quốc tế Đà Nẵng");
            a.setPlace("Đà Nẵng");
            airportsRepository.save(a);
        }

        // Nha Trang
        if (airportsRepository.findById("APORT-004").isEmpty()) {
            AirportsEntity a = new AirportsEntity();
            a.setAirportId("APORT-004");
            a.setCode("CXR");
            a.setName("Sân bay Quốc tế Cam Ranh");
            a.setPlace("Khánh Hòa");
            airportsRepository.save(a);
        }

        // Phu Quoc
        if (airportsRepository.findById("APORT-005").isEmpty()) {
            AirportsEntity a = new AirportsEntity();
            a.setAirportId("APORT-005");
            a.setCode("PQC");
            a.setName("Sân bay Quốc tế Phú Quốc");
            a.setPlace("Phú Quốc");
            airportsRepository.save(a);
        }

        // Da Lat
        if (airportsRepository.findById("APORT-006").isEmpty()) {
            AirportsEntity a = new AirportsEntity();
            a.setAirportId("APORT-006");
            a.setCode("DLI");
            a.setName("Sân bay Liên Khương");
            a.setPlace("Đà Lạt");
            airportsRepository.save(a);
        }
    }

    private void createAirplanes() {
        if (!airplaneRepository.existsByAirplaneId("AP-001")) {
            AirPlaneEntity airplane = new AirPlaneEntity();
            airplane.setAirplaneId("AP-001");
            airplane.setModel("Boeing 787-9");
            airplane.setCapacity(180);
            airplane.setAirline("Bamboo Airways");
            airplaneRepository.save(airplane);
        }

        if (!airplaneRepository.existsByAirplaneId("AP-002")) {
            AirPlaneEntity airplane = new AirPlaneEntity();
            airplane.setAirplaneId("AP-002");
            airplane.setModel("Airbus A321neo");
            airplane.setCapacity(220);
            airplane.setAirline("Bamboo Airways");
            airplaneRepository.save(airplane);
        }

        if (!airplaneRepository.existsByAirplaneId("AP-003")) {
            AirPlaneEntity airplane = new AirPlaneEntity();
            airplane.setAirplaneId("AP-003");
            airplane.setModel("Airbus A320");
            airplane.setCapacity(180);
            airplane.setAirline("Bamboo Airways");
            airplaneRepository.save(airplane);
        }

        if (!airplaneRepository.existsByAirplaneId("AP-004")) {
            AirPlaneEntity airplane = new AirPlaneEntity();
            airplane.setAirplaneId("AP-004");
            airplane.setModel("Boeing 737-800");
            airplane.setCapacity(189);
            airplane.setAirline("Bamboo Airways");
            airplaneRepository.save(airplane);
        }
    }

    private void createFlightRoutes() {
        // HAN -> SGN
        createRoute("FR-001", "APORT-001", "APORT-002");
        // SGN -> HAN
        createRoute("FR-002", "APORT-002", "APORT-001");
        // HAN -> DAD
        createRoute("FR-003", "APORT-001", "APORT-003");
        // DAD -> HAN
        createRoute("FR-004", "APORT-003", "APORT-001");
        // SGN -> DAD
        createRoute("FR-005", "APORT-002", "APORT-003");
        // DAD -> SGN
        createRoute("FR-006", "APORT-003", "APORT-002");
        // HAN -> PQC
        createRoute("FR-007", "APORT-001", "APORT-005");
        // PQC -> HAN
        createRoute("FR-008", "APORT-005", "APORT-001");
        // SGN -> PQC
        createRoute("FR-009", "APORT-002", "APORT-005");
        // PQC -> SGN
        createRoute("FR-010", "APORT-005", "APORT-002");
        // HAN -> CXR
        createRoute("FR-011", "APORT-001", "APORT-004");
        // CXR -> HAN
        createRoute("FR-012", "APORT-004", "APORT-001");
        // SGN -> DLI
        createRoute("FR-013", "APORT-002", "APORT-006");
        // DLI -> SGN
        createRoute("FR-014", "APORT-006", "APORT-002");
    }

    private void createRoute(String routeId, String startAirportId, String endAirportId) {
        if (flightsRoutesRepository.findById(routeId).isEmpty()) {
            AirportsEntity started = airportsRepository.findById(startAirportId).orElseThrow();
            AirportsEntity ended = airportsRepository.findById(endAirportId).orElseThrow();
            FlightRoutesEntity route = new FlightRoutesEntity();
            route.setFlightRoutesId(routeId);
            route.setStartedAirport(started);
            route.setEndedAirport(ended);
            flightsRoutesRepository.save(route);
        }
    }

    private void createFlights() {
        // Giảm bớt số lượng chuyến bay seed cho hợp lý.
        if (!flightsRepository.findAll().isEmpty()) {
            return; // đã có dữ liệu flight -> không seed thêm
        }
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime tomorrow = today.plusDays(1);

        // Một vài route chính: FR-001 (HAN->SGN), FR-002 (SGN->HAN), FR-003 (HAN->DAD)
        createFlight("FL-001", "AP-001", "FR-001", 1500000, today.withHour(6).withMinute(0), today.withHour(8).withMinute(15), "OPEN");
        createFlight("FL-002", "AP-002", "FR-001", 1400000, today.withHour(14).withMinute(0), today.withHour(16).withMinute(15), "OPEN");
        createFlight("FL-003", "AP-001", "FR-002", 1500000, today.withHour(9).withMinute(0), today.withHour(11).withMinute(15), "OPEN");
        createFlight("FL-004", "AP-003", "FR-003", 900000, today.withHour(7).withMinute(30), today.withHour(8).withMinute(50), "OPEN");
        // Flights for tomorrow
        createFlight("FL-005", "AP-001", "FR-001", 1550000, tomorrow.withHour(6).withMinute(0), tomorrow.withHour(8).withMinute(15), "OPEN");
        createFlight("FL-006", "AP-002", "FR-002", 1550000, tomorrow.withHour(9).withMinute(0), tomorrow.withHour(11).withMinute(15), "OPEN");
    }

    private void createFlight(String flightId, String airplaneId, String routeId,
                              int basePrice, LocalDateTime startTime, LocalDateTime endTime, String status) {
        if (flightsRepository.findById(flightId).isEmpty()) {
            AirPlaneEntity airplane = airplaneRepository.findById(airplaneId).orElse(null);
            FlightRoutesEntity route = flightsRoutesRepository.findById(routeId).orElse(null);
            if (airplane == null || route == null) return; // nếu thiếu dữ liệu nền thì bỏ qua
            FlightsEntity flight = new FlightsEntity();
            flight.setFlightId(flightId);
            flight.setAirplane(airplane);
            flight.setFlightRoute(route);
            flight.setBasePrice(new BigDecimal(basePrice));
            flight.setStartedTime(startTime);
            flight.setEndedTime(endTime);
            flight.setStatus(status); // đồng bộ với UI (OPEN, FLYING, DELAY, CANCELED)
            flightsRepository.save(flight);
        }
    }

    private void createSeats() {
        // Tạo ghế cho mỗi máy bay nếu chưa có
        List<AirPlaneEntity> airplanes = airplaneRepository.findAll();
        String[] columns = {"A", "B", "C", "D", "E", "F"};
        for (AirPlaneEntity airplane : airplanes) {
            if (!seatRepository.findSeatsByAirplaneId(airplane.getAirplaneId()).isEmpty()) {
                continue; // đã có ghế cho máy bay này
            }
            int capacity = airplane.getCapacity();
            int columnsCount = columns.length;
            int rows = (capacity + columnsCount - 1) / columnsCount; // làm tròn lên
            List<SeatEntity> seats = new ArrayList<>();
            int created = 0;
            for (int row = 1; row <= rows && created < capacity; row++) {
                for (int c = 0; c < columnsCount && created < capacity; c++) {
                    SeatEntity seat = new SeatEntity();
                    seat.setSeatId(UUID.randomUUID().toString());
                    seat.setSeatNumber(row + columns[c]);
                    seat.setSeatClass(row <= 3 ? "Business" : "Economy");
                    seat.setAvailable(true);
                    seat.setAirplane(airplane);
                    seats.add(seat);
                    created++;
                }
            }
            seatRepository.saveAll(seats);
        }
    }
}
