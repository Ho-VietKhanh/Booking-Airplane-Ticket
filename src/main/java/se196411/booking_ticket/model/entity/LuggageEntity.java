package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "luggage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuggageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "luggage_id", nullable = false)
    private Integer luggageId;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "luggage_allowance", nullable = false)
    private Double luggageAllowance;

    @Column(name = "note")
    private String note;

    // Relationships
    @OneToMany(mappedBy = "luggage")
    private List<TicketEntity> tickets = new ArrayList<>();
}


