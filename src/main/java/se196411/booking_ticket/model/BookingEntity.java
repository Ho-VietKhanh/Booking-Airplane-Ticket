package se196411.booking_ticket.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.Data;
import lombok.NoArgsConstructor;

=======
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

>>>>>>> 3777ba8d4c7c7782e6b1ea27df896c42032ffa3b
@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
<<<<<<< HEAD
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ai lam cai nay thi add them field nha, tui moi lam truoc cai relationship voi bang tui a`
=======
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
>>>>>>> 3777ba8d4c7c7782e6b1ea27df896c42032ffa3b

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;
<<<<<<< HEAD
=======

    //@ManyToOne
    //@JoinColumn(name = "user_id", nullable = false)
    //private UsersEntity user;
>>>>>>> 3777ba8d4c7c7782e6b1ea27df896c42032ffa3b
}