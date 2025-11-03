package se196411.booking_ticket.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Tự động tạo getter, setter, toString...
@NoArgsConstructor // Tự động tạo constructor rỗng
@AllArgsConstructor // Tự động tạo constructor có đủ tham số
public class UserDto {

    // Không cần id, vì đây là DTO để tạo user mới

    @NotEmpty(message = "Họ tên không được để trống")
    private String fullName;

    @NotEmpty(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotEmpty(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phone;

    // Chúng ta không cần các trường khác như createAt, roles... ở đây
    // Đây là dữ liệu CHỈ dành cho form đăng ký.
}