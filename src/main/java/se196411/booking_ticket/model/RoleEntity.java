package se196411.booking_ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles") // Tên bảng trong CSDL
public class RoleEntity {

    /**
     * Dùng ID kiểu Long và để CSDL tự động tăng (IDENTITY)
     * sẽ đơn giản và hiệu quả hơn là tự tạo ID ngẫu nhiên.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên của role, BẮT BUỘC phải có tiền tố "ROLE_"
     * Ví dụ: "ROLE_USER", "ROLE_ADMIN"
     * Spring Security sẽ tự động nhận diện tiền tố này.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserEntity> users;

    /**
     * Constructor này hữu ích để tạo nhanh đối tượng Role
     * ví dụ: new RoleEntity("ROLE_USER")
     */
    public RoleEntity(String name) {
        this.name = name;
    }
}