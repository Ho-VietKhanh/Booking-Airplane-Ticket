package swp.project.swp391.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Role không được để trống (EVM_STAFF / DEALER_MANAGER / DEALER_STAFF)")
    private String role;

    // Nếu tạo DEALER_MANAGER hoặc DEALER_STAFF thì bắt buộc có dealerId
    private Long dealerId;
}
