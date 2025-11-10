package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "cccd", nullable = false)
    private String cccd;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "sdt", nullable = false)
    private String sdt;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "nationality", nullable = false)
    private String nationality;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "gender", nullable = false)
    private Boolean gender;

    @Column(name = "birth_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "status", nullable = false)
    private String status;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    @ToString.Exclude
    private FlightsEntity flight;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    @ToString.Exclude
    private BookingEntity booking;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    @ToString.Exclude
    private SeatEntity seat;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    @ToString.Exclude
    private MealEntity meal;

    @ManyToOne
    @JoinColumn(name = "luggage_id")
    @ToString.Exclude
    private LuggageEntity luggage;
}
