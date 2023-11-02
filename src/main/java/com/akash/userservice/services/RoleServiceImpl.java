package com.akash.userservice.services;

import com.akash.userservice.dtos.RoleDto;
import com.akash.userservice.models.Role;
import com.akash.userservice.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService{

    private RoleRepository roleRepository;

    public RoleServiceImpl(
            RoleRepository roleRepository
    ) {
        this.roleRepository = roleRepository;
    }
    @Override
    public RoleDto createRole(String roleStr) {
        Optional<Role> optionalRole = roleRepository.findByRole(roleStr);
        if(optionalRole.isPresent()) {
            return RoleDto.from(optionalRole.get());
        }
        Role role = new Role();
        role.setRole(roleStr);

        Role savedRole = roleRepository.save(role);

        return RoleDto.from(savedRole);
    }
}
