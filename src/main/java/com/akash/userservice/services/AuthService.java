package com.akash.userservice.services;

import com.akash.userservice.dtos.TokenResponseDto;
import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.IncorrectUserDetailsException;
import com.akash.userservice.models.SessionStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AuthService {
    UserResponseDto signUp(String email, String password);
    ResponseEntity<UserResponseDto> logIn(String email, String password) throws IncorrectUserDetailsException;

    ResponseEntity<Void> logOut(String token);

    ResponseEntity<TokenResponseDto> validateToken(String token);
}
