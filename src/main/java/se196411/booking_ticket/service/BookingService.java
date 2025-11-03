package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.BookingEntity;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingEntity create(BookingEntity booking);
    Optional<BookingEntity> findById(String bookingId);
    List<BookingEntity> findAll();
    BookingEntity update(String bookingId, BookingEntity booking);
    void deleteById(String bookingId);
}
