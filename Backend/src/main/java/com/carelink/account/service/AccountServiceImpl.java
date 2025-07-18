package com.carelink.account.service;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.repository.UserCredentialsRepository;
import com.carelink.account.repository.UserRepository;

import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;

    @Autowired
    private JavaMailSender mailSender;

    public AccountServiceImpl(UserRepository userRepository, UserCredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        User savedUser = userRepository.save(user);

        String firstNamePart = savedUser.getFirstName() != null
                ? savedUser.getFirstName().toLowerCase().replaceAll("[^a-zA-Z0-9]", "")
                : "user";
        String generatedUsername = firstNamePart + savedUser.getUserID();

        String randomPassword = String.format("%04d", new Random().nextInt(10000));

        String hashedPassword = BCrypt.hashpw(randomPassword, BCrypt.gensalt());

        UserCredentials creds = new UserCredentials();
        creds.setUser(savedUser);
        creds.setUsername(generatedUsername);
        creds.setPasswordHash(hashedPassword);
        creds.setPasswordSalt(BCrypt.gensalt());

        credentialsRepository.save(creds);

        savedUser.setTransientPassword(randomPassword);

        return savedUser;
    }

    @Override
    public boolean validateLogin(String username, String password) {
        UserCredentials creds = credentialsRepository.findByUsername(username);
        if (creds == null)
            return false;
        return BCrypt.checkpw(password, creds.getPasswordHash());
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserCredentials getUserCredentialsByUser(User user) {
        return credentialsRepository.findByUser(user);
    }

    @Override
    public UserCredentials getUserCredentialsByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }

    @Override
    public UserCredentials getUserCredentialsByResetToken(String token) {
        return credentialsRepository.findByResetPasswordToken(token);
    }

    @Override
    public UserCredentials updateUserCredentials(UserCredentials creds) {
        return credentialsRepository.save(creds);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public UserCredentials getUserCredentialsByUserId(Integer UserID) {
        return credentialsRepository.findByUser_UserID(UserID);
    }
}