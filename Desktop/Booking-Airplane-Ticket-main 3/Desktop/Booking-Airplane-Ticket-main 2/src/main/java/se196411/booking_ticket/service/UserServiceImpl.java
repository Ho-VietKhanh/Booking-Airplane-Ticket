package se196411.booking_ticket.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import se196411.booking_ticket.model.UserEntity;
    import se196411.booking_ticket.repository.UserRepository;
    import se196411.booking_ticket.service.UserService;

    import java.util.Optional;

    @Service
    public class UserServiceImpl implements UserService {

        @Autowired
        private UserRepository userRepository;

        @Override
        public Optional<UserEntity> findByUsername(String username) {
            return userRepository.findByUsername(username);
        }

        @Override
        public Boolean existsByUsername(String username) {
            return userRepository.existsByUsername(username);
        }

        @Override
        public Boolean existsByEmail(String email) {
            return userRepository.existsByEmail(email);
        }

        @Override
        public UserEntity save(UserEntity user) {
            return userRepository.save(user);
        }
    }