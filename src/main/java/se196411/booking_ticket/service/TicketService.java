package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.TicketEntity;

import java.util.List;

public interface TicketService {
    TicketEntity create(TicketEntity ticket);
    TicketEntity findById(String ticketId);
    List<TicketEntity> findAll();
    TicketEntity update(String ticketId, TicketEntity ticket);
    void deleteById(String ticketId);
}
