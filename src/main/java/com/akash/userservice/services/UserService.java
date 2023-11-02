package com.akash.userservice.services;

import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponseDto getUserDetails(UUID id) throws UserNotFoundException;

    UserResponseDto setUserRoles(UUID id, List<UUID> roleIds) throws UserNotFoundException;
}
