package swp.project.swp391.response.customer;

import lombok.Builder;
import lombok.Data;
import swp.project.swp391.entity.Customer;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class CustomerProfileResponse {
    private Long customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String occupation;
    private String gender;
    private String incomeLevel;
    private String dateOfBirth;

    public static CustomerProfileResponse fromEntity(Customer customer) {
        return CustomerProfileResponse.builder()
                .customerId(customer.getId())
                .fullName(customer.getUser().getFullName())
                .email(customer.getUser().getEmail())
                .phoneNumber(customer.getUser().getPhoneNumber())
                .address(customer.getUser().getAddress()) // moved sang User
                .occupation(customer.getOccupation())
                .gender(customer.getUser().getGender() != null ? customer.getUser().getGender().name() : null) // moved sang User
                .incomeLevel(customer.getIncomeLevel() != null ? customer.getIncomeLevel().name() : null)
                .dateOfBirth(customer.getUser().getDateOfBirth() != null
                        ? customer.getUser().getDateOfBirth().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        : null)
                .build();
    }
}
