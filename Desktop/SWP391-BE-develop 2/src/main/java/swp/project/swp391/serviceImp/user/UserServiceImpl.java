    package swp.project.swp391.serviceImp.user;

    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import swp.project.swp391.constant.ErrorHandler;
    import swp.project.swp391.entity.Dealer;
    import swp.project.swp391.entity.Role;
    import swp.project.swp391.entity.User;
    import swp.project.swp391.exception.BaseException;
    import swp.project.swp391.repository.DealerRepository;
    import swp.project.swp391.repository.RoleRepository;
    import swp.project.swp391.repository.UserRepository;
    import swp.project.swp391.request.user.CreateUserRequest;
    import swp.project.swp391.response.ApiResponse;
    import swp.project.swp391.response.user.CreateUserResponse;
    import swp.project.swp391.response.user.UserResponse;
    import swp.project.swp391.security.RbacGuard;
    import swp.project.swp391.service.auth.EmailService;
    import swp.project.swp391.service.user.UserService;

    import java.util.List;
    import java.util.Set;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class UserServiceImpl implements UserService {
        private final UserRepository userRepository;
        private final RbacGuard guard;
        private final RoleRepository roleRepository;
        private final DealerRepository dealerRepository;
        private final EmailService emailService;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public ApiResponse<?> inactiveUser(Long userId) {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            guard.require(guard.has(currentUser, "user.inactive"));

            User target = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(ErrorHandler.USER_NOT_FOUND));

            boolean isAdmin = target.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
            if (isAdmin) {
                throw new BaseException(ErrorHandler.FORBIDDEN);
            }

            target.setIsActive(false);
            userRepository.save(target);

            return ApiResponse.builder()
                    .success(true)
                    .message("User đã bị vô hiệu hóa thành công")
                    .build();
        }

        @Override
        @Transactional
        public ApiResponse<?> reactivateUser(Long userId) {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            guard.require(guard.has(currentUser, "user.reactivate"));

            User target = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(ErrorHandler.USER_NOT_FOUND));

            target.setIsActive(true);
            userRepository.save(target);

            return ApiResponse.builder()
                    .success(true)
                    .message("User đã được kích hoạt lại thành công")
                    .build();
        }

        @Override
        @Transactional
        public ApiResponse<CreateUserResponse> createUser(CreateUserRequest request) {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            guard.require(guard.has(currentUser, "user.create")); // hoặc quyền cụ thể của ADMIN

            Role role = roleRepository.findByName(request.getRole().toUpperCase())
                    .orElseThrow(() -> new BaseException(ErrorHandler.ROLE_NOT_FOUND));

            Dealer dealer = null;
            String roleName = role.getName();

            // ❌ Không cho phép tạo USER qua API này
            if (roleName.equals("USER")) {
                throw new BaseException(ErrorHandler.FORBIDDEN);
            }

            if (roleName.equals("DEALER_MANAGER") || roleName.equals("DEALER_STAFF")) {
                if (request.getDealerId() == null) {
                    throw new BaseException(ErrorHandler.DEALER_REQUIRED);
                }
                dealer = dealerRepository.findById(request.getDealerId())
                        .orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));
            } else if (roleName.equals("EVM_STAFF")) {
                if (request.getDealerId() != null) {
                    throw new BaseException(ErrorHandler.DEALER_NOT_ALLOWED);
                }
            }

            // ✅ Check email trùng
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        throw new BaseException(ErrorHandler.EMAIL_ALREADY_EXISTS);
                    });

            // Generate password tạm
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            User user = User.builder()
                    .username(request.getEmail())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .password(passwordEncoder.encode(tempPassword))
                    .isVerified(true)
                    .isActive(true)
                    .roles(Set.of(role))
                    .dealer(dealer)
                    .build();

            userRepository.save(user);

            // Gửi email HTML
            String emailBody = String.format("""
    <html>
      <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding:20px;">
        <div style="max-width:500px; margin:auto; background:#fff; padding:30px; border-radius:10px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
          <h2 style="color:#2e86de; text-align:center;">Tài khoản mới đã được tạo</h2>
          <p style="text-align:center;">Xin chào <b>%s</b>,</p>
          <p style="color:#555; text-align:center;">
            Tài khoản của bạn đã được khởi tạo trên hệ thống.<br>
            Dưới đây là thông tin đăng nhập:
          </p>
          <div style="background:#f8f9fa; padding:15px; border-radius:8px; margin:20px 0; text-align:center;">
            <p style="margin:5px 0;"><b>Email/Username:</b> %s</p>
            <p style="margin:5px 0;"><b>Mật khẩu tạm:</b> 
              <span style="color:#e74c3c; font-weight:bold; letter-spacing:2px;">%s</span>
            </p>
          </div>
          <p style="text-align:center; color:#555;">
            Vui lòng đăng nhập, đổi mật khẩu và thông tin cá nhân ngay sau lần đăng nhập đầu tiên.
          </p>
          <p style="text-align:center; font-size:12px; color:#aaa; margin-top:30px;">
            © 2025 SWP391 Team
          </p>
        </div>
      </body>
    </html>
    """, request.getFullName(), request.getEmail(), tempPassword);

            emailService.sendEmail(request.getEmail(), "Tài khoản mới", emailBody);

            return ApiResponse.success(
                    "Tạo user thành công",
                    CreateUserResponse.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .role(role.getName())
                            .active(user.getIsActive())
                            .message("Thông tin đăng nhập đã được gửi qua email")
                            .build()
            );
        }
        @Override
        @Transactional
        public ApiResponse<List<UserResponse>> getAllUsers() {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            guard.require(guard.has(currentUser, "user.read.all")); // quyền cho admin

            List<User> users = userRepository.findAll();

            List<UserResponse> response = users.stream()
                    .map(UserResponse::fromEntity)
                    .toList();

            return ApiResponse.success("Danh sách tất cả user", response);
        }

    }
