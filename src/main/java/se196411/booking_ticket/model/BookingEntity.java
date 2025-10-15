package se196411.booking_ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column (name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;
    @Column (name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column (name = "status", nullable = false)
    private String status;

    // Relationships
    @OneToMany(mappedBy = "booking")
    private List<TicketEntity> tickets;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    //@ManyToOne
    //@JoinColumn(name = "user_id", nullable = false)
    //private UsersEntity user;
}