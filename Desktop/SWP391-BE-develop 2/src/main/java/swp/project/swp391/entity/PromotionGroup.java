package swp.project.swp391.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "PromotionGroups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Group code is required")
    @Column(unique = true, nullable = false, length = 50)
    private String groupCode;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "group")
    private List<Promotion> promotions;

}
