package se196411.booking_ticket.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se196411.booking_ticket.model.entity.MealEntity;

@Repository
public interface MealRepository extends JpaRepository<MealEntity, Integer> {
}
