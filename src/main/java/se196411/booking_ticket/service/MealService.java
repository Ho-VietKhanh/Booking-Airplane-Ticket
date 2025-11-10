package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.MealEntity;

import java.util.List;
import java.util.Optional;

public interface MealService {
    MealEntity create(MealEntity meal);
    MealEntity findById(Integer mealId);
    List<MealEntity> findAll();
    MealEntity update(Integer mealId, MealEntity meal);
    void deleteById(Integer mealId);

    Optional<Object> getAllMeals();
}