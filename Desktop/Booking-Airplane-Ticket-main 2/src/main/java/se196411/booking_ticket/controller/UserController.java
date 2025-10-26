package se196411.booking_ticket.controller;

                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.http.ResponseEntity;
                    import org.springframework.security.crypto.password.PasswordEncoder;
                    import org.springframework.web.bind.annotation.*;
                    import se196411.booking_ticket.model.RoleEntity;
                    import se196411.booking_ticket.model.UserEntity;
                    import se196411.booking_ticket.dto.MessageResponse;
                    import se196411.booking_ticket.dto.SignupRequest;
                    import se196411.booking_ticket.service.RoleService;
                    import se196411.booking_ticket.service.UserService;

                    import java.time.LocalDateTime;

                    @RestController
                    @RequestMapping("/api/auth")
                    public class UserController {

                        @Autowired
                        private UserService userService;

                        @Autowired
                        private RoleService roleService;

                        @Autowired
                        private PasswordEncoder passwordEncoder;

                        @PostMapping("/signup")
                        public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
                            if (userService.existsByEmail(signUpRequest.getEmail())) {
                                return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
                            }

                            // Tạo tài khoản người dùng mới
                            UserEntity user = new UserEntity();
                            user.setFullName(signUpRequest.getUsername());
                            user.setEmail(signUpRequest.getEmail());
                            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
                            user.setPhone(signUpRequest.getPhone()); // Dòng này bây giờ sẽ hoạt động
                            user.setCreateAt(LocalDateTime.now());

                            // Tìm và gán vai trò mặc định cho người dùng
                            RoleEntity userRole = roleService.findByRoleName("ROLE_USER")
                                    .orElseThrow(() -> new RuntimeException("Error: Default role 'ROLE_USER' is not found."));
                            user.setRole(userRole);

                            userService.save(user);

                            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
                        }
                    }