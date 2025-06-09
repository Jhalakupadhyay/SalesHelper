package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.service.interfaces.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
@Tag(name = "User Auth API",description = "API related ")
public class UserController {

    private final UserAuthService userAuthService;

    @Autowired
    public UserController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Operation(
            summary = "Register User API",
            description = "API takes name email and Password and registers the user."
    )
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(String userName, String email, String password) {
        boolean isRegistered = userAuthService.registerUser(userName, email, password);
        if (isRegistered) {
            return new ResponseEntity<>("Registration successful", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "SignIn API",
            description = "API takes email and password and signIn the user accordingly."
    )
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(String email, String password) {
        boolean isAuthenticated = userAuthService.authenticateUser(email, password);
        if (isAuthenticated) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
