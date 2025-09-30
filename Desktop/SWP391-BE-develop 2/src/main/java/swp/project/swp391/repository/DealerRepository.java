package swp.project.swp391.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.project.swp391.entity.Dealer;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

    // Kiểm tra xem Dealer đã tồn tại với email hay chưa
    boolean existsByEmail(String email);

    // Kiểm tra xem Dealer đã tồn tại với số điện thoại hay chưa
    boolean existsByPhoneNumber(String phoneNumber);

    // Tìm các dealer có trạng thái hoạt động (isActive)
    List<Dealer> findByIsActive(Boolean isActive);

    List<Dealer> findAll();

    Optional<Dealer> findByIdAndIsActive(Long id, Boolean isActive);
}
