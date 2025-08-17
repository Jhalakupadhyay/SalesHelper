package com.a2y.salesHelper.service.impl;

import com.a2y.salesHelper.config.PasswordHashingConfig;
import com.a2y.salesHelper.db.entity.UserEntity;
import com.a2y.salesHelper.db.repository.UserRepository;
import com.a2y.salesHelper.enums.Role;
import com.a2y.salesHelper.pojo.User;
import com.a2y.salesHelper.service.interfaces.UserAuthService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public User authenticateUser(String email, String password) {

        try {
            UserEntity userEntity = userRepository.findByEmail(email);
            if (userEntity == null) {
                throw new RuntimeException("User does not exist"); // User not found
            }
            if (passwordHashingConfig.passwordEncoder().matches(password, userEntity.getPassword())) {
                // Password matches, return user details
                return User.builder()
                        .id(userEntity.getId())
                        .firstName(userEntity.getFirstName())
                        .lastName(userEntity.getLastName())
                        .email(userEntity.getEmail())
                        .role(userEntity.getRole())
                        .isReset(userEntity.getIsReset())
                        .build();
            } else {
                throw new RuntimeException("Invalid credentials"); // Password does not match
            }
        } catch (Exception e) {
            throw new RuntimeException("User authentication failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean resetPassword(String email, String newPassword, String oldPassword) {
        try {
            UserEntity userEntity = userRepository.findByEmail(email);
            if(userEntity == null) {
                throw new RuntimeException("User does not exist"); // User not found
            }
            if( passwordHashingConfig.passwordEncoder().matches(oldPassword, userEntity.getPassword()))
            {
                userEntity.setPassword(passwordHashingConfig.passwordEncoder().encode(newPassword));
                userEntity.setIsReset(Boolean.TRUE); // Set isReset to true
                userRepository.save(userEntity);
                return true;
            }else {
                throw new RuntimeException("Old password does not match.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Password reset failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAllUsersForAdmin(Long adminId) {
       List<UserEntity> userEntities = userRepository.findAllById(adminId);

        return userEntities.stream().map(userEntity -> User.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .isReset(userEntity.getIsReset())
                .build()).toList();
    }

    @Override
    public Boolean EditUser(User user) {
        try {
            UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setEmail(user.getEmail());
            userEntity.setRole(user.getRole());
            userRepository.save(userEntity);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("User update failed: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return User.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .isReset(userEntity.getIsReset())
                .build();
    }

}
