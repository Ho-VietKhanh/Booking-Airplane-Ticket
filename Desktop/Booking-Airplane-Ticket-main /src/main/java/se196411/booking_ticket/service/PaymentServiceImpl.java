package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se196411.booking_ticket.model.PaymentEntity;
import se196411.booking_ticket.repository.PaymentRepository;
import se196411.booking_ticket.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
        payment.setStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
}