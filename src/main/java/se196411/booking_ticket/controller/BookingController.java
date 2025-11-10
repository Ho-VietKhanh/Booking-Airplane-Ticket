package se196411.booking_ticket.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import se196411.booking_ticket.model.dto.BookingRequestDTO;
import se196411.booking_ticket.model.dto.BookingResponseDTO;
import se196411.booking_ticket.model.dto.TicketResponseDTO;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.model.entity.UserEntity;
import se196411.booking_ticket.repository.BookingRepository;
import se196411.booking_ticket.repository.TicketRepository;
import se196411.booking_ticket.service.BookingService;
import se196411.booking_ticket.service.TicketService;
import se196411.booking_ticket.service.UserService;
import se196411.booking_ticket.service.PaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping("/list")
    public String listBookings(Model model, @RequestParam(required = false) String search) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<BookingResponseDTO> bookings;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userEmail = auth.getName();
            UserEntity user = userService.findByEmail(userEmail);

            if (user != null) {
                if (search != null && !search.trim().isEmpty()) {
                    bookings = bookingService.searchBookingsByUserId(user.getUserId(), search.trim());
                } else {
                    bookings = bookingService.getAllBookingsByUserId(user.getUserId());
                }
                model.addAttribute("username", user.getFullName());
                model.addAttribute("isAuthenticated", true);
            } else {
                bookings = List.of();
            }
        } else {
            if (search != null && !search.trim().isEmpty()) {
                bookings = bookingService.searchBookings(search.trim());
            } else {
                bookings = List.of();
            }
            model.addAttribute("isAuthenticated", false);
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("search", search);
        return "booking/bookinglist";
    }

    // Endpoint for viewing booking details from booking list (uses bookingdetail_list.html)
    @GetMapping("/detail/{bookingId}")
    public String viewBookingDetails(@PathVariable String bookingId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);

        if (booking == null) {
            return "redirect:/booking/list";
        }

        // Check if authenticated user owns this booking
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userEmail = auth.getName();
            UserEntity user = userService.findByEmail(userEmail);

            if (user != null) {
                if (!booking.getUserId().equals(user.getUserId())) {
                    return "redirect:/booking/list";
                }
                model.addAttribute("username", user.getFullName());
                model.addAttribute("isAuthenticated", true);
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        List<TicketResponseDTO> tickets = ticketService.getAllTicketsByBookingId(bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("tickets", tickets);
        return "booking/bookingdetail_list";
    }

    // New endpoint for viewing booking details from booking history (uses bookingdetail.html)
    @GetMapping("/history/detail/{bookingId}")
    public String viewBookingHistoryDetails(@PathVariable String bookingId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);

        if (booking == null) {
            return "redirect:/booking/history";
        }

        // Check if authenticated user owns this booking
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userEmail = auth.getName();
            UserEntity user = userService.findByEmail(userEmail);

            if (user != null) {
                if (!booking.getUserId().equals(user.getUserId())) {
                    return "redirect:/booking/history";
                }
                model.addAttribute("username", user.getFullName());
                model.addAttribute("isAuthenticated", true);
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        List<TicketResponseDTO> tickets = ticketService.getAllTicketsByBookingId(bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("tickets", tickets);
        return "booking/bookingdetail";
    }

    public List<BookingResponseDTO> searchBookings(String keyword) {
        List<BookingEntity> bookings = new ArrayList<>();

        // Search by booking ID
        Optional<BookingEntity> bookingById = bookingRepository.findById(keyword);
        if (bookingById.isPresent()) {
            bookings.add(bookingById.get());
        }

        // Search by ticket ID
        Optional<TicketEntity> ticket = ticketRepository.findById(keyword);
        if (ticket.isPresent()) {
            BookingEntity bookingByTicket = ticket.get().getBooking();
            if (bookingByTicket != null && !bookings.contains(bookingByTicket)) {
                bookings.add(bookingByTicket);
            }
        }

        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDTO> searchBookingsByUserId(String userId, String keyword) {
        List<BookingEntity> bookings = new ArrayList<>();

        // Search by booking ID for this user
        Optional<BookingEntity> bookingById = bookingRepository.findById(keyword);
        if (bookingById.isPresent() && bookingById.get().getUser().getUserId().equals(userId)) {
            bookings.add(bookingById.get());
        }

        // Search by ticket ID for this user
        Optional<TicketEntity> ticket = ticketRepository.findById(keyword);
        if (ticket.isPresent()) {
            BookingEntity bookingByTicket = ticket.get().getBooking();
            if (bookingByTicket != null && bookingByTicket.getUser().getUserId().equals(userId)
                    && !bookings.contains(bookingByTicket)) {
                bookings.add(bookingByTicket);
            }
        }

        return bookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingResponseDTO convertToDTO(BookingEntity booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setBookingTime(booking.getBookingTime());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        dto.setPaymentId(booking.getPayment() != null ? booking.getPayment().getPaymentId() : null);
        dto.setUserId(booking.getUser().getUserId());
        return dto;
    }

//    @GetMapping("/add")
//    public ModelAndView showAddBookingForm(HttpSession session) {
//        ModelAndView mv = new ModelAndView("booking/addbooking");
//        mv.addObject("bookingRequest", new BookingRequestDTO());
//        mv.addObject("users", userService.getAllUsers());
//        mv.addObject("payments", paymentService.getAllPayments());
//        return mv;
//    }
//
//    @PostMapping("/add")
//    public String addBooking(
//            @Valid @ModelAttribute("bookingRequest") BookingRequestDTO bookingRequestDTO,
//            BindingResult result,
//            Model model
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("users", userService.getAllUsers());
//            model.addAttribute("payments", paymentService.getAllPayments());
//            return "booking/addbooking";
//        }
//
//        bookingService.insertBooking(bookingRequestDTO);
//        return "redirect:/booking/list";
//    }
//
//    @GetMapping("/update/{bookingId}")
//    public ModelAndView showUpdateBookingForm(@PathVariable String bookingId) {
//        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);
//        if (booking == null) {
//            return new ModelAndView("redirect:/booking/list");
//        }
//
//        ModelAndView mv = new ModelAndView("booking/updatebooking");
//        mv.addObject("booking", booking);
//        mv.addObject("users", userService.getAllUsers());
//        mv.addObject("payments", paymentService.getAllPayments());
//        return mv;
//    }
//
//    @PostMapping("/update/{bookingId}")
//    public String updateBooking(
//            @PathVariable String bookingId,
//            @Valid @ModelAttribute("bookingRequest") BookingRequestDTO bookingRequestDTO,
//            BindingResult result,
//            Model model
//    ) {
//        if (result.hasErrors()) {
//            model.addAttribute("users", userService.getAllUsers());
//            model.addAttribute("payments", paymentService.getAllPayments());
//            return "booking/updatebooking";
//        }
//
//        bookingService.updateBookingByBookingId(bookingId, bookingRequestDTO);
//        return "redirect:/booking/list";
//    }
//
//    @GetMapping("/delete/{bookingId}")
//    public String deleteBooking(@PathVariable String bookingId) {
//        BookingResponseDTO booking = bookingService.getBookingByBookingId(bookingId);
//        if (booking == null) {
//            return "redirect:/booking/list";
//        }
//
//        bookingService.deleteBookingByBookingId(bookingId);
//        return "redirect:/booking/list";
//    }
//
//    @GetMapping("/user/{userId}")
//    public ModelAndView getBookingsByUser(@PathVariable String userId) {
//        List<BookingResponseDTO> bookings = bookingService.getAllBookingsByUserId(userId);
//        ModelAndView mv = new ModelAndView("booking/bookinglist");
//        mv.addObject("bookings", bookings);
//        return mv;
//    }

    @GetMapping("/history")
    public String showBookingHistory(Model model) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        // Get user email from authentication
        String userEmail = auth.getName();

        // Find user by email
        UserEntity user = userService.findByEmail(userEmail);

        if (user == null) {
            return "redirect:/dashboard";
        }

        // Get all bookings for this user
        List<BookingResponseDTO> bookings = bookingService.getAllBookingsByUserId(user.getUserId());

        model.addAttribute("bookings", bookings);
        model.addAttribute("username", user.getFullName());
        model.addAttribute("isAuthenticated", true);

        return "booking-history";
    }

    @GetMapping("/ticket/{ticketId}")
    public String viewTicketDetails(@PathVariable String ticketId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TicketResponseDTO ticket = ticketService.getTicketByTicketId(ticketId);

        if (ticket == null) {
            return "redirect:/booking/list";
        }

        // Get the booking to verify ownership
        BookingResponseDTO booking = bookingService.getBookingByBookingId(ticket.getBookingId());

        if (booking == null) {
            return "redirect:/booking/list";
        }

        // Check if authenticated user owns this ticket
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userEmail = auth.getName();
            UserEntity user = userService.findByEmail(userEmail);

            if (user != null) {
                if (!booking.getUserId().equals(user.getUserId())) {
                    return "redirect:/booking/list";
                }
                model.addAttribute("username", user.getFullName());
                model.addAttribute("isAuthenticated", true);
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("booking", booking);
        return "booking/ticketdetail";
    }
}