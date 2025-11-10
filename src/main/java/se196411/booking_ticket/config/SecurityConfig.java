package se196411.booking_ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import se196411.booking_ticket.security.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration // Đánh dấu đây là file Cấu hình
@EnableWebSecurity // Bật tính năng Spring Security
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

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
                .authorizeHttpRequests(auth -> auth
                        // Cho phép TẤT CẢ MỌI NGƯỜI truy cập các URL này (không cần đăng nhập)
                        .requestMatchers("/register/**", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        // Cho phép truy cập trang dashboard và booking mà không cần đăng nhập
                        .requestMatchers("/", "/dashboard", "/dashboard/**", "/booking/**", "/api/**").permitAll()
                        // Chỉ ADMIN mới được truy cập /admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Bất kỳ request nào khác đều YÊU CẦU PHẢI ĐĂNG NHẬP
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
                                .successHandler(customAuthenticationSuccessHandler)

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
                )

                // Cấu hình trang truy cập bị từ chối
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        // Xây dựng và trả về cấu hình
        return http.build();
    }

    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}