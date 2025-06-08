package com.a2y.salesHelper.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "user",schema = "sales")public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    @PrePersist
    @PreUpdate
    public void hashPassword() {
        if (this.password != null && !this.password.startsWith("$2a$")) {
            // Only hash if it's not already hashed (BCrypt hashes start with $2a$)
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(this.password);
        }
    }
}
