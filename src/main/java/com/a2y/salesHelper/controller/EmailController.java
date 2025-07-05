package com.a2y.salesHelper.controller;

import com.a2y.salesHelper.service.impl.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;


    @Operation(
            summary = "Send Credentials Email",
            description = "Sends an email with the user's credentials to the specified email address."
    )
    @PostMapping("/send-credentials")
    public String sendCredentials(@RequestParam String email,
                                  @RequestParam String username,
                                  @RequestParam String password) {
        try {
            emailService.sendCredentialsEmail(email, username, password);
            return "Email sent successfully to: " + email;
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}
