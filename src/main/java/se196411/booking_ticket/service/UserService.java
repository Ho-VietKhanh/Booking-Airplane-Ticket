package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.UserDto;
import se196411.booking_ticket.model.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> findAll();

    /**
     * Xử lý logic đăng ký user mới
     * @param userDto Dữ liệu từ form register
     */
    void saveUser(UserDto userDto);

    /**
     * Tìm user bằng email
     * @param email Email của user
     * @return UserEntity hoặc null nếu không tìm thấy
     */
    UserEntity findByEmail(String email);
    UserEntity createUser(UserEntity newUser);

    Object getAllUsers();
}
