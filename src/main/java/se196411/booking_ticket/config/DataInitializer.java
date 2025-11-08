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
        LocalDateTime day3 = today.plusDays(2);
        LocalDateTime day4 = today.plusDays(3);
        LocalDateTime day5 = today.plusDays(4);
        LocalDateTime day6 = today.plusDays(5);

        // ========== HAN -> SGN (Today) ==========
        createFlight("FL-001", "AP-001", "FR-001", 1500000, today.withHour(6).withMinute(0), today.withHour(8).withMinute(15), "AVAILABLE");
        createFlight("FL-002", "AP-002", "FR-001", 1200000, today.withHour(10).withMinute(30), today.withHour(12).withMinute(45), "AVAILABLE");
        createFlight("FL-003", "AP-003", "FR-001", 1100000, today.withHour(14).withMinute(0), today.withHour(16).withMinute(15), "AVAILABLE");
        createFlight("FL-004", "AP-001", "FR-001", 1600000, today.withHour(18).withMinute(30), today.withHour(20).withMinute(45), "AVAILABLE");

        // ========== SGN -> HAN (Today) ==========
        createFlight("FL-005", "AP-001", "FR-002", 1500000, today.withHour(7).withMinute(0), today.withHour(9).withMinute(15), "AVAILABLE");
        createFlight("FL-006", "AP-002", "FR-002", 1250000, today.withHour(11).withMinute(30), today.withHour(13).withMinute(45), "AVAILABLE");
        createFlight("FL-007", "AP-003", "FR-002", 1150000, today.withHour(15).withMinute(0), today.withHour(17).withMinute(15), "AVAILABLE");
        createFlight("FL-008", "AP-001", "FR-002", 1650000, today.withHour(19).withMinute(30), today.withHour(21).withMinute(45), "AVAILABLE");

        // ========== HAN -> DAD (Today) ==========
        createFlight("FL-009", "AP-003", "FR-003", 900000, today.withHour(8).withMinute(0), today.withHour(9).withMinute(20), "AVAILABLE");
        createFlight("FL-010", "AP-002", "FR-003", 950000, today.withHour(13).withMinute(0), today.withHour(14).withMinute(20), "AVAILABLE");
        createFlight("FL-011", "AP-004", "FR-003", 920000, today.withHour(17).withMinute(30), today.withHour(18).withMinute(50), "AVAILABLE");

        // ========== DAD -> HAN (Today) ==========
        createFlight("FL-012", "AP-003", "FR-004", 900000, today.withHour(10).withMinute(0), today.withHour(11).withMinute(20), "AVAILABLE");
        createFlight("FL-013", "AP-002", "FR-004", 950000, today.withHour(15).withMinute(0), today.withHour(16).withMinute(20), "AVAILABLE");

        // ========== SGN -> DAD (Today) ==========
        createFlight("FL-014", "AP-003", "FR-005", 800000, today.withHour(9).withMinute(0), today.withHour(10).withMinute(30), "AVAILABLE");
        createFlight("FL-015", "AP-004", "FR-005", 850000, today.withHour(14).withMinute(30), today.withHour(16).withMinute(0), "AVAILABLE");

        // ========== DAD -> SGN (Today) ==========
        createFlight("FL-016", "AP-003", "FR-006", 800000, today.withHour(11).withMinute(0), today.withHour(12).withMinute(30), "AVAILABLE");
        createFlight("FL-017", "AP-004", "FR-006", 850000, today.withHour(17).withMinute(0), today.withHour(18).withMinute(30), "AVAILABLE");

        // ========== HAN -> PQC (Today) ==========
        createFlight("FL-018", "AP-001", "FR-007", 1800000, today.withHour(9).withMinute(0), today.withHour(11).withMinute(30), "AVAILABLE");
        createFlight("FL-019", "AP-002", "FR-007", 1700000, today.withHour(15).withMinute(0), today.withHour(17).withMinute(30), "AVAILABLE");

        // ========== PQC -> HAN (Today) ==========
        createFlight("FL-020", "AP-001", "FR-008", 1800000, today.withHour(12).withMinute(0), today.withHour(14).withMinute(30), "AVAILABLE");
        createFlight("FL-021", "AP-002", "FR-008", 1700000, today.withHour(18).withMinute(0), today.withHour(20).withMinute(30), "AVAILABLE");

        // ========== SGN -> PQC (Today) ==========
        createFlight("FL-022", "AP-003", "FR-009", 1000000, today.withHour(8).withMinute(30), today.withHour(9).withMinute(30), "AVAILABLE");
        createFlight("FL-023", "AP-004", "FR-009", 1050000, today.withHour(13).withMinute(30), today.withHour(14).withMinute(30), "AVAILABLE");
        createFlight("FL-024", "AP-003", "FR-009", 1000000, today.withHour(17).withMinute(0), today.withHour(18).withMinute(0), "AVAILABLE");

        // ========== PQC -> SGN (Today) ==========
        createFlight("FL-025", "AP-003", "FR-010", 1000000, today.withHour(10).withMinute(0), today.withHour(11).withMinute(0), "AVAILABLE");
        createFlight("FL-026", "AP-004", "FR-010", 1050000, today.withHour(15).withMinute(0), today.withHour(16).withMinute(0), "AVAILABLE");

        // ========== HAN -> CXR (Today) ==========
        createFlight("FL-027", "AP-001", "FR-011", 1300000, today.withHour(7).withMinute(30), today.withHour(9).withMinute(45), "AVAILABLE");
        createFlight("FL-028", "AP-002", "FR-011", 1250000, today.withHour(14).withMinute(0), today.withHour(16).withMinute(15), "AVAILABLE");

        // ========== CXR -> HAN (Today) ==========
        createFlight("FL-029", "AP-001", "FR-012", 1300000, today.withHour(10).withMinute(30), today.withHour(12).withMinute(45), "AVAILABLE");
        createFlight("FL-030", "AP-002", "FR-012", 1250000, today.withHour(17).withMinute(0), today.withHour(19).withMinute(15), "AVAILABLE");

        // ========== SGN -> DLI (Today) ==========
        createFlight("FL-031", "AP-003", "FR-013", 700000, today.withHour(8).withMinute(0), today.withHour(9).withMinute(0), "AVAILABLE");
        createFlight("FL-032", "AP-004", "FR-013", 750000, today.withHour(16).withMinute(0), today.withHour(17).withMinute(0), "AVAILABLE");

        // ========== DLI -> SGN (Today) ==========
        createFlight("FL-033", "AP-003", "FR-014", 700000, today.withHour(10).withMinute(0), today.withHour(11).withMinute(0), "AVAILABLE");
        createFlight("FL-034", "AP-004", "FR-014", 750000, today.withHour(18).withMinute(0), today.withHour(19).withMinute(0), "AVAILABLE");

        // ========== Tomorrow (Day 2) ==========
        createFlight("FL-035", "AP-001", "FR-001", 1500000, tomorrow.withHour(6).withMinute(0), tomorrow.withHour(8).withMinute(15), "AVAILABLE");
        createFlight("FL-036", "AP-002", "FR-001", 1200000, tomorrow.withHour(12).withMinute(0), tomorrow.withHour(14).withMinute(15), "AVAILABLE");
        createFlight("FL-037", "AP-001", "FR-002", 1500000, tomorrow.withHour(7).withMinute(0), tomorrow.withHour(9).withMinute(15), "AVAILABLE");
        createFlight("FL-038", "AP-002", "FR-002", 1250000, tomorrow.withHour(14).withMinute(0), tomorrow.withHour(16).withMinute(15), "AVAILABLE");
        createFlight("FL-039", "AP-003", "FR-003", 900000, tomorrow.withHour(8).withMinute(30), tomorrow.withHour(9).withMinute(50), "AVAILABLE");
        createFlight("FL-040", "AP-003", "FR-004", 900000, tomorrow.withHour(11).withMinute(0), tomorrow.withHour(12).withMinute(20), "AVAILABLE");

        // ========== Day 3 ==========
        createFlight("FL-041", "AP-001", "FR-001", 1450000, day3.withHour(9).withMinute(0), day3.withHour(11).withMinute(15), "AVAILABLE");
        createFlight("FL-042", "AP-002", "FR-001", 1150000, day3.withHour(15).withMinute(30), day3.withHour(17).withMinute(45), "AVAILABLE");
        createFlight("FL-043", "AP-001", "FR-002", 1450000, day3.withHour(10).withMinute(0), day3.withHour(12).withMinute(15), "AVAILABLE");
        createFlight("FL-044", "AP-002", "FR-002", 1200000, day3.withHour(16).withMinute(30), day3.withHour(18).withMinute(45), "AVAILABLE");
        createFlight("FL-045", "AP-003", "FR-005", 800000, day3.withHour(10).withMinute(0), day3.withHour(11).withMinute(30), "AVAILABLE");
        createFlight("FL-046", "AP-003", "FR-006", 800000, day3.withHour(13).withMinute(0), day3.withHour(14).withMinute(30), "AVAILABLE");

        // ========== Day 4 ==========
        createFlight("FL-047", "AP-001", "FR-007", 1750000, day4.withHour(8).withMinute(0), day4.withHour(10).withMinute(30), "AVAILABLE");
        createFlight("FL-048", "AP-001", "FR-008", 1750000, day4.withHour(13).withMinute(0), day4.withHour(15).withMinute(30), "AVAILABLE");
        createFlight("FL-049", "AP-003", "FR-009", 1000000, day4.withHour(9).withMinute(0), day4.withHour(10).withMinute(0), "AVAILABLE");
        createFlight("FL-050", "AP-003", "FR-010", 1000000, day4.withHour(12).withMinute(0), day4.withHour(13).withMinute(0), "AVAILABLE");

        // ========== Day 5 ==========
        createFlight("FL-051", "AP-002", "FR-001", 1300000, day5.withHour(7).withMinute(0), day5.withHour(9).withMinute(15), "AVAILABLE");
        createFlight("FL-052", "AP-003", "FR-001", 1200000, day5.withHour(13).withMinute(0), day5.withHour(15).withMinute(15), "AVAILABLE");
        createFlight("FL-053", "AP-002", "FR-002", 1300000, day5.withHour(8).withMinute(0), day5.withHour(10).withMinute(15), "AVAILABLE");
        createFlight("FL-054", "AP-003", "FR-002", 1200000, day5.withHour(14).withMinute(0), day5.withHour(16).withMinute(15), "AVAILABLE");
        createFlight("FL-055", "AP-004", "FR-003", 920000, day5.withHour(9).withMinute(30), day5.withHour(10).withMinute(50), "AVAILABLE");
        createFlight("FL-056", "AP-004", "FR-004", 920000, day5.withHour(12).withMinute(0), day5.withHour(13).withMinute(20), "AVAILABLE");

        // ========== Day 6 ==========
        createFlight("FL-057", "AP-001", "FR-001", 1600000, day6.withHour(6).withMinute(30), day6.withHour(8).withMinute(45), "AVAILABLE");
        createFlight("FL-058", "AP-002", "FR-001", 1250000, day6.withHour(11).withMinute(0), day6.withHour(13).withMinute(15), "AVAILABLE");
        createFlight("FL-059", "AP-003", "FR-001", 1150000, day6.withHour(16).withMinute(0), day6.withHour(18).withMinute(15), "AVAILABLE");
        createFlight("FL-060", "AP-001", "FR-002", 1600000, day6.withHour(7).withMinute(30), day6.withHour(9).withMinute(45), "AVAILABLE");
        createFlight("FL-061", "AP-002", "FR-002", 1250000, day6.withHour(12).withMinute(0), day6.withHour(14).withMinute(15), "AVAILABLE");
        createFlight("FL-062", "AP-003", "FR-002", 1150000, day6.withHour(17).withMinute(0), day6.withHour(19).withMinute(15), "AVAILABLE");
        createFlight("FL-063", "AP-001", "FR-011", 1300000, day6.withHour(8).withMinute(0), day6.withHour(10).withMinute(15), "AVAILABLE");
        createFlight("FL-064", "AP-001", "FR-012", 1300000, day6.withHour(11).withMinute(0), day6.withHour(13).withMinute(15), "AVAILABLE");
        createFlight("FL-065", "AP-003", "FR-013", 700000, day6.withHour(9).withMinute(0), day6.withHour(10).withMinute(0), "AVAILABLE");
        createFlight("FL-066", "AP-003", "FR-014", 700000, day6.withHour(11).withMinute(30), day6.withHour(12).withMinute(30), "AVAILABLE");

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
