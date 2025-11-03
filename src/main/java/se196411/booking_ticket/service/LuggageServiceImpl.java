package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.LuggageEntity;
import se196411.booking_ticket.repository.LuggageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LuggageServiceImpl implements LuggageService {

    private final LuggageRepository luggageRepository;

    public LuggageServiceImpl(LuggageRepository luggageRepository) {
        this.luggageRepository = luggageRepository;
    }

    @Override
    public LuggageEntity create(LuggageEntity luggage) {
        return luggageRepository.save(luggage);
    }

    @Override
    public LuggageEntity findById(Integer luggageId) {
        return luggageRepository.findById(luggageId).orElse(null);
    }

    @Override
    public List<LuggageEntity> findAll() {
        return luggageRepository.findAll();
    }

    @Override
    public LuggageEntity update(Integer luggageId, LuggageEntity luggage) {
        Optional<LuggageEntity> existing = luggageRepository.findById(luggageId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Luggage not found: " + luggageId);
        }
        LuggageEntity l = existing.get();
        l.setPrice(luggage.getPrice());
        l.setLuggageAllowance(luggage.getLuggageAllowance());
        l.setNote(luggage.getNote());
        return luggageRepository.save(l);
    }

    @Override
    public void deleteById(Integer luggageId) {
        luggageRepository.deleteById(luggageId);
    }
}


