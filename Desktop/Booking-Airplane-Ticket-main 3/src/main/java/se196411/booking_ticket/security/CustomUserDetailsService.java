package se196411.booking_ticket.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.UserEntity;

import java.util.Collection;
import java.util.stream.Collectors;

@Service // Đánh dấu đây là một Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Phương thức này được Spring Security tự động gọi khi user submit form login.
     * "username" ở đây chính là email mà user đã nhập.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm UserEntity trong CSDL bằng email
        UserEntity user = userRepository.findByEmail(email);

        if (user != null) {
            // 2. Nếu tìm thấy, chuyển đổi UserEntity thành UserDetails
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(), // Username (là email)
                    user.getPassword(), // Mật khẩu đã được mã hóa trong DB
                    mapRolesToAuthorities(user.getRoles()) // Danh sách các quyền (roles)
            );
        } else {
            // 3. Nếu không tìm thấy, ném exception
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    /**
     * Hàm helper để chuyển đổi List<RoleEntity> của bạn
     * thành Collection<GrantedAuthority> mà Spring Security yêu cầu.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<RoleEntity> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}