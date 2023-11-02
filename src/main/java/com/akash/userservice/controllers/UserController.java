package com.akash.userservice.controllers;

import com.akash.userservice.dtos.SetUserRolesDto;
import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.UserNotFoundException;
import com.akash.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable("id") String id) throws UserNotFoundException {
        UserResponseDto userResponseDto = userService.getUserDetails(UUID.fromString(id));
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/setroles")
    public  ResponseEntity<UserResponseDto> setUserRoles(
            @PathVariable String id,
            @RequestBody SetUserRolesDto setUserRolesDto
    ) throws UserNotFoundException {
        List<UUID> roleIds = new ArrayList<>();
        setUserRolesDto.getRoleIds().forEach(roleId -> roleIds.add(UUID.fromString(roleId)));

        UserResponseDto userResponseDto = userService.setUserRoles(
                UUID.fromString(id),
                roleIds
        );
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

}
