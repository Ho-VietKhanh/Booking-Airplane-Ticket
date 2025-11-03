package se196411.booking_ticket.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se196411.booking_ticket.model.dto.BookingRequestDTO;
import se196411.booking_ticket.model.dto.BookingResponseDTO;
import se196411.booking_ticket.model.dto.TicketResponseDTO;
import se196411.booking_ticket.service.BookingService;
import se196411.booking_ticket.service.TicketService;
import se196411.booking_ticket.service.UserService;
import se196411.booking_ticket.service.PaymentService;

import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    // ===================== LIST ALL BOOKINGS =====================
    @GetMapping("/list")
    public ModelAndView listBookings(HttpSession session) {
        // Add authentication check if needed
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        ModelAndView mv = new ModelAndView("booking/bookinglist");
        mv.addObject("bookings", bookings);
        return mv;
    }

    // ===================== VIEW BOOKING DETAILS =====================
    @GetMapping("/detail/{bookingId}")
    public ModelAndView viewBookingDetails(@PathVariable String bookingId) {
        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);
        if (booking == null) {
            return new ModelAndView("redirect:/booking/list");
        }

        List<TicketResponseDTO> tickets = ticketService.getAllTicketsByBookingId(bookingId);
        ModelAndView mv = new ModelAndView("booking/bookingdetail");
        mv.addObject("booking", booking);
        mv.addObject("tickets", tickets);
        return mv;
    }

    // ===================== ADD BOOKING =====================
    @GetMapping("/add")
    public ModelAndView showAddBookingForm(HttpSession session) {
        ModelAndView mv = new ModelAndView("booking/addbooking");
        mv.addObject("bookingRequest", new BookingRequestDTO());
        mv.addObject("users", userService.getAllUsers());
        mv.addObject("payments", paymentService.getAllPayments());
        return mv;
    }

    @PostMapping("/add")
    public String addBooking(
            @Valid @ModelAttribute("bookingRequest") BookingRequestDTO bookingRequestDTO,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("payments", paymentService.getAllPayments());
            return "booking/addbooking";
        }

        bookingService.insertBooking(bookingRequestDTO);
        return "redirect:/booking/list";
    }

    // ===================== UPDATE BOOKING =====================
    @GetMapping("/update/{bookingId}")
    public ModelAndView showUpdateBookingForm(@PathVariable String bookingId) {
        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);
        if (booking == null) {
            return new ModelAndView("redirect:/booking/list");
        }

        ModelAndView mv = new ModelAndView("booking/updatebooking");
        mv.addObject("booking", booking);
        mv.addObject("users", userService.getAllUsers());
        mv.addObject("payments", paymentService.getAllPayments());
        return mv;
    }

    @PostMapping("/update/{bookingId}")
    public String updateBooking(
            @PathVariable String bookingId,
            @Valid @ModelAttribute("bookingRequest") BookingRequestDTO bookingRequestDTO,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("payments", paymentService.getAllPayments());
            return "booking/updatebooking";
        }

        bookingService.updateBookingByBookingId(bookingId, bookingRequestDTO);
        return "redirect:/booking/list";
    }

    // ===================== DELETE BOOKING =====================
    @GetMapping("/delete/{bookingId}")
    public String deleteBooking(@PathVariable String bookingId) {
        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);
        if (booking == null) {
            return "redirect:/booking/list";
        }

        bookingService.deleteBookingByBookingId(bookingId);
        return "redirect:/booking/list";
    }

    // ===================== GET BOOKINGS BY USER =====================
    @GetMapping("/user/{userId}")
    public ModelAndView getBookingsByUser(@PathVariable String userId) {
        List<BookingResponseDTO> bookings = bookingService.getAllBookingsByUserId(userId);
        ModelAndView mv = new ModelAndView("booking/bookinglist");
        mv.addObject("bookings", bookings);
        return mv;
    }
}