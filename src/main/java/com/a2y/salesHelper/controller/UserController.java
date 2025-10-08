package com.a2y.salesHelper.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a2y.salesHelper.config.JwtService;
import com.a2y.salesHelper.enums.Role;
import com.a2y.salesHelper.pojo.AuthResponse;
import com.a2y.salesHelper.pojo.LoginRequest;
import com.a2y.salesHelper.pojo.User;
import com.a2y.salesHelper.service.interfaces.UserAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/api/auth")
@Tag(name = "User Auth API", description = "API related ")
public class UserController {

    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @Autowired
    public UserController(JwtService jwtService, UserAuthService userAuthService) {
        this.jwtService = jwtService;
        this.userAuthService = userAuthService;
    }

    @Operation(summary = "SignIn API", description = "API takes email and password and signIn the user accordingly.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
        User user = userAuthService.authenticateUser(request.getEmail(), request.getPassword());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse());
        }

        // If no token provided → issue a new one
        if (request.getToken() == null) {
            String jwt = jwtService.generateToken(
                    user.getId(),
                    user.getTenantId(),
                    user.getEmail(),
                    user.getRole().name());
            return ResponseEntity.ok(AuthResponse.builder()
                    .token(jwt)
                    .tenantId(user.getTenantId())
                    .role(user.getRole().name())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .build());
        }

        // If token provided → validate it
        if (!jwtService.isTokenValid(request.getToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse());
        }

        return ResponseEntity.ok(AuthResponse.builder()
                .token(request.getToken())
                .tenantId(user.getTenantId())
                .role(user.getRole().name())
                .userId(user.getId())
                .email(user.getEmail())
                .build());
    }

    @Operation(summary = "Reset Password API", description = "API takes email, new password and old password and resets the user password.")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword,
            @RequestParam String oldPassword) {
        Boolean isReset = userAuthService.resetPassword(email, newPassword, oldPassword);
        if (isReset) {
            return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Password reset failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Find all user invited by admin", description = "api takes user ID")
    @GetMapping("/getInvitedUsers")
    public ResponseEntity<List<User>> getAllUsersForAdmin(@RequestParam Long adminId) {
        return new ResponseEntity<>(userAuthService.getAllUsersForAdmin(adminId), HttpStatus.OK);
    }

    @Operation(summary = "Edit User API", description = "API takes user object and updates the user details.")
    @PostMapping("/editUser")
    public ResponseEntity<String> editUser(User user) {
        Boolean isEdited = userAuthService.EditUser(user);
        if (isEdited) {
            return new ResponseEntity<>("User details updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update user details", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password, @RequestParam Role role,
            @RequestParam Long adminId) {
        Boolean isRegistered = userAuthService.registerUser(firstName, lastName, email, password, role, adminId);
        if (isRegistered) {
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("User registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get User by ID API", description = "API takes user ID and returns the user details.")
    @GetMapping("/getUserById")
    public ResponseEntity<User> getUserById(@RequestParam Long userId) {
        User user = userAuthService.getUserById(userId);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
