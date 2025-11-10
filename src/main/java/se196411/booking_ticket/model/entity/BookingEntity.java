package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {
    @Id
    @Column(name = "booking_id", nullable = false)
    private String bookingId;
    @Column (name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;
    @Column (name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column (name = "status", nullable = false)
    private String status;

    // Relationships
    @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    @ToString.Exclude
    private PaymentEntity payment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserEntity user;
}