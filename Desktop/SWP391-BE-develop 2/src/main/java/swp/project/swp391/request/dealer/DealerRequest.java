package swp.project.swp391.request.dealer;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DealerRequest {

    @NotBlank(message = "Tên đại lý không được để trống")
    @Size(min = 3, max = 255, message = "Tên đại lý phải có từ 3 đến 255 ký tự")
    private String name;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10 hoặc 11 chữ số")
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Khu vực không được để trống")
    @Pattern(regexp = "^(NORTH|CENTRAL|SOUTH)$", message = "Khu vực phải là NORTH, CENTRAL hoặc SOUTH")
    private String region; // NORTH, CENTRAL, SOUTH
}
