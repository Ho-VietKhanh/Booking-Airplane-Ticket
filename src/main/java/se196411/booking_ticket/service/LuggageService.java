package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.LuggageEntity;

import java.util.List;

public interface LuggageService {
    LuggageEntity create(LuggageEntity luggage);
    LuggageEntity findById(Integer luggageId);
    List<LuggageEntity> findAll();
    LuggageEntity update(Integer luggageId, LuggageEntity luggage);
    void deleteById(Integer luggageId);
}


