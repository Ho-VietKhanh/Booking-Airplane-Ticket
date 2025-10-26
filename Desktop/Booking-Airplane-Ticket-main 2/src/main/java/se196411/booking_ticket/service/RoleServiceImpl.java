package se196411.booking_ticket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.RoleEntity;
import se196411.booking_ticket.repository.RoleRepository;
import se196411.booking_ticket.service.RoleService;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<RoleEntity> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public RoleEntity save(RoleEntity role) {
        return roleRepository.save(role);
    }
}