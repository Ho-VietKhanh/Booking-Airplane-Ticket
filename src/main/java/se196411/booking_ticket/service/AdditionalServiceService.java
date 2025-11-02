package se196411.booking_ticket.service;

import se196411.booking_ticket.model.dto.BaggageOptionDTO;
import se196411.booking_ticket.model.dto.MealOptionDTO;

import java.util.List;

public interface AdditionalServiceService {
    List<BaggageOptionDTO> getAllBaggageOptions();
    List<MealOptionDTO> getAllMealOptions();
    BaggageOptionDTO getBaggageOptionById(String id);
    MealOptionDTO getMealOptionById(String id);
}

