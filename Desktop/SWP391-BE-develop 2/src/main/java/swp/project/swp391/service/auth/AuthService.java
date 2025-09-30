package swp.project.swp391.service.auth;

import swp.project.swp391.request.auth.*;
import swp.project.swp391.response.auth.LoginResponse;
import swp.project.swp391.response.auth.RefreshTokenResponse;
import swp.project.swp391.response.auth.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void verifyOtp(String otp);
    void requestNewOtp(String email);
    // Forgot password flow
    void forgotPassword(String email);
    void resendForgotPasswordOtp(String email);
    void resetPassword(ResetPasswordRequest request);

    // Change password khi đã login
    void changePassword(String username, ChangePasswordRequest request);
}
