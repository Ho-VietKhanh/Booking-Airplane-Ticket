package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.repository.BookingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;


    @Override
    public BookingEntity create(BookingEntity booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<BookingEntity> findById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public List<BookingEntity> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public BookingEntity update(String bookingId, BookingEntity booking) {
        Optional<BookingEntity> existing = bookingRepository.findById(bookingId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        BookingEntity e = existing.get();
        // update fields - keep id
        e.setBookingTime(booking.getBookingTime());
        e.setTotalAmount(booking.getTotalAmount());
        e.setStatus(booking.getStatus());
        e.setPayment(booking.getPayment());
        e.setUser(booking.getUser());
        e.setTickets(booking.getTickets());
        return bookingRepository.save(e);
    }

    @Override
    public void deleteById(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}
