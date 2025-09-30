package swp.project.swp391.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionUsageKey implements Serializable {
    private Integer promotionId;
    private Long customerId;
}
