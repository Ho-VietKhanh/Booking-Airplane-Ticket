package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se196411.booking_ticket.repository.BookingRepository;
import se196411.booking_ticket.repository.FlightsRepository;
import se196411.booking_ticket.repository.PaymentRepository;
import se196411.booking_ticket.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final FlightsRepository flightsRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardController(FlightsRepository flightsRepository,
                                    BookingRepository bookingRepository,
                                    UserRepository userRepository,
                                    PaymentRepository paymentRepository) {
        this.flightsRepository = flightsRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping({"", "/"})
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long flightsCount = flightsRepository.count();
        long bookingsCount = bookingRepository.count();
        long usersCount = userRepository.count();
        long paymentsCount = paymentRepository.count();

        model.addAttribute("flightsCount", flightsCount);
        model.addAttribute("bookingsCount", bookingsCount);
        model.addAttribute("usersCount", usersCount);
        model.addAttribute("paymentsCount", paymentsCount);

        return "admin/dashboard";
    }
}

