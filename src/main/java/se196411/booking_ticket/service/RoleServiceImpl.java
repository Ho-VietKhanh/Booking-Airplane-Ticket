package se196411.booking_ticket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se196411.booking_ticket.model.RoleEntity;
import se196411.booking_ticket.repository.RoleRepository;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<RoleEntity> findAllRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public RoleEntity createRole(RoleEntity role) {
        return this.roleRepository.save(role);
    }
}
