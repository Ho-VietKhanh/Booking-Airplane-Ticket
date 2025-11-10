package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se196411.booking_ticket.model.dto.BookingSessionDTO;
import se196411.booking_ticket.model.dto.PassengerInfoDTO;
import se196411.booking_ticket.model.entity.*;
import se196411.booking_ticket.service.*;
import se196411.booking_ticket.utils.RandomId;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/booking")
public class BookingServiceController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private AdditionalServiceService additionalServiceService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private se196411.booking_ticket.repository.FlightsRepository flightsRepository;

    @Autowired
    private MealService mealService;

    @Autowired
    private LuggageService luggageService;

    @Autowired
    private se196411.booking_ticket.repository.SeatRepository seatRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FlightsService flightsService;

    // ✅ NEW: Payment preview page - hiển thị payment mà KHÔNG tạo booking
    @GetMapping("/payment-preview")
    public String showPaymentPreview(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        if (bookingSession == null || bookingSession.getPassengers() == null || bookingSession.getPassengers().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No booking session found");
            return "redirect:/";
        }

        // ✅ DEBUG: Log session data
        System.out.println("\n========================================");
        System.out.println("📊 PAYMENT-PREVIEW DEBUG");
        System.out.println("========================================");
        System.out.println("Round trip: " + bookingSession.isRoundTrip());
        System.out.println("Flight ID: " + bookingSession.getFlightId());
        System.out.println("Return Flight ID: " + bookingSession.getReturnFlightId());
        System.out.println("Passenger count: " + bookingSession.getPassengerCount());

        for (int i = 0; i < bookingSession.getPassengers().size(); i++) {
            PassengerInfoDTO p = bookingSession.getPassengers().get(i);
            System.out.println("\n--- Passenger " + (i + 1) + " ---");
            System.out.println("  Outbound Seat ID: " + p.getSeatId());
            System.out.println("  Outbound Luggage ID: " + p.getLuggageId());
            System.out.println("  Outbound Meal ID: " + p.getMealId());
            if (bookingSession.isRoundTrip()) {
                System.out.println("  Return Seat ID: " + p.getReturnSeatId());
                System.out.println("  Return Luggage ID: " + p.getReturnLuggageId());
                System.out.println("  Return Meal ID: " + p.getReturnMealId());
            }
        }
        System.out.println("========================================\n");

        // Calculate total amount from session data
        BigDecimal totalAmount = calculateTotalFromSession(bookingSession);

        // Get payment methods
        List<PaymentMethodEntity> methods = paymentMethodService.getAllPaymentMethods();

        // ✅ THÊM: Lấy thông tin chuyến bay và dịch vụ để hiển thị chi tiết
        FlightsEntity outboundFlight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
        FlightsEntity returnFlight = null;
        if (bookingSession.isRoundTrip() && bookingSession.getReturnFlightId() != null) {
            returnFlight = flightsRepository.findById(bookingSession.getReturnFlightId()).orElse(null);
        }

        // ✅ NEW: Tính toán chi tiết giá để hiển thị
        int outboundBusinessCount = 0;
        BigDecimal outboundLuggageTotal = BigDecimal.ZERO;
        BigDecimal outboundMealTotal = BigDecimal.ZERO;

        int returnBusinessCount = 0;
        BigDecimal returnLuggageTotal = BigDecimal.ZERO;
        BigDecimal returnMealTotal = BigDecimal.ZERO;

        for (PassengerInfoDTO passenger : bookingSession.getPassengers()) {
            // Outbound flight services
            if (passenger.getSeatId() != null) {
                SeatEntity seat = seatRepository.findById(passenger.getSeatId()).orElse(null);
                if (seat != null && "Business".equalsIgnoreCase(seat.getSeatClass())) {
                    outboundBusinessCount++;
                }
            }

            if (passenger.getLuggageId() != null) {
                LuggageEntity luggage = luggageService.findById(passenger.getLuggageId());
                if (luggage != null) {
                    outboundLuggageTotal = outboundLuggageTotal.add(luggage.getPrice());
                }
            }

            if (passenger.getMealId() != null) {
                MealEntity meal = mealService.findById(passenger.getMealId());
                if (meal != null) {
                    outboundMealTotal = outboundMealTotal.add(meal.getPrice());
                }
            }

            // Return flight services
            if (bookingSession.isRoundTrip()) {
                if (passenger.getReturnSeatId() != null) {
                    SeatEntity seat = seatRepository.findById(passenger.getReturnSeatId()).orElse(null);
                    if (seat != null && "Business".equalsIgnoreCase(seat.getSeatClass())) {
                        returnBusinessCount++;
                    }
                }

                if (passenger.getReturnLuggageId() != null) {
                    LuggageEntity luggage = luggageService.findById(passenger.getReturnLuggageId());
                    if (luggage != null) {
                        returnLuggageTotal = returnLuggageTotal.add(luggage.getPrice());
                    }
                }

                if (passenger.getReturnMealId() != null) {
                    MealEntity meal = mealService.findById(passenger.getReturnMealId());
                    if (meal != null) {
                        returnMealTotal = returnMealTotal.add(meal.getPrice());
                    }
                }
            }
        }

        // ✅ DEBUG: Log calculated totals
        System.out.println("\n========================================");
        System.out.println("💰 CALCULATED TOTALS");
        System.out.println("========================================");
        System.out.println("Outbound Business Count: " + outboundBusinessCount);
        System.out.println("Outbound Luggage Total: " + outboundLuggageTotal);
        System.out.println("Outbound Meal Total: " + outboundMealTotal);
        System.out.println("Return Business Count: " + returnBusinessCount);
        System.out.println("Return Luggage Total: " + returnLuggageTotal);
        System.out.println("Return Meal Total: " + returnMealTotal);
        System.out.println("Total Amount: " + totalAmount);
        System.out.println("========================================\n");

        // Pass booking session to view
        model.addAttribute("bookingSession", bookingSession);
        model.addAttribute("amount", totalAmount);
        model.addAttribute("paymentMethods", methods);
        model.addAttribute("outboundFlight", outboundFlight);
        model.addAttribute("returnFlight", returnFlight);

        // Pass detailed breakdown
        model.addAttribute("outboundBusinessCount", outboundBusinessCount);
        model.addAttribute("outboundLuggageTotal", outboundLuggageTotal);
        model.addAttribute("outboundMealTotal", outboundMealTotal);
        model.addAttribute("returnBusinessCount", returnBusinessCount);
        model.addAttribute("returnLuggageTotal", returnLuggageTotal);
        model.addAttribute("returnMealTotal", returnMealTotal);

        return "payment";
    }

    /**
     * Helper method to calculate total price from booking session
     */
    private BigDecimal calculateTotalFromSession(BookingSessionDTO bookingSession) {
        BigDecimal total = BigDecimal.ZERO;

        try {
            // Get outbound flight
            FlightsEntity outboundFlight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
            if (outboundFlight == null) return BigDecimal.ZERO;

            // Calculate outbound flight costs
            for (PassengerInfoDTO passenger : bookingSession.getPassengers()) {
                BigDecimal passengerCost = outboundFlight.getBasePrice();

                // Add seat surcharge if Business class
                if (passenger.getSeatId() != null) {
                    SeatEntity seat = seatRepository.findById(passenger.getSeatId()).orElse(null);
                    if (seat != null && "Business".equalsIgnoreCase(seat.getSeatClass())) {
                        passengerCost = passengerCost.add(new BigDecimal("1800000"));
                    }
                }

                // Add meal cost
                if (passenger.getMealId() != null) {
                    MealEntity meal = mealService.findById(passenger.getMealId());
                    if (meal != null) {
                        passengerCost = passengerCost.add(meal.getPrice());
                    }
                }

                // Add luggage cost
                if (passenger.getLuggageId() != null) {
                    LuggageEntity luggage = luggageService.findById(passenger.getLuggageId());
                    if (luggage != null) {
                        passengerCost = passengerCost.add(luggage.getPrice());
                    }
                }

                total = total.add(passengerCost);
            }

            // Calculate return flight costs if round trip
            if (bookingSession.isRoundTrip() && bookingSession.getReturnFlightId() != null) {
                FlightsEntity returnFlight = flightsRepository.findById(bookingSession.getReturnFlightId()).orElse(null);
                if (returnFlight != null) {
                    for (PassengerInfoDTO passenger : bookingSession.getPassengers()) {
                        BigDecimal passengerCost = returnFlight.getBasePrice();

                        // Add seat surcharge if Business class
                        if (passenger.getReturnSeatId() != null) {
                            SeatEntity seat = seatRepository.findById(passenger.getReturnSeatId()).orElse(null);
                            if (seat != null && "Business".equalsIgnoreCase(seat.getSeatClass())) {
                                passengerCost = passengerCost.add(new BigDecimal("1800000"));
                            }
                        }

                        // Add meal cost
                        if (passenger.getReturnMealId() != null) {
                            MealEntity meal = mealService.findById(passenger.getReturnMealId());
                            if (meal != null) {
                                passengerCost = passengerCost.add(meal.getPrice());
                            }
                        }

                        // Add luggage cost
                        if (passenger.getReturnLuggageId() != null) {
                            LuggageEntity luggage = luggageService.findById(passenger.getReturnLuggageId());
                            if (luggage != null) {
                                passengerCost = passengerCost.add(luggage.getPrice());
                            }
                        }

                        total = total.add(passengerCost);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error calculating total from session: " + e.getMessage());
            e.printStackTrace();
        }

        return total;
    }

    // API endpoint để lấy danh sách seat ID đã được đặt cho một chuyến bay
    @GetMapping("/api/flights/{flightId}/reserved-seats")
    @ResponseBody
    public Set<String> getReservedSeats(@PathVariable String flightId) {
        return seatService.getReservedSeatIdsByFlightId(flightId);
    }

    // Screen 0: Passenger Information Form
    @GetMapping("/passenger-info")
    public String showPassengerInfoForm(@RequestParam(required = false) String flightId,
                                        @RequestParam(required = false) String outboundFlightId,
                                        @RequestParam(required = false) String returnFlightId,
                                        @RequestParam(defaultValue = "1") int adults,
                                        @RequestParam(defaultValue = "0") int children,
                                        Model model) {
        // Determine if this is a round trip or one way
        boolean isRoundTrip = (outboundFlightId != null && returnFlightId != null);

        String actualFlightId;
        if (isRoundTrip) {
            actualFlightId = outboundFlightId;
            model.addAttribute("outboundFlightId", outboundFlightId);
            model.addAttribute("returnFlightId", returnFlightId);
            model.addAttribute("isRoundTrip", true);

            // Load return flight info
            try {
                se196411.booking_ticket.model.entity.FlightsEntity returnFlight =
                    flightsRepository.findById(returnFlightId).orElse(null);
                model.addAttribute("returnFlight", returnFlight);
            } catch (Exception e) {
                System.err.println("Error loading return flight: " + e.getMessage());
                model.addAttribute("returnFlight", null);
            }
        } else {
            actualFlightId = flightId != null ? flightId : "FL-001";
            model.addAttribute("isRoundTrip", false);
        }

        model.addAttribute("flightId", actualFlightId);
        model.addAttribute("numAdults", adults);
        model.addAttribute("numChildren", children);

        // Load flight entity with full nested information (flightRoute, airports, airplane)
        try {
            se196411.booking_ticket.model.entity.FlightsEntity flight =
                flightsRepository.findById(actualFlightId).orElse(null);
            if (flight != null) {
                model.addAttribute("flight", flight);
            } else {
                System.out.println("Flight not found: " + actualFlightId);
                model.addAttribute("flight", null);
            }
        } catch (Exception e) {
            System.err.println("Error loading flight: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("flight", null);
        }

        return "form_booking";
    }

    // Handle passenger info form submission
    @PostMapping("/passenger-info")
    public String submitPassengerInfo(@RequestParam Map<String, String> formData,
                                      HttpSession session) {
        // Create booking session DTO
        BookingSessionDTO bookingSession = new BookingSessionDTO();
        bookingSession.setEmail(formData.get("email"));
        bookingSession.setPhone(formData.get("phone"));
        bookingSession.setFlightId(formData.getOrDefault("flightId", "FL-001"));

        // Check if this is a round trip
        String returnFlightId = formData.get("returnFlightId");
        boolean isRoundTrip = (returnFlightId != null && !returnFlightId.isEmpty());
        bookingSession.setRoundTrip(isRoundTrip);
        bookingSession.setReturnFlightId(returnFlightId);
        bookingSession.setCurrentLeg("outbound"); // Start with outbound flight

        System.out.println("\n=== PASSENGER INFO SUBMISSION ===");
        System.out.println("📊 Form Data:");
        System.out.println("   - Flight ID: " + bookingSession.getFlightId());
        System.out.println("   - Return Flight ID: " + returnFlightId);
        System.out.println("   - Is Round Trip: " + isRoundTrip);
        System.out.println("   - Current Leg: outbound");

        // Parse passengers from form data - handle multiple passengers with potential gaps
        // Check up to 10 passengers (reasonable limit)
        for (int passengerIndex = 1; passengerIndex <= 10; passengerIndex++) {
            // Check if this passenger exists
            if (!formData.containsKey("passenger" + passengerIndex + "_title")) {
                continue; // Skip this index if passenger doesn't exist
            }

            PassengerInfoDTO passenger = new PassengerInfoDTO();
            passenger.setTitle(formData.get("passenger" + passengerIndex + "_title"));
            passenger.setLastName(formData.get("passenger" + passengerIndex + "_lastName"));
            passenger.setFirstName(formData.get("passenger" + passengerIndex + "_firstName"));

            // Parse birth date
            String dobString = formData.get("passenger" + passengerIndex + "_dob");
            if (dobString != null && !dobString.isEmpty()) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    passenger.setBirthDate(sdf.parse(dobString));
                } catch (Exception e) {
                    // If parse fails, set default date
                    try {
                        passenger.setBirthDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));
                    } catch (Exception ex) {
                        passenger.setBirthDate(new java.util.Date());
                    }
                }
            } else {
                // If no date provided, set default date
                try {
                    passenger.setBirthDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));
                } catch (Exception e) {
                    passenger.setBirthDate(new java.util.Date());
                }
            }

            String genderStr = formData.get("passenger" + passengerIndex + "_gender");
            passenger.setGender("male".equalsIgnoreCase(genderStr) || "true".equalsIgnoreCase(genderStr));

            passenger.setCccd(formData.get("passenger" + passengerIndex + "_idCard"));
            passenger.setNationality(formData.get("passenger" + passengerIndex + "_nationality"));

            // sdt and email are at booking level
            passenger.setSdt(null); // Will use booking phone
            passenger.setEmail(null); // Will use booking email

            // Initialize with base flight price
            passenger.setPrice(BigDecimal.ZERO);
            passenger.setStatus("PENDING");

            bookingSession.addPassenger(passenger);
        }

        // Validate that at least one passenger exists
        if (bookingSession.getPassengers() == null || bookingSession.getPassengers().isEmpty()) {
            // Redirect back with error
            return "redirect:/booking/passenger-info?flightId=" + bookingSession.getFlightId() + "&error=noPassengers";
        }

        // Save to session
        session.setAttribute("bookingSession", bookingSession);

        System.out.println("Parsed " + bookingSession.getPassengers().size() + " passengers from form");
        System.out.println("Round trip: " + isRoundTrip + ", Return flight: " + returnFlightId);

        // Redirect to seat selection first
        return "redirect:/booking/services/seats?flightId=" + bookingSession.getFlightId();
    }

    // Screen 1: Service Selection Menu
    @GetMapping("/services/select")
    public String showServiceSelection(@RequestParam(required = false) String flightId,
                                        @RequestParam(required = false) Integer passengers,
                                        HttpSession session,
                                        Model model) {
        // Get booking session from session if available
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        if (bookingSession != null) {
            model.addAttribute("flightId", bookingSession.getFlightId());
            model.addAttribute("passengerCount", bookingSession.getPassengerCount());
            model.addAttribute("email", bookingSession.getEmail());
        } else {
            model.addAttribute("flightId", flightId != null ? flightId : "FL-001");
            model.addAttribute("passengerCount", passengers != null ? passengers : 1);
        }

        return "service-selection";
    }

    // Screen 2: Seat Selection
    @GetMapping("/services/seats")
    public String showSeatSelection(@RequestParam(required = false) String flightId,
                                    @RequestParam(required = false) Integer passengers,
                                    HttpSession session,
                                    Model model) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        String actualFlightId;
        int passengerCount;

        if (bookingSession != null) {
            // Determine which flight to show based on current leg
            if ("return".equals(bookingSession.getCurrentLeg()) && bookingSession.getReturnFlightId() != null) {
                actualFlightId = bookingSession.getReturnFlightId();
            } else {
                actualFlightId = bookingSession.getFlightId();
            }
            passengerCount = bookingSession.getPassengerCount();
            model.addAttribute("bookingSession", bookingSession);
        } else {
            actualFlightId = flightId != null ? flightId : "FL-001";
            passengerCount = passengers != null ? passengers : 1;
        }

        var seats = seatService.getSeatsByFlightId(actualFlightId);
        FlightsEntity flight = flightsRepository.findById(actualFlightId).orElse(null);

        model.addAttribute("seats", seats);
        model.addAttribute("flightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("flight", flight);

        return "seat-selection";
    }

    // Screen 3: Baggage and Meal Selection
    @GetMapping("/services/extras")
    public String showExtrasSelection(@RequestParam(required = false) String flightId,
                                      @RequestParam(required = false) Integer passengers,
                                      @RequestParam(required = false) String selectedSeats,
                                      HttpSession session,
                                      Model model) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        int passengerCount;
        String actualFlightId;

        if (bookingSession != null) {
            passengerCount = bookingSession.getPassengerCount();
            // Determine which flight to show based on current leg
            if ("return".equals(bookingSession.getCurrentLeg()) && bookingSession.getReturnFlightId() != null) {
                actualFlightId = bookingSession.getReturnFlightId();
            } else {
                actualFlightId = bookingSession.getFlightId();
            }
            model.addAttribute("bookingSession", bookingSession);
        } else {
            passengerCount = passengers != null ? passengers : 1;
            actualFlightId = flightId != null ? flightId : "FL-001";
        }

        var baggageOptions = additionalServiceService.getAllBaggageOptions();
        var mealOptions = additionalServiceService.getAllMealOptions();

        // ✅ Get flight info to pass base price to template
        FlightsEntity flight = flightsRepository.findById(actualFlightId).orElse(null);

        // ✅ Get seats to pass seat class information
        var seats = seatService.getSeatsByFlightId(actualFlightId);

        model.addAttribute("flightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("baggageOptions", baggageOptions);
        model.addAttribute("mealOptions", mealOptions);
        model.addAttribute("selectedSeats", selectedSeats != null ? selectedSeats : "");
        model.addAttribute("flight", flight);
        model.addAttribute("seats", seats);

        return "extras-selection";
    }

    // API endpoint to reserve seat
    @PostMapping("/services/seats/reserve")
    @ResponseBody
    public String reserveSeat(@RequestParam String seatId,
                            @RequestParam(required = false) String bookingId) {
        boolean success = seatService.reserveSeat(seatId, bookingId != null ? bookingId : "TEMP");
        return success ? "success" : "failed";
    }

    // API to save seat selection to session (called from JavaScript)
    @PostMapping("/services/seats/save-to-session")
    @ResponseBody
    public String saveSeatToSession(@RequestParam int passengerIndex,
                                   @RequestParam(required = false) String seatId,
                                   HttpSession session) {
        try {
            BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

            if (bookingSession == null || bookingSession.getPassengers() == null) {
                return "{\"success\": false, \"message\": \"No booking session found\"}";
            }

            if (passengerIndex < 0 || passengerIndex >= bookingSession.getPassengers().size()) {
                return "{\"success\": false, \"message\": \"Invalid passenger index\"}";
            }

            // Check which leg we're on (outbound or return)
            String currentLeg = bookingSession.getCurrentLeg();
            PassengerInfoDTO passenger = bookingSession.getPassengers().get(passengerIndex);

            if ("return".equals(currentLeg)) {
                // Save to return seat
                passenger.setReturnSeatId(seatId);
            } else {
                // Save to outbound seat (default)
                passenger.setSeatId(seatId);
            }

            // Save back to session
            session.setAttribute("bookingSession", bookingSession);

            System.out.println("✅ Saved " + currentLeg + " seat " + seatId + " for passenger " + passengerIndex + " to session");

            return "{\"success\": true, \"message\": \"Seat saved to session\"}";
        } catch (Exception e) {
            System.err.println("Error saving seat to session: " + e.getMessage());
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // Handle seat selection submission
    @PostMapping("/services/seats/submit")
    public String submitSeatSelection(@RequestParam(required = false) List<String> selectedSeats,
                                     @RequestParam Map<String, String> allParams,
                                     HttpSession session) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        System.out.println("=== Seat Selection Submission ===");
        System.out.println("All parameters received: " + allParams);
        System.out.println("Received selectedSeats: " + selectedSeats);

        // Filter out empty or null seat IDs
        if (selectedSeats != null) {
            selectedSeats = selectedSeats.stream()
                .filter(s -> s != null && !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
            System.out.println("Filtered selectedSeats: " + selectedSeats);
        }

        if (selectedSeats == null || selectedSeats.isEmpty()) {
            System.out.println("ERROR: No seats selected!");
            // Redirect back with error
            return "redirect:/booking/services/seats?flightId=" +
                   (bookingSession != null ? bookingSession.getFlightId() : "FL-001") +
                   "&error=noSeats";
        }

        if (bookingSession != null && bookingSession.getPassengers() != null) {
            System.out.println("Passenger count: " + bookingSession.getPassengers().size());

            String currentLeg = bookingSession.getCurrentLeg();

            // Assign seats to passengers based on current leg
            for (int i = 0; i < selectedSeats.size() && i < bookingSession.getPassengers().size(); i++) {
                String seatId = selectedSeats.get(i);
                PassengerInfoDTO passenger = bookingSession.getPassengers().get(i);

                if ("return".equals(currentLeg)) {
                    passenger.setReturnSeatId(seatId);
                    System.out.println("Assigned return seat " + seatId + " to passenger " + i);
                } else {
                    passenger.setSeatId(seatId);
                    System.out.println("Assigned outbound seat " + seatId + " to passenger " + i);
                }
            }

            // Save back to session
            session.setAttribute("bookingSession", bookingSession);
            System.out.println("Booking session updated and saved");
        } else {
            System.out.println("ERROR: BookingSession or passengers is null!");
        }

        // ✅ LUỒNG MỚI: Sau khi chọn ghế, chuyển sang chọn dịch vụ bổ sung cho cùng chuyến bay
        String nextFlightId = bookingSession != null ?
            ("return".equals(bookingSession.getCurrentLeg()) ? bookingSession.getReturnFlightId() : bookingSession.getFlightId())
            : "FL-001";

        return "redirect:/booking/services/extras?flightId=" + nextFlightId;
    }

    // Handle extras (meal & luggage) selection submission
    @PostMapping("/services/extras/submit")
    public String submitExtrasSelection(@RequestParam Map<String, String> formData,
                                       HttpSession session) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        System.out.println("\n=== EXTRAS SELECTION SUBMISSION ===");
        System.out.println("📋 BookingSession null? " + (bookingSession == null));

        if (bookingSession == null || bookingSession.getPassengers() == null || bookingSession.getPassengers().isEmpty()) {
            System.out.println("❌ No booking session or passengers, redirecting to passenger-info");
            return "redirect:/booking/passenger-info";
        }

        String currentLeg = bookingSession.getCurrentLeg();
        boolean isRoundTrip = bookingSession.isRoundTrip();
        String returnFlightId = bookingSession.getReturnFlightId();

        System.out.println("📊 SESSION INFO:");
        System.out.println("   - Current Leg: " + currentLeg);
        System.out.println("   - Is Round Trip: " + isRoundTrip);
        System.out.println("   - Return Flight ID: " + returnFlightId);
        System.out.println("   - Flight ID: " + bookingSession.getFlightId());
        System.out.println("   - Passengers: " + bookingSession.getPassengerCount());

        // Process each passenger's selections
        for (int i = 0; i < bookingSession.getPassengers().size(); i++) {
            PassengerInfoDTO passenger = bookingSession.getPassengers().get(i);

            // Get meal selection for this passenger
            String mealKey = "passenger_" + i + "_meal";
            String mealValue = formData.get(mealKey);
            Integer mealId = null;
            if (mealValue != null && !mealValue.isEmpty()) {
                try {
                    mealId = Integer.parseInt(mealValue);
                } catch (NumberFormatException e) {
                    mealId = null;
                }
            }

            // Get luggage selection for this passenger
            String luggageKey = "passenger_" + i + "_luggage";
            String luggageValue = formData.get(luggageKey);
            Integer luggageId = null;
            if (luggageValue != null && !luggageValue.isEmpty()) {
                try {
                    luggageId = Integer.parseInt(luggageValue);
                } catch (NumberFormatException e) {
                    luggageId = null;
                }
            }

            // Save based on current leg
            if ("return".equals(currentLeg)) {
                passenger.setReturnMealId(mealId);
                passenger.setReturnLuggageId(luggageId);
                System.out.println("   - Passenger " + i + ": Return meal=" + mealId + ", luggage=" + luggageId);
            } else {
                passenger.setMealId(mealId);
                passenger.setLuggageId(luggageId);
                System.out.println("   - Passenger " + i + ": Outbound meal=" + mealId + ", luggage=" + luggageId);
            }
        }

        session.setAttribute("bookingSession", bookingSession);

        // ✅ KIỂM TRA VÉ KHỨ HỒI
        System.out.println("\n🔍 CHECKING ROUND TRIP CONDITIONS:");
        System.out.println("   - isRoundTrip: " + isRoundTrip);
        System.out.println("   - returnFlightId != null: " + (returnFlightId != null));
        System.out.println("   - !\"return\".equals(currentLeg): " + (!"return".equals(currentLeg)));

        boolean shouldRedirectToReturnSeat = isRoundTrip && returnFlightId != null && !"return".equals(currentLeg);
        System.out.println("   - Should redirect to return seat: " + shouldRedirectToReturnSeat);

        if (shouldRedirectToReturnSeat) {
            // Switch to return leg
            bookingSession.setCurrentLeg("return");
            session.setAttribute("bookingSession", bookingSession);

            String redirectUrl = "redirect:/booking/services/seats-return?returnFlightId=" + returnFlightId;
            System.out.println("\n✅ REDIRECTING TO RETURN SEAT SELECTION");
            System.out.println("   URL: " + redirectUrl);
            System.out.println("========================================\n");

            return redirectUrl;
        }

        // ✅ FIX: KHÔNG TẠO BOOKING Ở ĐÂY - chỉ redirect sang payment
        // Booking và ticket sẽ được tạo KHI SUBMIT payment
        System.out.println("\n✅ REDIRECTING TO PAYMENT (Booking will be created on payment confirm)");
        System.out.println("========================================\n");

        return "redirect:/booking/payment-preview";
    }

    // ✅ NEW: Screen for Return Flight Seat Selection
    @GetMapping("/services/seats-return")
    public String showReturnSeatSelection(@RequestParam(required = false) String returnFlightId,
                                         HttpSession session,
                                         Model model) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        String actualFlightId;
        int passengerCount;

        if (bookingSession != null) {
            actualFlightId = bookingSession.getReturnFlightId();
            passengerCount = bookingSession.getPassengerCount();
            model.addAttribute("bookingSession", bookingSession);

            // ✅ Thêm thông tin chuyến đi
            if (bookingSession.getFlightId() != null) {
                FlightsEntity outboundFlight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
                model.addAttribute("outboundFlight", outboundFlight);

                // Lấy thông tin ghế chuyến đi
                var outboundSeats = seatService.getSeatsByFlightId(bookingSession.getFlightId());
                model.addAttribute("outboundSeats", outboundSeats);
            }
        } else {
            actualFlightId = returnFlightId != null ? returnFlightId : "FL-001";
            passengerCount = 1;
        }

        var seats = seatService.getSeatsByFlightId(actualFlightId);
        FlightsEntity returnFlight = flightsRepository.findById(actualFlightId).orElse(null);

        // ✅ Thêm dịch vụ để tính giá chuyến đi
        var baggageOptions = additionalServiceService.getAllBaggageOptions();
        var mealOptions = additionalServiceService.getAllMealOptions();

        model.addAttribute("seats", seats);
        model.addAttribute("returnFlightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("returnFlight", returnFlight);
        model.addAttribute("baggageOptions", baggageOptions);
        model.addAttribute("mealOptions", mealOptions);

        return "seat-selection-return";
    }

    // ✅ NEW: API to save return seat selection to session
    @PostMapping("/services/seats-return/save-to-session")
    @ResponseBody
    public String saveReturnSeatToSession(@RequestParam int passengerIndex,
                                         @RequestParam(required = false) String seatId,
                                         HttpSession session) {
        try {
            BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

            if (bookingSession == null || bookingSession.getPassengers() == null) {
                return "{\"success\": false, \"message\": \"No booking session found\"}";
            }

            if (passengerIndex < 0 || passengerIndex >= bookingSession.getPassengers().size()) {
                return "{\"success\": false, \"message\": \"Invalid passenger index\"}";
            }

            PassengerInfoDTO passenger = bookingSession.getPassengers().get(passengerIndex);
            passenger.setReturnSeatId(seatId);

            // Save back to session
            session.setAttribute("bookingSession", bookingSession);

            System.out.println("✅ Saved return seat " + seatId + " for passenger " + passengerIndex + " to session");

            return "{\"success\": true, \"message\": \"Return seat saved to session\"}";
        } catch (Exception e) {
            System.err.println("Error saving return seat to session: " + e.getMessage());
            e.printStackTrace();
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // ✅ NEW: Handle return flight seat selection submission
    @PostMapping("/services/seats-return/submit")
    public String submitReturnSeatSelection(@RequestParam(required = false) List<String> selectedReturnSeats,
                                           @RequestParam Map<String, String> allParams,
                                           HttpSession session) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        System.out.println("=== Return Seat Selection Submission ===");
        System.out.println("All parameters received: " + allParams);
        System.out.println("Received selectedReturnSeats: " + selectedReturnSeats);

        // Filter out empty or null seat IDs
        if (selectedReturnSeats != null) {
            selectedReturnSeats = selectedReturnSeats.stream()
                .filter(s -> s != null && !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
            System.out.println("Filtered selectedReturnSeats: " + selectedReturnSeats);
        }

        if (selectedReturnSeats == null || selectedReturnSeats.isEmpty()) {
            System.out.println("ERROR: No return seats selected!");
            return "redirect:/booking/services/seats-return?returnFlightId=" +
                   (bookingSession != null ? bookingSession.getReturnFlightId() : "FL-001") +
                   "&error=noSeats";
        }

        if (bookingSession != null && bookingSession.getPassengers() != null) {
            // Assign return seats to passengers
            for (int i = 0; i < selectedReturnSeats.size() && i < bookingSession.getPassengers().size(); i++) {
                String seatId = selectedReturnSeats.get(i);
                PassengerInfoDTO passenger = bookingSession.getPassengers().get(i);
                passenger.setReturnSeatId(seatId);
                System.out.println("Assigned return seat " + seatId + " to passenger " + i);
            }

            session.setAttribute("bookingSession", bookingSession);
        }

        // Redirect to extras selection for return flight
        return "redirect:/booking/services/extras-return?returnFlightId=" + bookingSession.getReturnFlightId();
    }

    // ✅ NEW: Screen for Return Flight Extras Selection
    @GetMapping("/services/extras-return")
    public String showReturnExtrasSelection(@RequestParam(required = false) String returnFlightId,
                                           HttpSession session,
                                           Model model) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        int passengerCount;
        String actualFlightId;

        if (bookingSession != null) {
            passengerCount = bookingSession.getPassengerCount();
            actualFlightId = bookingSession.getReturnFlightId();
            model.addAttribute("bookingSession", bookingSession);

            // ✅ THÊM: Lấy thông tin chuyến bay đi
            if (bookingSession.getFlightId() != null) {
                FlightsEntity outboundFlight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
                var outboundSeats = seatService.getSeatsByFlightId(bookingSession.getFlightId());
                model.addAttribute("outboundFlight", outboundFlight);
                model.addAttribute("outboundSeats", outboundSeats);
            }
        } else {
            passengerCount = 1;
            actualFlightId = returnFlightId != null ? returnFlightId : "FL-001";
        }

        var baggageOptions = additionalServiceService.getAllBaggageOptions();
        var mealOptions = additionalServiceService.getAllMealOptions();
        FlightsEntity returnFlight = flightsRepository.findById(actualFlightId).orElse(null);
        var seats = seatService.getSeatsByFlightId(actualFlightId);

        model.addAttribute("returnFlightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("baggageOptions", baggageOptions);
        model.addAttribute("mealOptions", mealOptions);
        model.addAttribute("returnFlight", returnFlight);
        model.addAttribute("seats", seats);

        return "extras-selection-return";
    }

    // ✅ NEW: Handle return flight extras selection submission
    @PostMapping("/services/extras-return/submit")
    public String submitReturnExtrasSelection(@RequestParam Map<String, String> formData,
                                             HttpSession session) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        if (bookingSession == null || bookingSession.getPassengers() == null || bookingSession.getPassengers().isEmpty()) {
            return "redirect:/booking/passenger-info";
        }

        // Process each passenger's return flight selections
        for (int i = 0; i < bookingSession.getPassengers().size(); i++) {
            PassengerInfoDTO passenger = bookingSession.getPassengers().get(i);

            // Get meal selection for this passenger
            String mealKey = "passenger_" + i + "_meal_return";
            String mealValue = formData.get(mealKey);
            Integer mealId = null;
            if (mealValue != null && !mealValue.isEmpty()) {
                try {
                    mealId = Integer.parseInt(mealValue);
                } catch (NumberFormatException e) {
                    mealId = null;
                }
            }

            // Get luggage selection for this passenger
            String luggageKey = "passenger_" + i + "_luggage_return";
            String luggageValue = formData.get(luggageKey);
            Integer luggageId = null;
            if (luggageValue != null && !luggageValue.isEmpty()) {
                try {
                    luggageId = Integer.parseInt(luggageValue);
                } catch (NumberFormatException e) {
                    luggageId = null;
                }
            }

            passenger.setReturnMealId(mealId);
            passenger.setReturnLuggageId(luggageId);
        }

        session.setAttribute("bookingSession", bookingSession);

        // ✅ FIX: KHÔNG TẠO BOOKING Ở ĐÂY - chỉ redirect sang payment
        // Booking và ticket sẽ được tạo KHI SUBMIT payment
        System.out.println("\n✅ REDIRECTING TO PAYMENT (Booking will be created on payment confirm)");
        System.out.println("========================================\n");

        return "redirect:/booking/payment-preview";
    }

    /**
     * Helper method to create booking and tickets from session data
     */
    private String createBookingAndTickets(BookingSessionDTO bookingSession) {
        try {
            // Get outbound flight
            FlightsEntity outboundFlight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
            if (outboundFlight == null) {
                System.out.println("Outbound flight not found: " + bookingSession.getFlightId());
                return null;
            }

            // Get return flight if round trip
            FlightsEntity returnFlight = null;
            if (bookingSession.isRoundTrip() && bookingSession.getReturnFlightId() != null) {
                returnFlight = flightsRepository.findById(bookingSession.getReturnFlightId()).orElse(null);
                if (returnFlight == null) {
                    System.out.println("Return flight not found: " + bookingSession.getReturnFlightId());
                    return null;
                }
            }

            // Create a pending payment first (required for booking)
            PaymentEntity payment = new PaymentEntity();
            payment.setPaymentId("PAY-" + RandomId.generateRandomId(2, 4));
            payment.setCreatedAt(LocalDateTime.now());
            payment.setStatus("PENDING");
            payment.setAmount(BigDecimal.ZERO); // Will be updated later

            // Try to get default payment method, or set to null
            try {
                PaymentMethodEntity defaultMethod = paymentMethodService.getAllPaymentMethods()
                    .stream()
                    .findFirst()
                    .orElse(null);
                payment.setPaymentMethod(defaultMethod);
            } catch (Exception e) {
                payment.setPaymentMethod(null);
            }

            PaymentEntity savedPayment = paymentService.createPayment(payment);
            System.out.println("Payment created: " + savedPayment.getPaymentId());

            // Get logged-in user (if any)
            UserEntity currentUser = null;
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String userEmail = auth.getName();
                    currentUser = userService.findByEmail(userEmail);
                    if (currentUser != null) {
                        System.out.println("✅ Found logged-in user: " + currentUser.getFullName());
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not get logged-in user: " + e.getMessage());
                currentUser = null;
            }

            // Create booking
            BookingEntity booking = new BookingEntity();
            booking.setBookingId("BK-" + RandomId.generateRandomId(3, 3));
            booking.setBookingTime(LocalDateTime.now());
            booking.setStatus("PENDING_PAYMENT");
            booking.setUser(currentUser);
            booking.setPayment(savedPayment);
            booking.setTotalAmount(BigDecimal.ZERO);

            // SAVE BOOKING FIRST before creating tickets
            BookingEntity savedBooking = bookingService.create(booking);
            System.out.println("Booking created: " + savedBooking.getBookingId());

            BigDecimal ticketsTotal = BigDecimal.ZERO;

            // Create tickets for outbound flight
            for (PassengerInfoDTO passengerDTO : bookingSession.getPassengers()) {
                BigDecimal outboundTicketPrice = createTicketForFlight(
                    passengerDTO,
                    outboundFlight,
                    savedBooking,
                    bookingSession,
                    passengerDTO.getSeatId(),
                    passengerDTO.getMealId(),
                    passengerDTO.getLuggageId()
                );
                ticketsTotal = ticketsTotal.add(outboundTicketPrice);
            }

            // Create tickets for return flight if round trip
            if (bookingSession.isRoundTrip() && returnFlight != null) {
                System.out.println("✅ Creating return flight tickets");
                for (PassengerInfoDTO passengerDTO : bookingSession.getPassengers()) {
                    BigDecimal returnTicketPrice = createTicketForFlight(
                        passengerDTO,
                        returnFlight,
                        savedBooking,
                        bookingSession,
                        passengerDTO.getReturnSeatId(),
                        passengerDTO.getReturnMealId(),
                        passengerDTO.getReturnLuggageId()
                    );
                    ticketsTotal = ticketsTotal.add(returnTicketPrice);
                }
            }

            // Update booking and payment with total amount
            savedBooking.setTotalAmount(ticketsTotal);
            savedPayment.setAmount(ticketsTotal);

            // Update both
            bookingService.create(savedBooking);
            paymentService.createPayment(savedPayment);

            System.out.println("✅ Booking completed with total: " + ticketsTotal + " VNĐ");
            if (bookingSession.isRoundTrip()) {
                System.out.println("   - Round trip booking with " + bookingSession.getPassengerCount() + " passengers");
                System.out.println("   - Outbound: " + bookingSession.getFlightId());
                System.out.println("   - Return: " + bookingSession.getReturnFlightId());
            }

            return savedBooking.getBookingId();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to create a single ticket for a flight
     */
    private BigDecimal createTicketForFlight(
            PassengerInfoDTO passengerDTO,
            FlightsEntity flight,
            BookingEntity booking,
            BookingSessionDTO bookingSession,
            String seatId,
            Integer mealId,
            Integer luggageId) {

        try {
            TicketEntity ticket = new TicketEntity();
            ticket.setTicketId("TK-" + RandomId.generateRandomId(2, 4));
            ticket.setFlight(flight);
            ticket.setBooking(booking);

            // Set seat if provided
            SeatEntity seat = null;
            if (seatId != null && !seatId.isEmpty()) {
                seat = seatRepository.findById(seatId).orElse(null);
                if (seat != null) {
                    ticket.setSeat(seat);
                }
            }

            // Set passenger info
            ticket.setTitle(passengerDTO.getTitle() != null ? passengerDTO.getTitle() : "Mr");
            ticket.setFirstName(passengerDTO.getFirstName() != null ? passengerDTO.getFirstName() : "Guest");
            ticket.setLastName(passengerDTO.getLastName() != null ? passengerDTO.getLastName() : "User");

            // Set birth date
            if (passengerDTO.getBirthDate() != null) {
                ticket.setBirthDate(passengerDTO.getBirthDate());
            } else {
                try {
                    ticket.setBirthDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));
                } catch (Exception e) {
                    ticket.setBirthDate(new java.util.Date());
                }
            }

            ticket.setGender(passengerDTO.getGender() != null ? passengerDTO.getGender() : false);
            ticket.setCccd(passengerDTO.getCccd() != null ? passengerDTO.getCccd() : "000000000000");
            ticket.setNationality(passengerDTO.getNationality() != null ? passengerDTO.getNationality() : "Vietnam");
            ticket.setSdt(bookingSession.getPhone() != null ? bookingSession.getPhone() : "0000000000");
            ticket.setEmail(bookingSession.getEmail() != null ? bookingSession.getEmail() : "guest@example.com");
            ticket.setStatus("PENDING");

            // Calculate ticket price
            BigDecimal ticketPrice = flight.getBasePrice();

            // Add surcharge for Business class seats
            if (seat != null && "Business".equalsIgnoreCase(seat.getSeatClass())) {
                ticketPrice = ticketPrice.add(new BigDecimal("1800000"));
            }

            // Add meal price if selected
            if (mealId != null) {
                MealEntity meal = mealService.findById(mealId);
                if (meal != null) {
                    ticket.setMeal(meal);
                    ticketPrice = ticketPrice.add(meal.getPrice());
                }
            }

            // Add luggage price if selected
            if (luggageId != null) {
                LuggageEntity luggage = luggageService.findById(luggageId);
                if (luggage != null) {
                    ticket.setLuggage(luggage);
                    ticketPrice = ticketPrice.add(luggage.getPrice());
                }
            }

            ticket.setPrice(ticketPrice);

            // Save ticket
            ticketService.create(ticket);
            System.out.println("Ticket created: " + ticket.getTicketId() + " for flight " + flight.getFlightId() + " - Price: " + ticketPrice);

            return ticketPrice;

        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    // API endpoint to calculate total price
    @PostMapping("/services/calculate-price")
    @ResponseBody
    public BigDecimal calculateTotalPrice(@RequestParam(required = false) String baggageIds,
                                          @RequestParam(required = false) String mealIds,
                                          @RequestParam(required = false) Integer passengers) {
        BigDecimal total = BigDecimal.ZERO;
        int passengerCount = passengers != null ? passengers : 1;

        if (baggageIds != null && !baggageIds.isEmpty()) {
            String[] ids = baggageIds.split(",");
            for (String id : ids) {
                var baggage = additionalServiceService.getBaggageOptionById(id.trim());
                if (baggage != null) {
                    total = total.add(baggage.getPrice());
                }
            }
        }

        if (mealIds != null && !mealIds.isEmpty()) {
            String[] ids = mealIds.split(",");
            for (String id : ids) {
                var meal = additionalServiceService.getMealOptionById(id.trim());
                if (meal != null) {
                    total = total.add(meal.getPrice());
                }
            }
        }

        return total.multiply(new BigDecimal(passengerCount));
    }
}
