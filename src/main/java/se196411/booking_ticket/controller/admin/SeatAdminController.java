package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.entity.SeatEntity;
import se196411.booking_ticket.repository.SeatRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/seats")
public class SeatAdminController {
    private final SeatRepository seatRepository;

    public SeatAdminController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @GetMapping("/airplane/{airplaneId}")
    public String seatMap(@PathVariable String airplaneId, Model model) {
        List<SeatEntity> seats = seatRepository.findSeatsByAirplaneId(airplaneId);
        model.addAttribute("seats", seats);
        model.addAttribute("airplaneId", airplaneId);
        model.addAttribute("active", "seats");
        return "admin/seat-selection";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("seats", seatRepository.findAll());
        model.addAttribute("active", "seats");
        return "admin/seats"; // optional list page
    }
}
