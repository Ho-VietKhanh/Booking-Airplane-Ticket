package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.BookingEntity;

public interface EmailService {
    void sendBookingConfirmationEmail(BookingEntity booking, String recipientEmail);
}

