package se196411.booking_ticket.model.dto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se196411.booking_ticket.model.entity.FlightsEntity;
import se196411.booking_ticket.model.entity.AirPlaneEntity;
import se196411.booking_ticket.model.entity.FlightRoutesEntity;
import se196411.booking_ticket.repository.AirplaneRepository;
import se196411.booking_ticket.repository.FlightRoutesRepository;
import se196411.booking_ticket.service.admin.FlightAdminService;
import se196411.booking_ticket.utils.RandomId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/flights")
public class FlightAdminController {
    private final FlightAdminService flightAdminService;
    private final AirplaneRepository airplaneRepository;
    private final FlightRoutesRepository flightRoutesRepository;

    public FlightAdminController(FlightAdminService flightAdminService,
                                 AirplaneRepository airplaneRepository,
                                 FlightRoutesRepository flightRoutesRepository) {
        this.flightAdminService = flightAdminService;
        this.airplaneRepository = airplaneRepository;
        this.flightRoutesRepository = flightRoutesRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       Model model) {
        LocalDateTime fromDt = null, toDt = null;
        try {
            if (from != null && !from.isBlank()) {
                fromDt = LocalDate.parse(from).atStartOfDay();
            }
            if (to != null && !to.isBlank()) {
                toDt = LocalDate.parse(to).atTime(LocalTime.MAX);
            }
        } catch (Exception ignored) {
        }

        List<FlightsEntity> flights = flightAdminService.search(q, fromDt, toDt);
        model.addAttribute("flights", flights);
        model.addAttribute("active", "flights");
        model.addAttribute("q", q);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "admin/flights";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") String id, Model model) {
        FlightsEntity flight = flightAdminService.findById(id);
        if (flight == null) {
            return "redirect:/admin/flights";
        }

        // Calculate seat statistics
        int totalSeats = 0;
        int availableSeats = 0;
        int bookedSeats = 0;

        if (flight.getAirplane() != null) {
            totalSeats = flight.getAirplane().getCapacity();
            if (flight.getAirplane().getSeats() != null) {
                for (var seat : flight.getAirplane().getSeats()) {
                    if (seat.isAvailable()) {
                        availableSeats++;
                    } else {
                        bookedSeats++;
                    }
                }
            }
        }

        model.addAttribute("flight", flight);
        model.addAttribute("totalSeats", totalSeats);
        model.addAttribute("availableSeats", availableSeats);
        model.addAttribute("bookedSeats", bookedSeats);
        model.addAttribute("active", "flights");
        return "admin/flight-detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        flightAdminService.deleteById(id);
        return "redirect:/admin/flights";
    }

    @GetMapping("/create")
    public String createForm(@RequestParam(required = false) String edit, Model model) {
        FlightsEntity flight;
        if (edit != null) {
            flight = flightAdminService.findById(edit);
            if (flight == null) {
                flight = new FlightsEntity();
            }
        } else {
            flight = new FlightsEntity();
        }

        // Load available airplanes and routes for dropdown
        model.addAttribute("flight", flight);
        model.addAttribute("airplanes", airplaneRepository.findAll());
        model.addAttribute("routes", flightRoutesRepository.findAll());
        model.addAttribute("active", "flights");
        return "admin/flight-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) String flightId,
                      @RequestParam String airplaneId,
                      @RequestParam String flightRouteId,
                      @RequestParam String basePrice,
                      @RequestParam String startedTime,
                      @RequestParam String endedTime,
                      @RequestParam String status,
                      RedirectAttributes redirectAttributes) {
        try {
            FlightsEntity flight;

            // If flightId is provided (edit mode), find existing flight
            if (flightId != null && !flightId.trim().isEmpty()) {
                flight = flightAdminService.findById(flightId);
                if (flight == null) {
                    redirectAttributes.addFlashAttribute("error", "Flight not found");
                    return "redirect:/admin/flights";
                }
            } else {
                // Create new flight with auto-generated ID
                flight = new FlightsEntity();
                String newFlightId = "FL-" + RandomId.generateRandomId(0, 3);
                flight.setFlightId(newFlightId);
            }

            // Set airplane
            AirPlaneEntity airplane = airplaneRepository.findById(airplaneId).orElse(null);
            if (airplane == null) {
                redirectAttributes.addFlashAttribute("error", "Airplane not found");
                return "redirect:/admin/flights/create";
            }
            flight.setAirplane(airplane);

            // Set flight route
            FlightRoutesEntity route = flightRoutesRepository.findById(flightRouteId).orElse(null);
            if (route == null) {
                redirectAttributes.addFlashAttribute("error", "Flight route not found");
                return "redirect:/admin/flights/create";
            }
            flight.setFlightRoute(route);

            flight.setBasePrice(new java.math.BigDecimal(basePrice));
            flight.setStartedTime(LocalDateTime.parse(startedTime));
            flight.setEndedTime(LocalDateTime.parse(endedTime));
            flight.setStatus(status);

            flightAdminService.save(flight);
            redirectAttributes.addFlashAttribute("success", "Flight saved successfully! Flight ID: " + flight.getFlightId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error saving flight: " + e.getMessage());
        }
        return "redirect:/admin/flights";
    }
}
