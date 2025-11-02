package se196411.booking_ticket.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.dto.UserDto;
import se196411.booking_ticket.model.RoleEntity;
import se196411.booking_ticket.model.UserEntity;
import se196411.booking_ticket.service.UserService;

import java.util.Arrays;

@Service // Đánh dấu đây là một Service Bean
public class UserServiceImpl implements UserService {

    // Tiêm (Inject) các Repository và PasswordEncoder
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Sử dụng Constructor Injection
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());

        // **QUAN TRỌNG: Mã hóa mật khẩu**
        // Không bao giờ được lưu mật khẩu gốc (plain text)
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Gán vai trò (Role) mặc định cho user mới
        RoleEntity role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            // Nếu role "ROLE_USER" chưa có trong CSDL, tạo mới
            role = new RoleEntity("ROLE_USER");
            roleRepository.save(role);
        }

        // Gán role này cho user
        // Dùng Arrays.asList() vì trường 'roles' của UserEntity là List<RoleEntity>
        user.setRoles(Arrays.asList(role));

        // Lưu user vào CSDL
        userRepository.save(user);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}