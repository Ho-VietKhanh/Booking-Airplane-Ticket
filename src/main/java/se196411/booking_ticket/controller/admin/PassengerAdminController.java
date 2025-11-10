package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String list(@RequestParam(value = "q", required = false) String searchKeyword, Model model) {
        List<TicketEntity> tickets = null;

        // If search keyword is provided, use search method
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            tickets = ticketRepository.searchTickets(searchKeyword.trim());
            model.addAttribute("searchKeyword", searchKeyword);
        } else {
            // Use findAllWithDetails() to fetch all relationships eagerly
            tickets = ticketRepository.findAllWithDetails();
        }

        // Sort tickets by booking time descending (newest first)
        if (tickets != null) {
            tickets.sort((t1, t2) -> {
                if (t1.getBooking() != null && t2.getBooking() != null &&
                    t1.getBooking().getBookingTime() != null && t2.getBooking().getBookingTime() != null) {
                    return t2.getBooking().getBookingTime().compareTo(t1.getBooking().getBookingTime());
                }
                return 0;
            });
        }

        // Count tickets by status (add null check)
        long bookedCount = 0;
        long heldCount = 0;
        long cancelledCount = 0;

        if (tickets != null) {
            bookedCount = tickets.stream()
                    .filter(t -> "BOOKED".equals(t.getStatus()) || "CONFIRMED".equals(t.getStatus()))
                    .count();
            heldCount = tickets.stream()
                    .filter(t -> "HELD".equals(t.getStatus()))
                    .count();
            cancelledCount = tickets.stream()
                    .filter(t -> "CANCELLED".equals(t.getStatus()))
                    .count();
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("bookedCount", bookedCount);
        model.addAttribute("heldCount", heldCount);
        model.addAttribute("cancelledCount", cancelledCount);
        model.addAttribute("active", "passengers");

        return "admin/passengers";
    }
}
