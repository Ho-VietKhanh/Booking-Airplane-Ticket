package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealOptionDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private String description;
    private String imageUrl;
}

