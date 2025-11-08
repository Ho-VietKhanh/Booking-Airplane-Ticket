package se196411.booking_ticket.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se196411.booking_ticket.model.entity.AirPlaneEntity;
import se196411.booking_ticket.repository.AirplaneRepository;
import se196411.booking_ticket.repository.FlightsRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/airplanes")
public class AirplaneAdminController {
    private final AirplaneRepository airplaneRepository;
    private final FlightsRepository flightsRepository;

    public AirplaneAdminController(AirplaneRepository airplaneRepository, FlightsRepository flightsRepository) {
        this.airplaneRepository = airplaneRepository;
        this.flightsRepository = flightsRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<AirPlaneEntity> planes = airplaneRepository.findAll();
        model.addAttribute("airplanes", planes);
        model.addAttribute("active", "airplanes");
        return "admin/airplanes";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("airplane", new AirPlaneEntity());
        model.addAttribute("active", "airplanes");
        return "admin/airplane-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AirPlaneEntity airplane) {
        airplaneRepository.save(airplane);
        return "redirect:/admin/airplanes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            // Check if airplane has associated flights
            AirPlaneEntity airplane = airplaneRepository.findById(id).orElse(null);
            if (airplane == null) {
                redirectAttributes.addFlashAttribute("error", "Airplane not found");
                return "redirect:/admin/airplanes";
            }

            // Check if there are flights using this airplane
            if (airplane.getFlights() != null && !airplane.getFlights().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete airplane: it has associated flights. Delete the flights first.");
                return "redirect:/admin/airplanes";
            }

            // Check if there are seats associated
            if (airplane.getSeats() != null && !airplane.getSeats().isEmpty()) {
                redirectAttributes.addFlashAttribute("warning", "Deleting airplane will also delete all its seats.");
            }

            airplaneRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Airplane deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete airplane: " + e.getMessage());
        }
        return "redirect:/admin/airplanes";
    }
}
