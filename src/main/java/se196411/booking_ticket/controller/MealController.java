package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.entity.MealEntity;
import se196411.booking_ticket.service.MealService;

import java.util.List;

@RestController
@RequestMapping("/api/meal")
public class MealController {

    @Autowired
    private MealService mealService;

    @PostMapping("/create")
    public ResponseEntity<MealEntity> createMeal(@RequestBody MealEntity meal) {
        MealEntity createdMeal = mealService.create(meal);
        return ResponseEntity.ok(createdMeal);
    }

    @GetMapping("/getById/{mealId}")
    public ResponseEntity<MealEntity> getMealById(@PathVariable Integer mealId) {
        MealEntity meal = mealService.findById(mealId);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(meal);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MealEntity>> getAllMeals() {
        List<MealEntity> meals = mealService.findAll();
        return ResponseEntity.ok(meals);
    }

    @PutMapping("/update/{mealId}")
    public ResponseEntity<MealEntity> updateMeal(@PathVariable Integer mealId, @RequestBody MealEntity meal) {
        MealEntity existingMeal = mealService.findById(mealId);
        if (existingMeal == null) {
            return ResponseEntity.notFound().build();
        }
        MealEntity updatedMeal = mealService.update(mealId, meal);
        return ResponseEntity.ok(updatedMeal);
    }

    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<String> deleteMeal(@PathVariable Integer mealId) {
        MealEntity meal = mealService.findById(mealId);
        if (meal == null) {
            return ResponseEntity.notFound().build();
        }
        mealService.deleteById(mealId);
        return ResponseEntity.ok("Meal deleted successfully");
    }
}