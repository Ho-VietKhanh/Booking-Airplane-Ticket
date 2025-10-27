package se196411.booking_ticket.service;

import se196411.booking_ticket.model.RoleEntity;
import java.util.Optional;

public interface RoleService {
    Optional<RoleEntity> findByRoleName(String roleName);
    RoleEntity save(RoleEntity role);
}