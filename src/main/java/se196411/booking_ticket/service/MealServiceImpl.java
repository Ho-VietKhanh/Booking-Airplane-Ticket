package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.MealEntity;
import se196411.booking_ticket.repository.MealRepository;


import java.util.List;
import java.util.Optional;

@Service
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;

    public MealServiceImpl(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    @Override
    public MealEntity create(MealEntity meal) {
        return mealRepository.save(meal);
    }

    @Override
    public MealEntity findById(Integer mealId) {
        return mealRepository.findById(mealId).orElse(null);
    }

    @Override
    public List<MealEntity> findAll() {
        return mealRepository.findAll();
    }

    @Override
    public MealEntity update(Integer mealId, MealEntity meal) {
        Optional<MealEntity> existing = mealRepository.findById(mealId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Meal not found: " + mealId);
        }
        MealEntity m = existing.get();
        m.setPrice(meal.getPrice());
        m.setName(meal.getName());
        m.setNote(meal.getNote());
        return mealRepository.save(m);
    }

    @Override
    public void deleteById(Integer mealId) {
        mealRepository.deleteById(mealId);
    }

    @Override
    public Optional<Object> getAllMeals() {
        return Optional.of(mealRepository.findAll());
    }
}