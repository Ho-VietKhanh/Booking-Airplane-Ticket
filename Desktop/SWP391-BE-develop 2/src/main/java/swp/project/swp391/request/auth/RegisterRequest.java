package swp.project.swp391.request.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp.project.swp391.validator.PasswordMatches;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PasswordMatches(message = "Mật khẩu nhập lại không khớp")
public class RegisterRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 12, message = "Tên đăng nhập phải có từ 3 đến 12 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Mật khẩu nhập lại không được để trống")
    private String confirmPassword;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải có từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10 hoặc 11 chữ số")
    private String phoneNumber;

    @Email(message = "Email phải đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @Pattern(regexp = "^[0-9]*$", message = "Số CMND/CCCD chỉ được chứa chữ số")
    @Size(max = 20, message = "Số CMND/CCCD không được vượt quá 20 ký tự")
    private String idNumber;

    // Sử dụng String để nhận dữ liệu từ request, sau đó chuyển đổi
    private String dateOfBirth;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @Size(max = 100, message = "Nghề nghiệp không được vượt quá 100 ký tự")
    private String occupation;

    @NotBlank(message = "Mức thu nhập không được để trống")
    private String incomeLevel;

}