package se196411.booking_ticket.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import se196411.booking_ticket.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true) // Thêm unique = true
    private String email; // Dùng email làm username để login

    @Column(name = "password", nullable = false)
    private String password; // Mật khẩu sẽ được mã hóa

    @Column(name = "phone") // Bỏ nullable = false để cho phép null
    private String phone;

    @Column(name = "create_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createAt;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RoleEntity role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BookingEntity> bookings;

    @PrePersist
    public void prePersist() {
        if (this.createAt == null) {
            this.createAt = DateTimeUtils.now();
        }
    }
}