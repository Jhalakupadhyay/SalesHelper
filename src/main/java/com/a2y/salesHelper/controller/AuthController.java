package com.a2y.salesHelper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a2y.salesHelper.config.JwtService;
import com.a2y.salesHelper.db.entity.UserEntity;
import com.a2y.salesHelper.db.repository.UserRepository;

import lombok.Data;

@RestController
@RequestMapping("/auth")
public class    AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        // Persist user
        UserEntity user = UserEntity.builder()
                .tenantId(req.getTenantId())
                .email(req.getEmail())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .password(req.getPassword())
                .role(req.getRole())
                .build();
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getEmail(),
                user.getRole().name());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequest req) {
        UserEntity user = userRepository.findByEmailAndTenantId(req.getEmail(), req.getTenantId());
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getEmail(),
                user.getRole().name());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @Data
    public static class SignupRequest {
        private Long tenantId;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private com.a2y.salesHelper.enums.Role role;
    }

    @Data
    public static class SigninRequest {
        private Long tenantId;
        private String email;
        private String password;
    }

    @Data
    public static class TokenResponse {
        private final String token;
    }
}
