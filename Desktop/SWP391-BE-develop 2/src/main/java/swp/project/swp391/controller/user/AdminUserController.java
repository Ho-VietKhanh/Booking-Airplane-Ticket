package swp.project.swp391.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.project.swp391.repository.RoleRepository;
import swp.project.swp391.request.user.CreateUserRequest;
import swp.project.swp391.response.ApiResponse;
import swp.project.swp391.response.role.RoleResponse;
import swp.project.swp391.response.user.CreateUserResponse;
import swp.project.swp391.response.user.UserResponse;
import swp.project.swp391.service.role.RoleService;
import swp.project.swp391.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;
    private final RoleService roleService;

    @PutMapping("/{id}/inactive")
    public ResponseEntity<ApiResponse<?>> inactiveUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.inactiveUser(id));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<ApiResponse<?>> reactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.reactivateUser(id));
    }
    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/roles-get-all")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
