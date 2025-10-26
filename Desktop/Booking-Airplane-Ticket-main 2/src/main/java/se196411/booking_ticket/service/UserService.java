package se196411.booking_ticket.service;

import se196411.booking_ticket.model.UserEntity;
import java.util.Optional;

public interface UserService {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    UserEntity save(UserEntity user);
}