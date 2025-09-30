package swp.project.swp391.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import swp.project.swp391.repository.CustomerRepository;

@Component
@RequiredArgsConstructor
public class Ownership {
    private final CustomerRepository customerRepo;
    public boolean isCustomerOwner(Long customerId, Long userId) {
        return customerRepo.findById(customerId)
                .map(c -> c.getUser()!=null && c.getUser().getId().equals(userId))
                .orElse(false);
    }
}
