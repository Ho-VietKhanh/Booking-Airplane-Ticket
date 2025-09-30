package swp.project.swp391.response.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {
    private Long id;
    private String email;
    private String role;
    private boolean active;
    private String message;
}
