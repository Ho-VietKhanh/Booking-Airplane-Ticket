package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.BookingEntity;
import se196411.booking_ticket.model.dto.BookingRequestDTO;
import se196411.booking_ticket.model.dto.BookingResponseDTO;
import se196411.booking_ticket.model.entity.PaymentEntity;
import se196411.booking_ticket.model.entity.UserEntity;
import se196411.booking_ticket.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void insertBooking(BookingRequestDTO bookingRequestDTO) {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setBookingId(bookingRequestDTO.getBookingId());
        bookingEntity.setBookingTime(LocalDateTime.now()); // Changed this line
        bookingEntity.setTotalAmount(bookingRequestDTO.getTotalAmount());
        bookingEntity.setStatus(bookingRequestDTO.getStatus());

        // Set payment relationship
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentId(bookingRequestDTO.getPaymentId());
        bookingEntity.setPayment(payment);

        // Set user relationship
        UserEntity user = new UserEntity();
        user.setUserId(bookingRequestDTO.getUserId());
        bookingEntity.setUser(user);

        bookingRepository.save(bookingEntity);
    }

    @Override
    public BookingResponseDTO getBookingByBookingId(String bookingId) {
        Optional<BookingEntity> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            return null;
        }

        BookingEntity booking = bookingOptional.get();
        BookingResponseDTO responseDTO = new BookingResponseDTO();
        responseDTO.setBookingId(booking.getBookingId());
        responseDTO.setBookingTime(booking.getBookingTime());
        responseDTO.setTotalAmount(booking.getTotalAmount());
        responseDTO.setStatus(booking.getStatus());
        responseDTO.setPaymentId(booking.getPayment().getPaymentId());
        responseDTO.setUserId(booking.getUser().getUserId());

        return responseDTO;
    }

    @Override
    public void updateBookingByBookingId(String bookingId, BookingRequestDTO bookingRequestDTO) {
        Optional<BookingEntity> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        BookingEntity booking = bookingOptional.get();
        booking.setBookingTime(bookingRequestDTO.getBookingTime());
        booking.setTotalAmount(bookingRequestDTO.getTotalAmount());
        booking.setStatus(bookingRequestDTO.getStatus());

        // Update payment relationship
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentId(bookingRequestDTO.getPaymentId());
        booking.setPayment(payment);

        // Update user relationship
        UserEntity user = new UserEntity();
        user.setUserId(bookingRequestDTO.getUserId());
        booking.setUser(user);

        bookingRepository.save(booking);
    }

    @Override
    public void deleteBookingByBookingId(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public List<BookingResponseDTO> getAllBookingsByUserId(String userId) {
        return bookingRepository.findAllByUserUserId(userId);
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> {
                    BookingResponseDTO dto = new BookingResponseDTO();
                    dto.setBookingId(booking.getBookingId());
                    dto.setBookingTime(booking.getBookingTime());
                    dto.setTotalAmount(booking.getTotalAmount());
                    dto.setStatus(booking.getStatus());
                    dto.setPaymentId(booking.getPayment().getPaymentId());
                    dto.setUserId(booking.getUser().getUserId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
