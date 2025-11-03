package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Tìm một RoleEntity bằng tên của nó (ví dụ: "ROLE_USER").
     * Spring Data JPA sẽ tự động tạo câu truy vấn từ tên phương thức này.
     */
    RoleEntity findByName(String name);

}
