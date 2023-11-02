package com.akash.userservice.services;

import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.IncorrectUserDetailsException;
import com.akash.userservice.models.Session;
import com.akash.userservice.models.SessionStatus;
import com.akash.userservice.models.User;
import com.akash.userservice.repositories.SessionRepository;
import com.akash.userservice.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImpl(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        user.setPassword(bCryptPasswordEncoder.encode(password));

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

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectUserDetailsException("Unable to login, Incorrect user details");
        }

        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();
        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("id", user.getId());
        jsonForJwt.put("email", user.getEmail());
        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expireAt", new Date(new Date().getTime() + 2*24*60*60*1000));
        String token = Jwts.builder()
                            .claims(jsonForJwt)
                            .signWith(key)
                            .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        UserResponseDto responseDto = UserResponseDto.from(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);
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
