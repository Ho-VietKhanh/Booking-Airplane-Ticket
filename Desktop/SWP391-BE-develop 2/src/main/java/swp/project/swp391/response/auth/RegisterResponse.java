package swp.project.swp391.response.auth;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Thêm @Builder vào đây
public class RegisterResponse {

    private boolean success;
    private String message;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime registrationTime;

}