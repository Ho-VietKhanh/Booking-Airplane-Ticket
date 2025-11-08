package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.dto.BaggageOptionDTO;
import se196411.booking_ticket.model.dto.MealOptionDTO;
import se196411.booking_ticket.model.entity.LuggageEntity;
import se196411.booking_ticket.model.entity.MealEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    @Autowired
    private MealService mealService;

    @Autowired
    private LuggageService luggageService;

    @Override
    public List<BaggageOptionDTO> getAllBaggageOptions() {
        List<LuggageEntity> luggageEntities = luggageService.findAll();
        return luggageEntities.stream()
                .map(l -> new BaggageOptionDTO(
                        String.valueOf(l.getLuggageId()),
                        "Hành Lý " + l.getLuggageAllowance() + "kg",
                        l.getLuggageAllowance().intValue(),
                        l.getPrice(),
                        l.getNote()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MealOptionDTO> getAllMealOptions() {
        List<MealEntity> mealEntities = mealService.findAll();
        return mealEntities.stream()
                .map(m -> new MealOptionDTO(
                        String.valueOf(m.getMealId()),
                        m.getName(),
                        m.getPrice(),
                        m.getNote(),
                        null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public BaggageOptionDTO getBaggageOptionById(String id) {
        try {
            Integer luggageId = Integer.parseInt(id);
            LuggageEntity luggage = luggageService.findById(luggageId);
            if (luggage != null) {
                return new BaggageOptionDTO(
                        String.valueOf(luggage.getLuggageId()),
                        "Hành Lý " + luggage.getLuggageAllowance() + "kg",
                        luggage.getLuggageAllowance().intValue(),
                        luggage.getPrice(),
                        luggage.getNote()
                );
            }
        } catch (NumberFormatException e) {
            // Invalid ID format
        }
        return null;
    }

    @Override
    public MealOptionDTO getMealOptionById(String id) {
        try {
            Integer mealId = Integer.parseInt(id);
            MealEntity meal = mealService.findById(mealId);
            if (meal != null) {
                return new MealOptionDTO(
                        String.valueOf(meal.getMealId()),
                        meal.getName(),
                        meal.getPrice(),
                        meal.getNote(),
                        null
                );
            }
        } catch (NumberFormatException e) {
            // Invalid ID format
        }
        return null;
    }
}

