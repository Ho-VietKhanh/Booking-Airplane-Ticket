package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.model.entity.PaymentEntity;
import se196411.booking_ticket.model.entity.PaymentMethodEntity;
import se196411.booking_ticket.service.BookingService;
import se196411.booking_ticket.service.PaymentMethodService;
import se196411.booking_ticket.service.PaymentService;
import se196411.booking_ticket.service.BookingCreationService;
import se196411.booking_ticket.utils.RandomId;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingCreationService bookingCreationService;

    @GetMapping("/booking/payment")
    private String showPaymentPage(
            @RequestParam(name = "bookingId", required = false) String bookingId,
            Model model, RedirectAttributes redirectAttributes) {
        if(bookingId == null || bookingId.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Missing booking Id parameter");
            return "redirect:/";
        }
        Optional<BookingEntity> bookingOpt = this.bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found");
            return "payment-error";
        }

        BookingEntity booking = bookingOpt.get();
        List<PaymentMethodEntity> methods = this.paymentMethodService.getAllPaymentMethods();

        model.addAttribute("booking", booking);
        model.addAttribute("amount", booking.getTotalAmount());
        model.addAttribute("paymentMethods", methods);
        return "payment";
    }

    @PostMapping("/booking/payment/confirm")
    private String confirmPayment(
            @RequestParam(name = "bookingId", required = false) String bookingId,
            @RequestParam("paymentMethodId") String paymentMethodId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // ✅ FIX: Tạo booking từ session nếu chưa có bookingId
        if (bookingId == null || bookingId.isBlank()) {
            // Get booking session from session
            se196411.booking_ticket.model.dto.BookingSessionDTO bookingSession =
                (se196411.booking_ticket.model.dto.BookingSessionDTO) session.getAttribute("bookingSession");

            if (bookingSession == null || bookingSession.getPassengers() == null || bookingSession.getPassengers().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No booking session found");
                return "redirect:/";
            }

            // Create booking and tickets from session
            bookingId = createBookingFromSession(bookingSession, paymentMethodId, session);

            if (bookingId == null) {
                redirectAttributes.addFlashAttribute("error", "Failed to create booking");
                return "redirect:/booking/payment-preview";
            }
        }

        // Get the created booking
        Optional<BookingEntity> bookingOpt = this.bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/";
        }

        BookingEntity booking = bookingOpt.get();

        // Get or update payment
        PaymentEntity payment = booking.getPayment();
        if (payment == null) {
            redirectAttributes.addFlashAttribute("error", "Payment not found");
            return "redirect:/booking/payment-preview";
        }

        // Update payment method if different
        Optional<PaymentMethodEntity> pmOpt = this.paymentMethodService.getPaymentMethodById(paymentMethodId);
        if (pmOpt.isPresent()) {
            payment.setPaymentMethod(pmOpt.get());
            this.paymentService.createPayment(payment);
        }

        redirectAttributes.addAttribute("paymentId", payment.getPaymentId());
        return "redirect:/booking/payment/redirect";
    }

    /**
     * ✅ NEW: Helper method to create booking from session
     */
    private String createBookingFromSession(
            se196411.booking_ticket.model.dto.BookingSessionDTO bookingSession,
            String paymentMethodId,
            HttpSession session) {
        try {
            System.out.println("🔄 Delegating to BookingCreationService...");

            // Use BookingCreationService to create booking from session
            String bookingId = bookingCreationService.createBookingFromSession(bookingSession, paymentMethodId);

            if (bookingId != null) {
                // Clear session after successful booking creation
                session.removeAttribute("bookingSession");
                System.out.println("✅ Booking created successfully: " + bookingId);
            } else {
                System.err.println("❌ Failed to create booking from session");
            }

            return bookingId;

        } catch (Exception e) {
            System.err.println("❌ Error creating booking from session: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/booking/payment/redirect")
    public String redirectToProvider(
            @RequestParam("paymentId") String paymentId, Model model) {
        model.addAttribute("paymentId", paymentId);
        return "payment-redirect";
    }

    @GetMapping("/booking/payment/complete")
    public String completePayment(
            @RequestParam("paymentId") String paymentId, Model model) {
        Optional<PaymentEntity> pOpt = this.paymentService.getPaymentById(paymentId);
        if (pOpt.isEmpty()) {
            model.addAttribute("error", "Payment not found");
            return "payment-error";
        }

        PaymentEntity paid = paymentService.markAsPaid(paymentId);

        model.addAttribute("payment", paid);
        model.addAttribute("booking", paid != null ? paid.getBookings().isEmpty() ? null : paid.getBookings().get(0) : null);
        return "payment-success";
    }
}
