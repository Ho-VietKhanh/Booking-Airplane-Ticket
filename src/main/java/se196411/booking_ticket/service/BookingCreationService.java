package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.dto.BookingSessionDTO;
import se196411.booking_ticket.model.dto.PassengerInfoDTO;
import se196411.booking_ticket.model.entity.*;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.repository.SeatRepository;
import se196411.booking_ticket.utils.RandomId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service để tạo booking và tickets từ session data
 * ✅ FIX: Booking chỉ được tạo KHI SUBMIT PAYMENT, không phải khi chọn dịch vụ
 */
@Service
public class BookingCreationService {

    @Autowired
    private FlightsRepository flightsRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private MealService mealService;

    @Autowired
    private LuggageService luggageService;

    @Autowired
    private UserService userService;

    /**
     * Tạo booking và tickets từ booking session
     * @param bookingSession Session chứa thông tin booking
     * @param paymentMethodId ID của phương thức thanh toán
     * @return Booking ID nếu thành công, null nếu thất bại
     */
    public String createBookingFromSession(BookingSessionDTO bookingSession, String paymentMethodId) {
        try {
            System.out.println("\n🔄 CREATING BOOKING FROM SESSION...");

            // Get outbound flight
            FlightsEntity outboundFlight = flightsRepository.findById(bookingSession.getFlightId()).orElse(null);
            if (outboundFlight == null) {
                System.out.println("❌ Outbound flight not found: " + bookingSession.getFlightId());
                return null;
            }

            // Get return flight if round trip
            FlightsEntity returnFlight = null;
            if (bookingSession.isRoundTrip() && bookingSession.getReturnFlightId() != null) {
                returnFlight = flightsRepository.findById(bookingSession.getReturnFlightId()).orElse(null);
                if (returnFlight == null) {
                    System.out.println("❌ Return flight not found: " + bookingSession.getReturnFlightId());
                    return null;
                }
            }

            // Get payment method
            PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodById(paymentMethodId).orElse(null);
            if (paymentMethod == null) {
                System.out.println("⚠️ Payment method not found, using default");
                paymentMethod = paymentMethodService.getAllPaymentMethods().stream().findFirst().orElse(null);
            }

            // Create payment
            PaymentEntity payment = new PaymentEntity();
            payment.setPaymentId("PAY-" + RandomId.generateRandomId(2, 4));
            payment.setCreatedAt(LocalDateTime.now());
            payment.setStatus("PENDING");
            payment.setAmount(BigDecimal.ZERO); // Will be updated later
            payment.setPaymentMethod(paymentMethod);
            PaymentEntity savedPayment = paymentService.createPayment(payment);

            // Get logged-in user
            UserEntity currentUser = null;
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String userEmail = auth.getName();
                    currentUser = userService.findByEmail(userEmail);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not get logged-in user: " + e.getMessage());
            }

            // Create booking
            BookingEntity booking = new BookingEntity();
            booking.setBookingId("BK-" + RandomId.generateRandomId(3, 3));
            booking.setBookingTime(LocalDateTime.now());
            booking.setStatus("PENDING_PAYMENT");
            booking.setUser(currentUser);
            booking.setPayment(savedPayment);
            booking.setTotalAmount(BigDecimal.ZERO);

            BookingEntity savedBooking = bookingService.create(booking);
            System.out.println("✅ Booking created: " + savedBooking.getBookingId());

            BigDecimal ticketsTotal = BigDecimal.ZERO;

            // Create tickets for outbound flight
            for (PassengerInfoDTO passengerDTO : bookingSession.getPassengers()) {
                BigDecimal ticketPrice = createTicketForFlight(
                    passengerDTO,
                    outboundFlight,
                    savedBooking,
                    bookingSession,
                    passengerDTO.getSeatId(),
                    passengerDTO.getMealId(),
                    passengerDTO.getLuggageId()
                );
                ticketsTotal = ticketsTotal.add(ticketPrice);
            }

            // Create tickets for return flight if round trip
            if (bookingSession.isRoundTrip() && returnFlight != null) {
                System.out.println("✅ Creating return flight tickets");
                for (PassengerInfoDTO passengerDTO : bookingSession.getPassengers()) {
                    BigDecimal ticketPrice = createTicketForFlight(
                        passengerDTO,
                        returnFlight,
                        savedBooking,
                        bookingSession,
                        passengerDTO.getReturnSeatId(),
                        passengerDTO.getReturnMealId(),
                        passengerDTO.getReturnLuggageId()
                    );
                    ticketsTotal = ticketsTotal.add(ticketPrice);
                }
            }

            // Update booking and payment with total amount
            savedBooking.setTotalAmount(ticketsTotal);
            savedPayment.setAmount(ticketsTotal);
            bookingService.create(savedBooking);
            paymentService.createPayment(savedPayment);

            System.out.println("✅ Booking completed successfully!");
            System.out.println("   - Booking ID: " + savedBooking.getBookingId());
            System.out.println("   - Total amount: " + ticketsTotal + " VNĐ");
            System.out.println("   - Round trip: " + bookingSession.isRoundTrip());

            return savedBooking.getBookingId();

        } catch (Exception e) {
            System.err.println("❌ Error creating booking from session:");
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

            // Set seat
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
            ticket.setBirthDate(passengerDTO.getBirthDate() != null ? passengerDTO.getBirthDate() : new java.util.Date());
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

            // Add meal price
            if (mealId != null) {
                MealEntity meal = mealService.findById(mealId);
                if (meal != null) {
                    ticket.setMeal(meal);
                    ticketPrice = ticketPrice.add(meal.getPrice());
                }
            }

            // Add luggage price
            if (luggageId != null) {
                LuggageEntity luggage = luggageService.findById(luggageId);
                if (luggage != null) {
                    ticket.setLuggage(luggage);
                    ticketPrice = ticketPrice.add(luggage.getPrice());
                }
            }

            ticket.setPrice(ticketPrice);
            ticketService.create(ticket);

            System.out.println("   ✓ Ticket created: " + ticket.getTicketId() + " - " + ticketPrice + " VNĐ");

            return ticketPrice;

        } catch (Exception e) {
            System.err.println("❌ Error creating ticket: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}

