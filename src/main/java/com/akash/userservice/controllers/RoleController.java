package com.akash.userservice.controllers;

import com.akash.userservice.dtos.CreateRoleRequestDto;
import com.akash.userservice.dtos.RoleDto;
import com.akash.userservice.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(
            RoleService roleService
    ) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody CreateRoleRequestDto createRoleRequestDto) {
        RoleDto roleDto = roleService.createRole(createRoleRequestDto.getRole());
        return new ResponseEntity<>(roleDto, HttpStatus.OK);
    }
}
