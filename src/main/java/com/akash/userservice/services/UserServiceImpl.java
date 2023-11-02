package com.akash.userservice.services;

import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.UserNotFoundException;
import com.akash.userservice.models.Role;
import com.akash.userservice.models.User;
import com.akash.userservice.repositories.RoleRepository;
import com.akash.userservice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponseDto getUserDetails(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

        return UserResponseDto.from(optionalUser.get());
    }

    @Override
    public UserResponseDto setUserRoles(UUID id, List<UUID> roleIds) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

        List<Role> roles = roleRepository.findAllByIdIn(roleIds);

        User user = optionalUser.get();
        roles.forEach(role -> user.getRoles().add(role));
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }
}
