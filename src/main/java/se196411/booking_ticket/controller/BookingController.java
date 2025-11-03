package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.dto.BookingRequestDTO;
import se196411.booking_ticket.model.dto.BookingResponseDTO;
import se196411.booking_ticket.model.dto.TicketResponseDTO;
import se196411.booking_ticket.service.BookingService;
import se196411.booking_ticket.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private TicketService ticketService;

    @PostMapping("/create")
    ResponseEntity<String> insertBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        if (bookingRequestDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        bookingService.insertBooking(bookingRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{bookingId}")
    ResponseEntity<String> updateBooking(@PathVariable String bookingId, @RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO findBooking = bookingService.getBookingByBookingId(bookingId);
        if (findBooking == null) {
            return ResponseEntity.badRequest().build();
        }
        bookingService.updateBookingByBookingId(bookingId, bookingRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{bookingId}")
    ResponseEntity<String> deleteBooking(@PathVariable String bookingId) {
        BookingResponseDTO bookingResponseDTO = bookingService.getBookingByBookingId(bookingId);
        if (bookingResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        bookingService.deleteBookingByBookingId(bookingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getBookingById/{bookingId}")
    ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable String bookingId) {
        BookingResponseDTO bookingResponseDTO = bookingService.getBookingByBookingId(bookingId);
        if (bookingResponseDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(bookingResponseDTO);
    }

    @GetMapping("/getBookingByUser/{userId}")
    ResponseEntity<List<BookingResponseDTO>> getBookingByUser(@PathVariable String userId) {
        List<BookingResponseDTO> bookingResponseDTOList = bookingService.getAllBookingsByUserId(userId);
        if (bookingResponseDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(bookingResponseDTOList);
    }

    @GetMapping("/getAllBooking")
    ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<BookingResponseDTO> bookingResponseDTOList = bookingService.getAllBookings();
        if (bookingResponseDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(bookingResponseDTOList);
    }

    @GetMapping("/getTicketsByBooking/{bookingId}")
    ResponseEntity<List<TicketResponseDTO>> getTicketsByBooking(@PathVariable String bookingId) {
        List<TicketResponseDTO> ticketResponseDTOList = ticketService.getAllTicketsByBookingId(bookingId);
        if (ticketResponseDTOList == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(ticketResponseDTOList);
    }
}