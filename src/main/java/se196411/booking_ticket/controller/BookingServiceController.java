package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.dto.BookingSessionDTO;
import se196411.booking_ticket.model.dto.PassengerInfoDTO;
import se196411.booking_ticket.model.entity.*;
import se196411.booking_ticket.service.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    // API endpoint để lấy danh sách seat ID đã được đặt cho một chuyến bay
    @GetMapping("/api/flights/{flightId}/reserved-seats")
    @ResponseBody
    public Set<String> getReservedSeats(@PathVariable String flightId) {
        return seatService.getReservedSeatIdsByFlightId(flightId);
    }

    // Screen 0: Passenger Information Form
    @GetMapping("/passenger-info")
    public String showPassengerInfoForm(@RequestParam(required = false) String flightId,
                                        Model model) {
        String actualFlightId = flightId != null ? flightId : "FL-001";
        model.addAttribute("flightId", actualFlightId);

        // Load flight entity with full nested information (flightRoute, airports, airplane)
        try {
            se196411.booking_ticket.model.entity.FlightsEntity flight = flightsRepository.findById(actualFlightId).orElse(null);
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
            actualFlightId = bookingSession.getFlightId();
            passengerCount = bookingSession.getPassengerCount();
            // Add bookingSession to model so template can access it
            model.addAttribute("bookingSession", bookingSession);
        } else {
            actualFlightId = flightId != null ? flightId : "FL-001";
            passengerCount = passengers != null ? passengers : 1;
        }

        var seats = seatService.getSeatsByFlightId(actualFlightId);

        model.addAttribute("seats", seats);
        model.addAttribute("flightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);

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
            actualFlightId = bookingSession.getFlightId();
        } else {
            passengerCount = passengers != null ? passengers : 1;
            actualFlightId = flightId != null ? flightId : "FL-001";
        }

        var baggageOptions = additionalServiceService.getAllBaggageOptions();
        var mealOptions = additionalServiceService.getAllMealOptions();

        model.addAttribute("flightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("baggageOptions", baggageOptions);
        model.addAttribute("mealOptions", mealOptions);
        model.addAttribute("selectedSeats", selectedSeats != null ? selectedSeats : "");

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

            // Update seat for this passenger
            bookingSession.getPassengers().get(passengerIndex).setSeatId(seatId);

            // Save back to session
            session.setAttribute("bookingSession", bookingSession);

            System.out.println("✅ Saved seat " + seatId + " for passenger " + passengerIndex + " to session");

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

            // Assign seats to passengers
            for (int i = 0; i < selectedSeats.size() && i < bookingSession.getPassengers().size(); i++) {
                String seatId = selectedSeats.get(i);
                bookingSession.getPassengers().get(i).setSeatId(seatId);
                System.out.println("Assigned seat " + seatId + " to passenger " + i);
            }

            // Save back to session
            session.setAttribute("bookingSession", bookingSession);
            System.out.println("Booking session updated and saved");
        } else {
            System.out.println("ERROR: BookingSession or passengers is null!");
        }

        return "redirect:/booking/services/extras?flightId=" +
               (bookingSession != null ? bookingSession.getFlightId() : "FL-001");
    }

    // Handle extras (meal & luggage) selection submission
    @PostMapping("/services/extras/submit")
    public String submitExtrasSelection(@RequestParam Map<String, String> formData,
                                       HttpSession session) {
        BookingSessionDTO bookingSession = (BookingSessionDTO) session.getAttribute("bookingSession");

        if (bookingSession == null || bookingSession.getPassengers() == null || bookingSession.getPassengers().isEmpty()) {
            return "redirect:/booking/passenger-info";
        }

        // Process each passenger's selections
        for (int i = 0; i < bookingSession.getPassengers().size(); i++) {
            PassengerInfoDTO passenger = bookingSession.getPassengers().get(i);

            // Get meal selection for this passenger
            String mealKey = "passenger_" + i + "_meal";
            String mealValue = formData.get(mealKey);
            if (mealValue != null && !mealValue.isEmpty()) {
                try {
                    passenger.setMealId(Integer.parseInt(mealValue));
                } catch (NumberFormatException e) {
                    passenger.setMealId(null);
                }
            } else {
                passenger.setMealId(null);
            }

            // Get luggage selection for this passenger
            String luggageKey = "passenger_" + i + "_luggage";
            String luggageValue = formData.get(luggageKey);
            if (luggageValue != null && !luggageValue.isEmpty()) {
                try {
                    passenger.setLuggageId(Integer.parseInt(luggageValue));
                } catch (NumberFormatException e) {
                    passenger.setLuggageId(null);
                }
            } else {
                passenger.setLuggageId(null);
            }
        }

        session.setAttribute("bookingSession", bookingSession);

        // Create booking and tickets
        String bookingId = createBookingAndTickets(bookingSession);

        if (bookingId != null) {
            return "redirect:/booking/payment?bookingId=" + bookingId;
        } else {
            return "redirect:/booking/services/extras?error=bookingFailed";
        }
    }

    /**
     * Helper method to create booking and tickets from session data
     */
    private String createBookingAndTickets(BookingSessionDTO bookingSession) {
        try {
            // Get flight
            FlightsEntity flight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
            if (flight == null) {
                System.out.println("Flight not found: " + bookingSession.getFlightId());
                return null;
            }

            // Create a pending payment first (required for booking)
            PaymentEntity payment = new PaymentEntity();
            payment.setPaymentId(UUID.randomUUID().toString());
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

            // ✅ Get logged-in user (if any)
            UserEntity currentUser = null;
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String userEmail = auth.getName();
                    currentUser = userService.findByEmail(userEmail);
                    if (currentUser != null) {
                        System.out.println("✅ Found logged-in user: " + currentUser.getFullName() + " (ID: " + currentUser.getUserId() + ")");
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not get logged-in user: " + e.getMessage());
                currentUser = null;
            }

            // Create booking
            BookingEntity booking = new BookingEntity();
            booking.setBookingId(UUID.randomUUID().toString());
            booking.setBookingTime(LocalDateTime.now());
            booking.setStatus("PENDING_PAYMENT");
            booking.setUser(currentUser); // ✅ Set user if logged in, null if guest
            booking.setPayment(savedPayment); // Link to payment
            booking.setTotalAmount(BigDecimal.ZERO); // Will be updated later

            if (currentUser != null) {
                System.out.println("✅ Booking will be linked to user: " + currentUser.getUserId());
            } else {
                System.out.println("⚠️ Guest booking (no user linked)");
            }

            // ✅ SAVE BOOKING FIRST before creating tickets
            BookingEntity savedBooking = bookingService.create(booking);
            System.out.println("Booking created: " + savedBooking.getBookingId());

            // Now create tickets for each passenger
            BigDecimal ticketsTotal = BigDecimal.ZERO;

            for (PassengerInfoDTO passengerDTO : bookingSession.getPassengers()) {
                TicketEntity ticket = new TicketEntity();
                ticket.setTicketId(UUID.randomUUID().toString());
                ticket.setFlight(flight);
                ticket.setBooking(savedBooking); // ✅ Use savedBooking (already persisted)

                // Set seat
                SeatEntity seat = seatRepository.findById(passengerDTO.getSeatId()).orElse(null);
                if (seat != null) {
                    ticket.setSeat(seat);
                }

                // Set passenger info with null checks
                ticket.setTitle(passengerDTO.getTitle() != null ? passengerDTO.getTitle() : "Mr");
                ticket.setFirstName(passengerDTO.getFirstName() != null ? passengerDTO.getFirstName() : "Guest");
                ticket.setLastName(passengerDTO.getLastName() != null ? passengerDTO.getLastName() : "User");

                // Ensure birthDate is never null
                if (passengerDTO.getBirthDate() != null) {
                    ticket.setBirthDate(passengerDTO.getBirthDate());
                } else {
                    // Set default birth date if null
                    try {
                        ticket.setBirthDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));
                    } catch (Exception e) {
                        ticket.setBirthDate(new java.util.Date());
                    }
                }

                ticket.setGender(passengerDTO.getGender() != null ? passengerDTO.getGender() : false);
                ticket.setCccd(passengerDTO.getCccd() != null ? passengerDTO.getCccd() : "000000000000");
                ticket.setNationality(passengerDTO.getNationality() != null ? passengerDTO.getNationality() : "Vietnam");

                // ✅ Use booking session email and phone for all passengers
                ticket.setSdt(bookingSession.getPhone() != null ? bookingSession.getPhone() : "0000000000");
                ticket.setEmail(bookingSession.getEmail() != null ? bookingSession.getEmail() : "guest@example.com");
                ticket.setStatus("PENDING");

                // Calculate ticket price
                BigDecimal ticketPrice = flight.getBasePrice();

                // Add meal price if selected
                if (passengerDTO.getMealId() != null) {
                    MealEntity meal = mealService.findById(passengerDTO.getMealId());
                    if (meal != null) {
                        ticket.setMeal(meal);
                        ticketPrice = ticketPrice.add(meal.getPrice());
                    }
                }

                // Add luggage price if selected
                if (passengerDTO.getLuggageId() != null) {
                    LuggageEntity luggage = luggageService.findById(passengerDTO.getLuggageId());
                    if (luggage != null) {
                        ticket.setLuggage(luggage);
                        ticketPrice = ticketPrice.add(luggage.getPrice());
                    }
                }

                ticket.setPrice(ticketPrice);
                ticketsTotal = ticketsTotal.add(ticketPrice);

                // Save ticket
                ticketService.create(ticket);
                System.out.println("Ticket created: " + ticket.getTicketId() + " - Price: " + ticketPrice);
            }

            // Update booking and payment with total amount
            savedBooking.setTotalAmount(ticketsTotal);
            savedPayment.setAmount(ticketsTotal);

            // Update both
            bookingService.create(savedBooking);
            paymentService.createPayment(savedPayment);

            System.out.println("Booking updated with total: " + ticketsTotal);

            return savedBooking.getBookingId();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
