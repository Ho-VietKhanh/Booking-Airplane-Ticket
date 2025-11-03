package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se196411.booking_ticket.model.entity.PaymentMethodEntity;
import se196411.booking_ticket.repository.PaymentMethodRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public PaymentMethodEntity createPaymentMethod(PaymentMethodEntity paymentMethod) {
        if (paymentMethod.getPaymentMethodId() == null || paymentMethod.getPaymentMethodId().isBlank()) {
            paymentMethod.setPaymentMethodId(UUID.randomUUID().toString());
        }
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public Optional<PaymentMethodEntity> getPaymentMethodById(String paymentMethodId) {
        return paymentMethodRepository.findById(paymentMethodId);
    }

    @Override
    public List<PaymentMethodEntity> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public PaymentMethodEntity updatePaymentMethod(String paymentMethodId, PaymentMethodEntity updated) {
        PaymentMethodEntity existing = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("PaymentMethod not found: " + paymentMethodId));

        existing.setPaymentMethodName(updated.getPaymentMethodName());
        existing.setDescription(updated.getDescription());
        return paymentMethodRepository.save(existing);
    }

    @Override
    public void deletePaymentMethod(String paymentMethodId) {
        if (!paymentMethodRepository.existsById(paymentMethodId)) {
            throw new RuntimeException("PaymentMethod not found: " + paymentMethodId);
        }
        paymentMethodRepository.deleteById(paymentMethodId);
    }

    @Override
    public Optional<PaymentMethodEntity> findByName(String name) {
        return paymentMethodRepository.findByPaymentMethodName(name);
    }
}
