package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.UserEntity;
import se196411.booking_ticket.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public UserEntity createUser(UserEntity newUser) {
        return this.userRepository.save(newUser);
    }
}
