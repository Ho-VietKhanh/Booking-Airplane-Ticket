package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> findAll();

    UserEntity createUser(UserEntity newUser);
}
