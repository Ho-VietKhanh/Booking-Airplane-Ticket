package se196411.booking_ticket.service;

import se196411.booking_ticket.model.BookingEntity;
import se196411.booking_ticket.model.dto.BookingRequestDTO;
import se196411.booking_ticket.model.dto.BookingResponseDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingEntity create(BookingEntity booking);
    BookingEntity findById(String bookingId);
    List<BookingEntity> findAll();
    BookingEntity update(String bookingId, BookingEntity booking);
    void deleteById(String bookingId);
    public void insertBooking(BookingRequestDTO bookingRequestDTO);

    public BookingResponseDTO getBookingByBookingId(String bookingId);

    public void updateBookingByBookingId(String bookingId, BookingRequestDTO bookingRequestDTO);

    public void deleteBookingByBookingId(String bookingId);

    public List<BookingResponseDTO> getAllBookingsByUserId(String userId);

    public List<BookingResponseDTO> getAllBookings();
}
