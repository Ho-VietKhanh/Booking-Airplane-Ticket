package se196411.booking_ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    @Column (name = "ticket_id", nullable = false)
    private String ticketId;
    @Column (name = "price", nullable = false)
    private BigDecimal price;
    @Column (name = "status", nullable = false)
    private String status;

    // Relationships
    @ManyToOne
    @JoinColumn (name = "flight_id", nullable = false)
    private FlightsEntity flight;

    @ManyToOne
    @JoinColumn (name = "booking", nullable = false)
    private BookingEntity booking;

    //@ManyToOne
    //@Column (name = "seat", nullable = false)
    //private SeatEntity seat;
}
