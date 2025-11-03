package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.model.dto.BookingResponseDTO;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,String> {
    public List<BookingResponseDTO> findAllByUserUserId(String userId);
    public List<BookingEntity> findAll();
}
