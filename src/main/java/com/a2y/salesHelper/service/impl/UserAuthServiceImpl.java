package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.config.PasswordHashingConfig;
import com.a2y.salesHelper.db.entity.UserEntity;
import com.a2y.salesHelper.db.repository.UserRepository;
import com.a2y.salesHelper.enums.Role;
import com.a2y.salesHelper.service.interfaces.UserAuthService;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final PasswordHashingConfig passwordHashingConfig;

    public UserAuthServiceImpl(UserRepository userRepository, PasswordHashingConfig passwordHashingConfig) {
        this.userRepository = userRepository;
        this.passwordHashingConfig = passwordHashingConfig;
    }


    @Override
    public boolean registerUser(String userName, String email, String password, Role role) {

        try{
            UserEntity userEntity = UserEntity.builder()
                    .firstName(userName.split(" ")[0])
                    .lastName(userName.split(" ").length > 1 ? userName.split(" ")[1] : "")
                    .email(email)
                    .password(passwordHashingConfig.passwordEncoder().encode(password)) // Hash the password
                    .role(role)// Password should be hashed before saving
                    .build();
            userRepository.save(userEntity);
            return true;
        }catch (Exception e) {
            throw new RuntimeException("User registration failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean authenticateUser(String email, String password) {

        try {
            UserEntity userEntity = userRepository.findByEmail(email);
            return userEntity != null && passwordHashingConfig.passwordEncoder().matches(password, userEntity.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("User authentication failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean resetPassword(String email, String newPassword, String oldPassword) {
        try {
            UserEntity userEntity = userRepository.findByEmail(email);
            if (userEntity != null && passwordHashingConfig.passwordEncoder().matches(oldPassword, userEntity.getPassword())) {
                userEntity.setPassword(passwordHashingConfig.passwordEncoder().encode(newPassword));
                userRepository.save(userEntity);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Password reset failed: " + e.getMessage(), e);
        }
    }

}
