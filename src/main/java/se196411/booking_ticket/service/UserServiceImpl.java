package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.UserEntity;
import se196411.booking_ticket.repository.UserRepository;
import se196411.booking_ticket.model.dto.UserDto;
import se196411.booking_ticket.model.enums.Role;
import se196411.booking_ticket.utils.RandomId;

import java.util.List;

@Service // Đánh dấu đây là một Service Bean
public class UserServiceImpl implements UserService {

    // Tiêm (Inject) các Repository và PasswordEncoder
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Sử dụng Constructor Injection
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public void saveUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setUserId(RandomId.generateRandomId(2, 3)); // Tạo ID ngẫu nhiên cho user
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());

        // **QUAN TRỌNG: Mã hóa mật khẩu**
        // Không bao giờ được lưu mật khẩu gốc (plain text)
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Gán vai trò (Role) mặc định cho user mới
        user.setRole(Role.USER);

        // Lưu user vào CSDL
        userRepository.save(user);
    }

    @Override
    public UserEntity createUser(UserEntity newUser) {
        return this.userRepository.save(newUser);
    }

    @Override
    public Object getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
