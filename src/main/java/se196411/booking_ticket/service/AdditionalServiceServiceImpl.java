package se196411.booking_ticket.service;

import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.dto.BaggageOptionDTO;
import se196411.booking_ticket.model.dto.MealOptionDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    @Override
    public List<BaggageOptionDTO> getAllBaggageOptions() {
        List<BaggageOptionDTO> options = new ArrayList<>();

        options.add(new BaggageOptionDTO("BAG_15", "Hành Lý 15kg", 15,
                new BigDecimal("200000"), "Hành lý ký gửi 15kg"));
        options.add(new BaggageOptionDTO("BAG_20", "Hành Lý 20kg", 20,
                new BigDecimal("300000"), "Hành lý ký gửi 20kg"));
        options.add(new BaggageOptionDTO("BAG_25", "Hành Lý 25kg", 25,
                new BigDecimal("400000"), "Hành lý ký gửi 25kg"));
        options.add(new BaggageOptionDTO("BAG_30", "Hành Lý 30kg", 30,
                new BigDecimal("500000"), "Hành lý ký gửi 30kg"));

        return options;
    }

    @Override
    public List<MealOptionDTO> getAllMealOptions() {
        List<MealOptionDTO> options = new ArrayList<>();

        options.add(new MealOptionDTO("MEAL_01", "Xôi Thịt Kho Trứng",
                new BigDecimal("80000"), "Xôi thịt kho trứng truyền thống", null));
        options.add(new MealOptionDTO("MEAL_02", "Mỳ Ý Sốt Bò Bằm",
                new BigDecimal("80000"), "Mỳ Ý sốt bò bằm thơm ngon", null));
        options.add(new MealOptionDTO("MEAL_03", "Mì Ly Ăn Liền Và 1 Lon Nước Ngọt Có Ga (7 Up/Pepsi)",
                new BigDecimal("55000"), "Combo mì ly và nước ngọt", null));
        options.add(new MealOptionDTO("MEAL_04", "Bánh Mì Pate",
                new BigDecimal("45000"), "Bánh mì pate truyền thống", null));

        return options;
    }

    @Override
    public BaggageOptionDTO getBaggageOptionById(String id) {
        return getAllBaggageOptions().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public MealOptionDTO getMealOptionById(String id) {
        return getAllMealOptions().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

