package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.entity.SeatEntity;
import se196411.booking_ticket.repository.SeatRepository;
import se196411.booking_ticket.repository.TicketRepository;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.model.entity.FlightsEntity;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Controller
@RequestMapping("/admin/seats")
public class SeatAdminController {
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final FlightsRepository flightsRepository;

    public SeatAdminController(SeatRepository seatRepository, TicketRepository ticketRepository, FlightsRepository flightsRepository) {
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.flightsRepository = flightsRepository;
    }

    @GetMapping("/airplane/{airplaneId}")
    public String seatMap(@PathVariable String airplaneId, Model model) {
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneId(airplaneId);
        model.addAttribute("seats", seats);
        model.addAttribute("airplaneId", airplaneId);
        model.addAttribute("active", "seats");

        // quick stats: total / empty / booked / held / maintenance
        long total = seats.size();
        long booked = seats.stream().filter(s -> s.getTickets() != null && !s.getTickets().isEmpty()
                && s.getTickets().get(0).getStatus() != null && !s.getTickets().get(0).getStatus().equalsIgnoreCase("HELD")).count();
        long held = seats.stream().filter(s -> s.getTickets() != null && !s.getTickets().isEmpty()
                && s.getTickets().get(0).getStatus() != null && s.getTickets().get(0).getStatus().equalsIgnoreCase("HELD")).count();
        long maintenance = seats.stream().filter(s -> !s.isAvailable()).count();
        long empty = total - booked - held - maintenance;

        model.addAttribute("totalSeats", total);
        model.addAttribute("emptySeats", Math.max(0, empty));
        model.addAttribute("bookedSeats", booked);
        model.addAttribute("heldSeats", held);
        model.addAttribute("maintenanceSeats", maintenance);

        return "admin/seat-selection";
    }

    // Endpoint mới: xem ghế theo flight ID - sử dụng template riêng
    @GetMapping("/flight/{flightId}")
    public String seatMapByFlight(@PathVariable String flightId, Model model) {
        FlightsEntity flight = flightsRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return "redirect:/admin/flights";
        }

        String airplaneId = flight.getAirplane().getAirplaneId();
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneId(airplaneId);

        // Lấy danh sách seat ID đã được đặt CHỈ cho chuyến bay này
        List<String> reservedSeatIds = ticketRepository.findReservedSeatIdsByFlightId(flightId);
        Set<String> reservedSeatSet = new HashSet<>(reservedSeatIds);

        model.addAttribute("seats", seats);
        model.addAttribute("reservedSeatIds", reservedSeatSet);
        model.addAttribute("flightId", flightId);
        model.addAttribute("airplaneId", airplaneId);
        model.addAttribute("flight", flight);
        model.addAttribute("active", "seats");

        // Thống kê ghế cho chuyến bay này
        long total = seats.size();
        long booked = reservedSeatIds.size();
        long available = total - booked;

        model.addAttribute("totalSeats", total);
        model.addAttribute("emptySeats", available);
        model.addAttribute("bookedSeats", booked);

        // Sử dụng template riêng cho xem theo flight
        return "admin/seat-selection-flight";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("seats", seatRepository.findAll());
        model.addAttribute("active", "seats");
        return "admin/seats"; // optional list page
    }
}
