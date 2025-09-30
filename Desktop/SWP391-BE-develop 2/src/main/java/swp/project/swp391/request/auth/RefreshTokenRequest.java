package swp.project.swp391.request.auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}