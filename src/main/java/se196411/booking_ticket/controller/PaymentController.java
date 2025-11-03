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
import se196411.booking_ticket.utils.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private BookingService bookingService;

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
            @RequestParam("bookingId") String bookingId,
            @RequestParam("paymentMethodId") String paymentMethodId,
            RedirectAttributes redirectAttributes) {
        Optional<BookingEntity> bookingOpt = this.bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/";
        }

        BookingEntity booking = bookingOpt.get();

        Optional<PaymentMethodEntity> pmOpt = this.paymentMethodService.getPaymentMethodById(paymentMethodId);
        if (pmOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Payment method not found");
            return "redirect:/booking/payment?bookingId=" + bookingId;
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setCreatedAt(DateTimeUtils.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus("PENDING");
        payment.setAmount(booking.getTotalAmount() != null ? booking.getTotalAmount() : BigDecimal.ZERO);
        payment.setPaymentMethod(pmOpt.get());

        PaymentEntity saved = this.paymentService.createPayment(payment);

        booking.setPayment(saved);
        this.bookingService.create(booking);

        redirectAttributes.addAttribute("paymentId", saved.getPaymentId());
        return "redirect:/booking/payment/redirect";
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
