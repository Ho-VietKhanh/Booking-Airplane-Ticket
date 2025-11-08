package se196411.booking_ticket.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import se196411.booking_ticket.security.AppUserDetails;
import se196411.booking_ticket.repository.UserRepository;
import se196411.booking_ticket.model.entity.UserEntity;

@ControllerAdvice
public class CurrentUserAdvice {

    private final UserRepository userRepository;

    public CurrentUserAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUserName")
    public String currentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "Guest";
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof AppUserDetails) {
            String full = ((AppUserDetails) principal).getFullName();
            return full != null && !full.isBlank() ? full : auth.getName();
        }
        // Try to lookup user by authentication name (commonly email)
        String name = auth.getName();
        if (name != null) {
            try {
                UserEntity user = userRepository.findByEmail(name);
                if (user != null && user.getFullName() != null && !user.getFullName().isBlank()) {
                    return user.getFullName();
                }
            } catch (Exception ignored) {
                // fallback to name below
            }
            return name;
        }
        // Fallback
        return "Guest";
    }
}
