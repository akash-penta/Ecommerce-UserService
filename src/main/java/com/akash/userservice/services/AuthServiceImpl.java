package com.akash.userservice.services;

import com.akash.userservice.dtos.TokenResponseDto;
import com.akash.userservice.dtos.UserResponseDto;
import com.akash.userservice.exceptions.IncorrectUserDetailsException;
import com.akash.userservice.models.Session;
import com.akash.userservice.models.SessionStatus;
import com.akash.userservice.models.User;
import com.akash.userservice.repositories.SessionRepository;
import com.akash.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private String secretKeyString = "abcghsgssgshsjsshshbdhsddhddhdhdhdhh";
    private MacAlgorithm alg;
    private SecretKey secretKey;

    public AuthServiceImpl(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

        alg = Jwts.SIG.HS256;
        //convert  string to byte array
        byte[] secretKeyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
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

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("id", user.getId());
        jsonForJwt.put("email", user.getEmail());
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getRole()));
        jsonForJwt.put("roles", roles);
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expireAt", new Date(new Date().getTime() + 2*24*60*60*1000));
        String token = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(secretKey, alg)
                .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        UserResponseDto responseDto = UserResponseDto.from(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);

        return new ResponseEntity<>(responseDto, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> logOut(String token) {
        Optional<Session> optionalSession = sessionRepository.findByToken(token);
        if(optionalSession.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Session session = optionalSession.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<TokenResponseDto> validateToken(String token) {
        Optional<Session> optionalSession = sessionRepository.findByToken(token);
        if(optionalSession.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Session session = optionalSession.get();
        if(session.getSessionStatus() == SessionStatus.ENDED) {
            return ResponseEntity.badRequest().build();
        }

        byte[] secretKeyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        TokenResponseDto responseDto = TokenResponseDto.builder()
                .id(UUID.fromString((String) claimsJws.getPayload().get("id")))
                .email((String) claimsJws.getPayload().get("email"))
                .roles((List<String>) claimsJws.getPayload().get("roles"))
                .createdAt(new Date((Long) claimsJws.getPayload().get("createdAt")))
                .expireAt(new Date((Long) claimsJws.getPayload().get("expireAt")))
                .build();

        Date currentDate = new Date();
        if(currentDate.compareTo(responseDto.getExpireAt()) >= 0) {
            session.setSessionStatus(SessionStatus.ENDED);
            sessionRepository.save(session);
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
