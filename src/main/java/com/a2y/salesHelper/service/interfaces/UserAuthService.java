package com.a2y.salesHelper.service.interfaces;

import com.a2y.salesHelper.enums.Role;
import com.a2y.salesHelper.pojo.User;

public interface UserAuthService {

    /**
     * Registers a new user with the provided details.
     *
     * @param userName the username of the new user
     * @param email the email address of the new user
     * @param password the password for the new user
     * @return true if registration is successful, false otherwise
     */
    boolean registerUser(String userName, String email, String password, Role role);

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param email the email address of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     */
    User authenticateUser(String email, String password);

    //Reset Password service
    Boolean resetPassword(String email, String newPassword, String oldPassword);

}
