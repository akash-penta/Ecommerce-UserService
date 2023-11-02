package com.akash.userservice.services;

import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.IncorrectUserDetailsException;
import com.akash.userservice.models.Session;
import com.akash.userservice.models.SessionStatus;
import com.akash.userservice.models.User;
import com.akash.userservice.repositories.SessionRepository;
import com.akash.userservice.repositories.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthServiceImpl(
            UserRepository userRepository,
            SessionRepository sessionRepository
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public UserResponseDto signUp(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if(optionalUser.isEmpty()) {
            user = new User();
            user.setEmail(email);
        } else {
            user = optionalUser.get();
        }
        user.setPassword(password);

        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }

    @Override
    public ResponseEntity<UserResponseDto> logIn(String email, String password) throws IncorrectUserDetailsException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new IncorrectUserDetailsException("User not exists, Please signup");
        }

        User user = optionalUser.get();

        if(!user.getPassword().equals(password)) {
            throw new IncorrectUserDetailsException("Unable to login, Incorrect user details");
        }

        String token = user.getEmail() + new Date();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        UserResponseDto responseDto = UserResponseDto.from(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token" + token);
        headers.add(HttpHeaders.SET_COOKIE2, user.getId().toString());

        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> logOut(String token, UUID id) {
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, id);
        if(optionalSession.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Session session = optionalSession.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SessionStatus> validateToken(String token, UUID id) {
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, id);
        if(optionalSession.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        SessionStatus sessionStatus = optionalSession.get().getSessionStatus();

        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }
}
