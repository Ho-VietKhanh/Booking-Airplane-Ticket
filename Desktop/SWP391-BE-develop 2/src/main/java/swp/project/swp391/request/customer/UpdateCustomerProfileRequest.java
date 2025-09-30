package swp.project.swp391.request.customer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCustomerProfileRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải có từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10 hoặc 11 chữ số")
    private String phoneNumber;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Size(max = 100, message = "Nghề nghiệp không được vượt quá 100 ký tự")
    private String occupation;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;  // MALE/FEMALE

    @NotBlank(message = "Mức thu nhập không được để trống")
    private String incomeLevel;  // HIGH/MEDIUM/LOW

    // yyyy-MM-dd, có thể regex kiểm tra định dạng nếu muốn chặt hơn
    @NotBlank(message = "Ngày sinh không được để trống")
    private String dateOfBirth;
}
