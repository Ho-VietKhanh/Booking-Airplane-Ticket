package swp.project.swp391.service.user;

import swp.project.swp391.request.user.CreateUserRequest;
import swp.project.swp391.response.ApiResponse;
import swp.project.swp391.response.user.CreateUserResponse;
import swp.project.swp391.response.user.UserResponse;

import java.util.List;

public interface UserService {
    ApiResponse<CreateUserResponse> createUser(CreateUserRequest request);
    ApiResponse<?> inactiveUser(Long userId);
    ApiResponse<?> reactivateUser(Long userId);
    ApiResponse<List<UserResponse>> getAllUsers();

}


