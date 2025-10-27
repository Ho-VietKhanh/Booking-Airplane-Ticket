// src/main/java/se196411/booking_ticket/service/PaymentService.java
package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.PaymentEntity;

import java.util.List;
import java.util.Optional;

@Service
public interface PaymentService {
    public PaymentEntity createPayment(PaymentEntity payment);
    public Optional<PaymentEntity> getPaymentById(String paymentId);
    public List<PaymentEntity> getAllPayments();
    public PaymentEntity updatePayment(String paymentId, PaymentEntity updated);
    public void deletePayment(String paymentId);
    public PaymentEntity markAsPaid(String paymentId);
}