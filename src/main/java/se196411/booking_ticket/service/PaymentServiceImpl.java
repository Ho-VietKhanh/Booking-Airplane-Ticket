package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.model.entity.PaymentEntity;
import se196411.booking_ticket.model.entity.SeatEntity;
import se196411.booking_ticket.model.entity.TicketEntity;
import se196411.booking_ticket.repository.BookingRepository;
import se196411.booking_ticket.repository.PaymentRepository;
import se196411.booking_ticket.repository.SeatRepository;
import se196411.booking_ticket.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             BookingRepository bookingRepository,
                             TicketRepository ticketRepository,
                             SeatRepository seatRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public PaymentEntity createPayment(PaymentEntity payment) {
        if (payment.getPaymentId() == null || payment.getPaymentId().isBlank()) {
            payment.setPaymentId(UUID.randomUUID().toString());
        }
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<PaymentEntity> getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<PaymentEntity> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public PaymentEntity updatePayment(String paymentId, PaymentEntity updated) {
        PaymentEntity existing = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        existing.setAmount(updated.getAmount());
        existing.setPaymentMethod(updated.getPaymentMethod());
        existing.setStatus(updated.getStatus());
        existing.setPaidAt(updated.getPaidAt());
        // keep createdAt and paymentId unchanged
        return paymentRepository.save(existing);
    }

    @Override
    public void deletePayment(String paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new RuntimeException("Payment not found: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
    }

    @Override
    public PaymentEntity markAsPaid(String paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        // Update payment status
        payment.setStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());
        PaymentEntity savedPayment = paymentRepository.save(payment);

        // ✅ Update associated bookings to CONFIRMED
        if (savedPayment.getBookings() != null && !savedPayment.getBookings().isEmpty()) {
            for (BookingEntity booking : savedPayment.getBookings()) {
                booking.setStatus("CONFIRMED");
                bookingRepository.save(booking);

                // ✅ Update associated tickets to CONFIRMED and mark seats as unavailable
                if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
                    for (TicketEntity ticket : booking.getTickets()) {
                        ticket.setStatus("CONFIRMED");
                        ticketRepository.save(ticket);

                        // ✅ Mark seat as unavailable
                        if (ticket.getSeat() != null) {
                            SeatEntity seat = ticket.getSeat();
                            seat.setAvailable(false);
                            seatRepository.save(seat);
                            System.out.println("Marked seat " + seat.getSeatNumber() + " as unavailable");
                        }
                    }
                }
            }
        }

        return savedPayment;
    }
}