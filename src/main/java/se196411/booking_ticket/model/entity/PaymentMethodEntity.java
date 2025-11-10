package se196411.booking_ticket.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment_method")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodEntity {
    @Id
    @Column(name = "payment_method_id", nullable = false, unique = true, length = 36)
    private String paymentMethodId;

    @NotBlank
    @Size(min = 2, max = 50, message = "Payment method name must be between 2 and 50 characters")
    @Column(name = "payment_method_name", nullable = false, length = 50)
    private String paymentMethodName;

    @Size(max = 255, message = "Description must be at most 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    // Relationships
    @OneToMany(mappedBy = "paymentMethod")
    @ToString.Exclude
    private List<PaymentEntity> payments = new ArrayList<>();
}