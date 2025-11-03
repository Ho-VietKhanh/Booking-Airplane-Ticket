package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.BookingEntity;

import java.util.List;

public interface BookingService {
    BookingEntity create(BookingEntity booking);
    BookingEntity findById(String bookingId);
    List<BookingEntity> findAll();
    BookingEntity update(String bookingId, BookingEntity booking);
    void deleteById(String bookingId);
}
