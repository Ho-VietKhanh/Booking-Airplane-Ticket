package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.SeatEntity;
import se196411.booking_ticket.repository.SeatRepository;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public SeatEntity create(SeatEntity seat) {
        return this.seatRepository.save(seat);
    }

    @Override
    public SeatEntity findById(String id) {
        return this.seatRepository.findById(id).orElse(null);
    }

    @Override
    public SeatEntity updateById(String id, SeatEntity seat) {
        SeatEntity existSeat = this.seatRepository.findById(id).orElse(null);
        if(existSeat == null) {
            throw new IllegalArgumentException("Seat not found: " + id);
        } else {
            existSeat.setSeatNumber(seat.getSeatNumber());
            existSeat.setSeatClass(seat.getSeatClass());
            existSeat.setAirplane(seat.getAirplane());
            existSeat.setAvailable(seat.isAvailable());
            existSeat.setTickets(seat.getTickets());
            return this.seatRepository.save(existSeat);
        }
    }

    @Override
    public void deleteById(String id) {
        this.seatRepository.deleteById(id);
    }

    @Override
    public List<SeatEntity> findAll() {
        return this.seatRepository.findAll();
    }

    @Override
    public List<SeatEntity> findAvailableSeatsByAirplaneId(String airplaneId, String status) {
        return this.seatRepository.findSeatsByAirplaneIdAndStatus(airplaneId, status);
    }


}
