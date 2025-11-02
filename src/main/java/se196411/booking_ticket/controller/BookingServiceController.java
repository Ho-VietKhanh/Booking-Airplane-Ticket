package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.dto.BookingSessionDTO;
import se196411.booking_ticket.model.dto.PassengerInfoDTO;
import se196411.booking_ticket.service.AdditionalServiceService;
import se196411.booking_ticket.service.SeatService;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/booking")
public class BookingServiceController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private AdditionalServiceService additionalServiceService;

    // Screen 0: Passenger Information Form
    @GetMapping("/passenger-info")
    public String showPassengerInfoForm(@RequestParam(required = false) String flightId,
                                        Model model) {
        model.addAttribute("flightId", flightId != null ? flightId : "FL001");
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
        bookingSession.setFlightId(formData.getOrDefault("flightId", "FL001"));

        // Parse passengers from form data
        int passengerIndex = 1;
        while (formData.containsKey("passenger" + passengerIndex + "_title")) {
            PassengerInfoDTO passenger = new PassengerInfoDTO();
            passenger.setTitle(formData.get("passenger" + passengerIndex + "_title"));
            passenger.setLastName(formData.get("passenger" + passengerIndex + "_lastName"));
            passenger.setFirstName(formData.get("passenger" + passengerIndex + "_firstName"));

            String dobString = formData.get("passenger" + passengerIndex + "_dob");
            if (dobString != null && !dobString.isEmpty()) {
                passenger.setDateOfBirth(LocalDate.parse(dobString));
            }

            passenger.setGender(formData.get("passenger" + passengerIndex + "_gender"));
            passenger.setIdCard(formData.get("passenger" + passengerIndex + "_idCard"));
            passenger.setNationality(formData.get("passenger" + passengerIndex + "_nationality"));

            bookingSession.addPassenger(passenger);
            passengerIndex++;
        }

        // Save to session
        session.setAttribute("bookingSession", bookingSession);

        // Redirect to service selection
        return "redirect:/booking/services/select?flightId=" + bookingSession.getFlightId()
               + "&passengers=" + bookingSession.getPassengerCount();
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
            model.addAttribute("flightId", flightId != null ? flightId : "FL001");
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
        } else {
            actualFlightId = flightId != null ? flightId : "FL001";
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
            actualFlightId = flightId != null ? flightId : "FL001";
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
