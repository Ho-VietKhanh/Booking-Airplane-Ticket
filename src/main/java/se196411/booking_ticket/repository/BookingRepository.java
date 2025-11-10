package se196411.booking_ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.BookingEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,String> {
    List<BookingEntity> findAllByUserUserId(String userId);
    List<BookingEntity> findAll();

    List<BookingEntity> findByTicketsTicketIdAndUserUserId(String keyword, String userId);

    List<BookingEntity> findByTicketsTicketId(String keyword);

    Optional<Object> findByBookingId(String keyword);
}
