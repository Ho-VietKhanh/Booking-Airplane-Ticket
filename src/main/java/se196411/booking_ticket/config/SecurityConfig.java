package se196411.booking_ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Đánh dấu đây là file Cấu hình
@EnableWebSecurity // Bật tính năng Spring Security
public class SecurityConfig {

    /**
     * Bean này dùng để mã hóa mật khẩu.
     * Spring sẽ tự động "tiêm" (inject) Bean này vào UserService
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình chuỗi lọc bảo mật (Security Filter Chain)
     * Đây là nơi định nghĩa các quy tắc truy cập
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF (Cross-Site Request Forgery) để đơn giản hóa
                // (Khi deploy thực tế nên bật và xử lý nó)
                .csrf(csrf -> csrf.disable())

                // Bắt đầu cấu hình phân quyền truy cập
                .authorizeHttpRequests((authorize) ->
                        authorize
                                // Cho phép TẤT CẢ MỌI NGƯỜI truy cập các URL này
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/").permitAll() // Trang chủ
                                // Cho phép truy cập các file tĩnh (css, js, images)
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                                // Bất kỳ request nào khác (ngoài những cái ở trên)
                                // đều YÊU CẦU PHẢI ĐĂNG NHẬP
                                .anyRequest().authenticated()
                )

                // Cấu hình Form Đăng nhập
                .formLogin(
                        form -> form
                                // URL của trang login (trang mà user thấy)
                                .loginPage("/login")

                                // URL mà Spring Security sẽ xử lý (form action)
                                .loginProcessingUrl("/login")

                                // URL điều hướng sau khi login thành công
                                .defaultSuccessUrl("/dashboard", true)

                                // URL điều hướng nếu login thất bại
                                .failureUrl("/login?error=true")

                                // Cho phép mọi người truy cập trang login
                                .permitAll()
                )

                // Cấu hình Đăng xuất
                .logout(
                        logout -> logout
                                // Dùng .logoutUrl() thay thế, đơn giản và đúng chuẩn mới
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout=true")
                                .permitAll()
                );

        // Xây dựng và trả về cấu hình
        return http.build();
    }
}