package se196411.booking_ticket.service;

import se196411.booking_ticket.model.RoleEntity;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleEntity> findAllRoles();

    RoleEntity createRole(RoleEntity role);
}
