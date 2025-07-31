package com.carelink.account.service;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.dto.UpdateProfileRequest;
import java.util.List;

public interface AccountService {
    User createUser(User user);

    boolean validateLogin(String username, String password);

    User getUserById(Integer id);

    User getUserByEmail(String email);

    User getUserByPhoneNumber(String phoneNumber);

    User updateUser(User user);

    UserCredentials getUserCredentialsByUser(User user);

    UserCredentials getUserCredentialsByUsername(String username);

    UserCredentials getUserCredentialsByResetToken(String token);

    UserCredentials updateUserCredentials(UserCredentials creds);

    String encodePassword(String rawPassword);

    UserCredentials getUserCredentialsByUserId(Integer UserID);

    List<User> getAllUsersByRole(String role);

    void updateUserProfile(UpdateProfileRequest request);
}