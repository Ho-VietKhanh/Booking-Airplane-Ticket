package se196411.booking_ticket.service;

import se196411.booking_ticket.model.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    List<RoleEntity> findAllRoles();

    RoleEntity createRole(RoleEntity role);
}
