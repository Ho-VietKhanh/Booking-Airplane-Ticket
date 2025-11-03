package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.AirportsEntity;

@Repository
public interface AirportsRepository extends JpaRepository<AirportsEntity, String> {
    // Tìm theo tên sân bay (contains - không phân biệt hoa thường)
    AirportsEntity findByNameContainingIgnoreCase(String airportName); // Đoạn này để tìm theo tên sân bay (contains - không phân biệt hoa thường)

    // Tìm theo thành phố (contains - không phân biệt hoa thường)
    AirportsEntity findByPlaceContainingIgnoreCase(String place);

}
