package se196411.booking_ticket.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se196411.booking_ticket.model.dto.UserDto;
import se196411.booking_ticket.model.UserEntity;
import se196411.booking_ticket.service.UserService;

@Controller // Đánh dấu đây là một Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Xử lý request GET /login
     * Trả về trang login.html
     */
    @GetMapping("/login")
    public String showLoginForm() {
        // Trả về tên của file view (login.html)
        return "login";
    }

    /**
     * Xử lý request GET /register
     * Trả về trang register.html
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Tạo một đối tượng UserDto rỗng
        UserDto userDto = new UserDto();

        // Thêm đối tượng này vào Model với tên là "user"
        // Form Thymeleaf sẽ sử dụng "user" này để binding dữ liệu
        model.addAttribute("user", userDto);

        // Trả về tên của file view (register.html)
        return "register";
    }

    /**
     * Xử lý request POST /register/save
     * Nhận dữ liệu từ form đăng ký
     */
    @PostMapping("/register/save")
    public String registration(
            // @Valid: Kích hoạt validation (NotEmpty, Email...) cho UserDto
            // @ModelAttribute("user"): Lấy đối tượng "user" từ form
            @Valid @ModelAttribute("user") UserDto userDto,
            // BindingResult: Giữ kết quả của validation
            BindingResult bindingResult,
            // Model: Dùng để gửi thông báo lỗi/thành công về view
            Model model) {

        // 1. Kiểm tra email đã tồn tại chưa
        UserEntity existingUser = userService.findByEmail(userDto.getEmail());
        if (existingUser != null) {
            // Thêm lỗi vào BindingResult
            bindingResult.rejectValue("email", "email.exists", "Email này đã được sử dụng");
        }

        // 2. Kiểm tra nếu có lỗi validation (từ @Valid hoặc lỗi email.exists)
        if (bindingResult.hasErrors()) {
            // Nếu có lỗi, GỬI LẠI form đăng ký
            // model.addAttribute("user", userDto); // Dữ liệu cũ sẽ được giữ lại
            return "register"; // Trả về lại trang register.html
        }

        // 3. Nếu không có lỗi, lưu user mới
        userService.saveUser(userDto);

        // 4. Trả về thông báo đăng ký thành công
        // "successMessage" sẽ được dùng trong register.html
        model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");

        // Tạo lại 1 userDto rỗng cho form, tránh submit lại
        model.addAttribute("user", new UserDto());

        return "register"; // Vẫn ở lại trang register để hiển thị thông báo
        // Hoặc bạn có thể chuyển hướng về trang login:
        // return "redirect:/login?register_success";
    }

    /**
     * Trang test sau khi login thành công
     * (URL này đã được cấu hình trong SecurityConfig -> defaultSuccessUrl)
     */
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Trả về file dashboard.html
    }

    /**
     * Trang chủ
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index"; // Trả về file index.html
    }
}