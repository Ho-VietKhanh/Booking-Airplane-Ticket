package se196411.booking_ticket.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ai lam cai nay thi add them field nha, tui moi lam truoc cai relationship voi bang tui a`

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;
}