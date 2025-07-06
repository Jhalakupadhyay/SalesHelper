package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.config.PasswordHashingConfig;
import com.a2y.salesHelper.db.entity.UserEntity;
import com.a2y.salesHelper.db.repository.UserRepository;
import com.a2y.salesHelper.enums.Role;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final PasswordHashingConfig passwordHashingConfig;
    private final UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(PasswordHashingConfig passwordHashingConfig, UserRepository userRepository) {
        this.passwordHashingConfig = passwordHashingConfig;
        this.userRepository = userRepository;
    }

    public void sendCredentialsEmail(String toEmail, String username, String password, Role role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your Account Credentials");
            helper.setText(buildHtmlContent(username, password), true);

            mailSender.send(message);

            //hash the password and save it to the database
            UserEntity userEntity = UserEntity.builder()
                    .firstName(username.split(" ")[0])
                    .lastName(username.split(" ").length > 1 ? username.split(" ")[1] : "")
                    .email(toEmail)
                    .password(passwordHashingConfig.passwordEncoder().encode(password)) // Password should be hashed before saving
                    .role(role)
                    .build();

            //check if the user already exists
            UserEntity existingUser = userRepository.findByEmail(toEmail);

            //if already exists, update the user entity
            if (existingUser != null) {
                userEntity.setId(existingUser.getId());
            }

            // Save the user entity to the database (assuming you have a UserRepository)
            userRepository.save(userEntity);


        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildHtmlContent(String username, String password) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Account Credentials</title>" +
                "<style>" +
                "body {" +
                "    font-family: Arial, sans-serif;" +
                "    margin: 0;" +
                "    padding: 20px;" +
                "    background-color: #f5f5f5;" +
                "}" +
                ".container {" +
                "    max-width: 600px;" +
                "    margin: 0 auto;" +
                "    background-color: white;" +
                "    padding: 30px;" +
                "    border-radius: 10px;" +
                "    box-shadow: 0 2px 10px rgba(0,0,0,0.1);" +
                "}" +
                ".header {" +
                "    text-align: center;" +
                "    margin-bottom: 30px;" +
                "    color: #333;" +
                "}" +
                ".credentials-table {" +
                "    width: 100%;" +
                "    border-collapse: collapse;" +
                "    margin: 20px 0;" +
                "}" +
                ".credentials-table td {" +
                "    padding: 15px;" +
                "    border: 1px solid #ddd;" +
                "    font-size: 16px;" +
                "}" +
                ".credentials-table td:first-child {" +
                "    background-color: #f8f9fa;" +
                "    font-weight: bold;" +
                "    color: #495057;" +
                "    width: 30%;" +
                "}" +
                ".credentials-table td:last-child {" +
                "    background-color: #fff;" +
                "    color: #333;" +
                "}" +
                ".footer {" +
                "    margin-top: 30px;" +
                "    text-align: center;" +
                "    color: #666;" +
                "    font-size: 14px;" +
                "}" +
                ".warning {" +
                "    background-color: #fff3cd;" +
                "    border: 1px solid #ffeaa7;" +
                "    padding: 15px;" +
                "    border-radius: 5px;" +
                "    margin: 20px 0;" +
                "    color: #856404;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"header\">" +
                "<h2>Your Account Credentials</h2>" +
                "</div>" +
                "<table class=\"credentials-table\">" +
                "<tr>" +
                "<td>Username</td>" +
                "<td>" + username + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Password</td>" +
                "<td>" + password + "</td>" +
                "</tr>" +
                "</table>" +
                "<div class=\"warning\">" +
                "<strong>Important:</strong> Please keep these credentials secure and change your password after first login." +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>If you have any questions, please contact our support team.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
