package com.akash.userservice.controllers;

import com.akash.userservice.dtos.TokenResponseDto;
import com.akash.userservice.dtos.UserRequestDto;
import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.IncorrectUserDetailsException;
import com.akash.userservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Void> logOut(@RequestHeader Map<String, String> headers) {
        String token = headers.get("set-cookie").split(":")[1];
        return authService.logOut(token);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenResponseDto> validateToken(@RequestHeader Map<String, String> headers) {
        System.out.println(headers);
        String token = headers.get("set-cookie").split(":")[1];
        return authService.validateToken(token);
    }
}
