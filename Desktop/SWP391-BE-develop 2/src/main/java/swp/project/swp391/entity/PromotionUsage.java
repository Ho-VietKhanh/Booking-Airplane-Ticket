package swp.project.swp391.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "promotion_usage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionUsage {

    @EmbeddedId
    private PromotionUsageKey id;

    @ManyToOne
    @MapsId("promotionId")
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private Integer usageCount = 0;
}
