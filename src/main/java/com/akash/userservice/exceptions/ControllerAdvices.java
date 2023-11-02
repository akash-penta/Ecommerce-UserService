package com.akash.userservice.exceptions;

import com.akash.userservice.dtos.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvices {

    @ExceptionHandler(IncorrectUserDetailsException.class)
    public ResponseEntity<ExceptionDto> handleIncorrectUserDetailsException(IncorrectUserDetailsException incorrectUserDetailsException) {
        return new ResponseEntity(
            new ExceptionDto(HttpStatus.NOT_FOUND, incorrectUserDetailsException.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        return new ResponseEntity(
                new ExceptionDto(HttpStatus.NOT_FOUND, userNotFoundException.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
