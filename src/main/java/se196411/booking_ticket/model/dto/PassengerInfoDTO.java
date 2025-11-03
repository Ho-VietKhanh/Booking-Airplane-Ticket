package se196411.booking_ticket.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerInfoDTO {
    // Personal Information (from form_booking)
    private String title;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Boolean gender;
    private String cccd;
    private String nationality;
    private String sdt;
    private String email;

    // Booking details (selected in other pages)
    private String seatId;
    private Integer mealId;
    private Integer luggageId;

    // Price calculation
    private BigDecimal price = BigDecimal.ZERO;

    // Status
    private String status = "PENDING";
}

