package com.carelink.account.service;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.repository.UserCredentialsRepository;
import com.carelink.account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCredentialsRepository credentialsRepository;

    public User createUser(User user, String username, String password) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);

        User savedUser = userRepository.save(user);

        UserCredentials credentials = new UserCredentials();
        credentials.setUser(savedUser);
        credentials.setUsername(username);
        credentials.setPasswordHash(hashedPassword);
        credentials.setPasswordSalt(salt);

        credentialsRepository.save(credentials);

        return savedUser;
    }

    public boolean validateLogin(String username, String password) {
        UserCredentials creds = credentialsRepository.findByUsername(username);
        if (creds == null)
            return false;
        return BCrypt.checkpw(password, creds.getPasswordHash());
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }
}