package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.service.AdditionalServiceService;
import se196411.booking_ticket.service.SeatService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/booking/services")
public class BookingServiceController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private AdditionalServiceService additionalServiceService;

    // Screen 1: Service Selection Menu
    @GetMapping("/select")
    public String showServiceSelection(@RequestParam(required = false) String flightId,
                                        @RequestParam(required = false) Integer passengers,
                                        Model model) {
        model.addAttribute("flightId", flightId != null ? flightId : "FL001");
        model.addAttribute("passengerCount", passengers != null ? passengers : 1);
        return "service-selection";
    }

    // Screen 2: Seat Selection
    @GetMapping("/seats")
    public String showSeatSelection(@RequestParam(required = false) String flightId,
                                    @RequestParam(required = false) Integer passengers,
                                    Model model) {
        String actualFlightId = flightId != null ? flightId : "FL001";
        int passengerCount = passengers != null ? passengers : 1;

        var seats = seatService.getSeatsByFlightId(actualFlightId);

        model.addAttribute("seats", seats);
        model.addAttribute("flightId", actualFlightId);
        model.addAttribute("passengerCount", passengerCount);

        return "seat-selection";
    }

    // Screen 3: Baggage and Meal Selection
    @GetMapping("/extras")
    public String showExtrasSelection(@RequestParam(required = false) String flightId,
                                      @RequestParam(required = false) Integer passengers,
                                      @RequestParam(required = false) String selectedSeats,
                                      Model model) {
        int passengerCount = passengers != null ? passengers : 1;

        var baggageOptions = additionalServiceService.getAllBaggageOptions();
        var mealOptions = additionalServiceService.getAllMealOptions();

        model.addAttribute("flightId", flightId != null ? flightId : "FL001");
        model.addAttribute("passengerCount", passengerCount);
        model.addAttribute("baggageOptions", baggageOptions);
        model.addAttribute("mealOptions", mealOptions);
        model.addAttribute("selectedSeats", selectedSeats != null ? selectedSeats : "");

        return "extras-selection";
    }

    // API endpoint to reserve seat
    @PostMapping("/seats/reserve")
    @ResponseBody
    public String reserveSeat(@RequestParam String seatId,
                            @RequestParam(required = false) String bookingId) {
        boolean success = seatService.reserveSeat(seatId, bookingId != null ? bookingId : "TEMP");
        return success ? "success" : "failed";
    }

    // API endpoint to calculate total price
    @PostMapping("/calculate-price")
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
