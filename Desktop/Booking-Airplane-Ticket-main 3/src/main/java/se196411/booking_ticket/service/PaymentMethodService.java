package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.PaymentMethodEntity;

import java.util.List;
import java.util.Optional;

@Service
public interface PaymentMethodService {
    public PaymentMethodEntity createPaymentMethod(PaymentMethodEntity paymentMethod);
    public Optional<PaymentMethodEntity> getPaymentMethodById(String paymentMethodId);
    public List<PaymentMethodEntity> getAllPaymentMethods();
    public PaymentMethodEntity updatePaymentMethod(String paymentMethodId, PaymentMethodEntity updated);
    public void deletePaymentMethod(String paymentMethodId);
    public Optional<PaymentMethodEntity> findByName(String name);
}