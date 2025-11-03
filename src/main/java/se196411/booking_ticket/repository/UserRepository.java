package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Tìm một UserEntity bằng email.
     * Phương thức này RẤT QUAN TRỌNG, được dùng cho cả:
     * 1. Logic Đăng ký (kiểm tra email đã tồn tại chưa).
     * 2. Logic Đăng nhập (Spring Security dùng để tìm user).
     */
    UserEntity findByEmail(String email);
}
