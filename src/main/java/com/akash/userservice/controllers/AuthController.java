package com.akash.userservice.controllers;

import com.akash.userservice.dtos.TokenRequestDto;
import com.akash.userservice.dtos.UserRequestDto;
import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.IncorrectUserDetailsException;
import com.akash.userservice.models.SessionStatus;
import com.akash.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = authService.signUp(userRequestDto.getEmail(), userRequestDto.getPassword());
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> logIn(@RequestBody UserRequestDto userRequestDto) throws IncorrectUserDetailsException {
        return authService.logIn(userRequestDto.getEmail(), userRequestDto.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(@RequestBody TokenRequestDto tokenRequestDto) {
        return authService.logOut(tokenRequestDto.getToken(), UUID.fromString(tokenRequestDto.getId()));
    }

    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody TokenRequestDto tokenRequestDto) {
        return authService.validateToken(tokenRequestDto.getToken(), UUID.fromString(tokenRequestDto.getId()));
    }
}
