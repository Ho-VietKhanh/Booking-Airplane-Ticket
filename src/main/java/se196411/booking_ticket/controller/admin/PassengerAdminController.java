package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se196411.booking_ticket.repository.TicketRepository;

@Controller
@RequestMapping("/admin/passengers")
public class PassengerAdminController {
    private final TicketRepository ticketRepository;

    public PassengerAdminController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        model.addAttribute("active", "passengers");
        return "admin/passengers";
    }
}

