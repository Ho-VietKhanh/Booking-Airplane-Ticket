package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id", nullable = false)
    private Integer mealId;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "note")
    private String note;

    // Relationships
    @OneToMany(mappedBy = "meal")
    @ToString.Exclude
    private List<TicketEntity> tickets = new ArrayList<>();
}