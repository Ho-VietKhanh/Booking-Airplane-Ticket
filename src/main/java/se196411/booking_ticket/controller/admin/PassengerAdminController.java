package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.repository.TicketRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/passengers")
public class PassengerAdminController {
    private final TicketRepository ticketRepository;

    public PassengerAdminController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public String list(Model model) {
        // Use findAllWithDetails() to fetch all relationships eagerly
        List<TicketEntity> tickets = ticketRepository.findAllWithDetails();

        // Count tickets by status
        long bookedCount = tickets.stream()
                .filter(t -> "BOOKED".equals(t.getStatus()))
                .count();
        long heldCount = tickets.stream()
                .filter(t -> "HELD".equals(t.getStatus()))
                .count();
        long cancelledCount = tickets.stream()
                .filter(t -> "CANCELLED".equals(t.getStatus()))
                .count();

        model.addAttribute("tickets", tickets);
        model.addAttribute("bookedCount", bookedCount);
        model.addAttribute("heldCount", heldCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("active", "passengers");

        return "admin/passengers";
    }
}
