package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.enums.Role;
import com.a2y.salesHelper.pojo.User;
import com.a2y.salesHelper.service.interfaces.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
            summary = "SignIn API",
            description = "API takes email and password and signIn the user accordingly."
    )
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(String email, String password) {
        User isAuthenticated = userAuthService.authenticateUser(email, password);
        if (isAuthenticated != null) {
            return new ResponseEntity<>(isAuthenticated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
            summary = "Reset Password API",
            description = "API takes email, new password and old password and resets the user password."
    )
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(String email, String newPassword, String oldPassword) {
        Boolean isReset = userAuthService.resetPassword(email, newPassword, oldPassword);
        if (isReset) {
            return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Password reset failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Find all user invited by admin",
            description = "api takes user ID"
    )
    @GetMapping("/getInvitedUsers")
    public ResponseEntity<List<User>> getAllUsersForAdmin(Long adminId) {
        return new ResponseEntity<>(userAuthService.getAllUsersForAdmin(adminId), HttpStatus.OK);
    }
}
