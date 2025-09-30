package swp.project.swp391.response.user;

import lombok.Builder;
import lombok.Data;
import swp.project.swp391.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private Boolean active;
    private Boolean verified;
    private Set<String> roles;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .active(user.getIsActive())
                .verified(user.getIsVerified())
                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                .build();
    }
}
