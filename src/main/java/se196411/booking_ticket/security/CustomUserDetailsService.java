package se196411.booking_ticket.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.entity.UserEntity;
import se196411.booking_ticket.model.enums.Role;
import se196411.booking_ticket.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    mapRolesToAuthorities(user));
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(UserEntity user) {
        se196411.booking_ticket.model.enums.Role role = user.getRole() != null ? user.getRole() : se196411.booking_ticket.model.enums.Role.USER;
        String authority = "ROLE_" + role.name();
        return Collections.singleton(new SimpleGrantedAuthority(authority));
    }
}
