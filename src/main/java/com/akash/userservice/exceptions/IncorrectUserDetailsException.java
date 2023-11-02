package com.akash.userservice.exceptions;

public class IncorrectUserDetailsException extends Exception{
    public IncorrectUserDetailsException(String message) {
        super(message);
    }
}
