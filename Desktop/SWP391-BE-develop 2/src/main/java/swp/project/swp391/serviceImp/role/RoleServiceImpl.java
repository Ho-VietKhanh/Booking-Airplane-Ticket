package swp.project.swp391.serviceImp.role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import swp.project.swp391.entity.User;
import swp.project.swp391.repository.RoleRepository;
import swp.project.swp391.response.role.RoleResponse;
import swp.project.swp391.security.RbacGuard;
import swp.project.swp391.service.role.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RbacGuard guard;

    private User me() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        User currentUser = me();
        guard.require(guard.has(currentUser, "role.read.all")); // chỉ admin có quyền

        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .toList();
    }
}
