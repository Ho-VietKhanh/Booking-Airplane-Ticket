package se196411.booking_ticket.service;

import se196411.booking_ticket.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserEntity> findAll();

    UserEntity createUser(UserEntity newUser);
}
