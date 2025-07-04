package com.carelink.account.controller;

import com.carelink.account.dto.LoginRequest;
import com.carelink.account.dto.RegisterRequest;
import com.carelink.account.dto.UserResponse;
import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.service.AccountService;
import com.carelink.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public AccountController(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole("USER");

        User createdUser = accountService.createUser(user);

        String generatedUsername = accountService.getUserCredentialsByUser(createdUser).getUsername();
        String generatedPassword = createdUser.getTransientPassword();

        UserResponse response = new UserResponse();
        response.setUsername(generatedUsername);
        response.setEmail(createdUser.getEmail());
        response.setGeneratedPassword(generatedPassword);

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        UserCredentials credentials = accountService.getUserCredentialsByUsername(request.getUsername());
        if (credentials == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (!BCrypt.checkpw(request.getPassword(), credentials.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = jwtUtil.generateToken(credentials.getUsername());

        User user = credentials.getUser();

        UserResponse response = new UserResponse();
        response.setUsername(credentials.getUsername());
        response.setEmail(user.getEmail());
        response.setToken(token);
        response.setGeneratedPassword(null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);

            UserCredentials creds = accountService.getUserCredentialsByUsername(username);
            if (creds == null) {
                return ResponseEntity.status(404).body(null);
            }

            User user = creds.getUser();
            if (user == null) {
                return ResponseEntity.status(404).body(null);
            }

            UserResponse response = new UserResponse();
            response.setUsername(creds.getUsername());

            if (user.getUserID() != null) {
                response.setUserId(user.getUserID().longValue());
            }

            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());
            response.setPhoneNumber(user.getPhoneNumber());

            if (user.getDateOfBirth() != null) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                response.setDateOfBirth(sdf.format(user.getDateOfBirth()));

            }

            response.setGender(user.getGender());
            response.setAddress(user.getAddress());
            response.setCity(user.getCity());
            response.setCountry(user.getCountry());
            response.setRole(user.getRole());
            response.setNotificationPreference(user.getNotificationPreference());
            response.setGeneratedPassword(null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestHeader("Authorization") String authHeader,
            @RequestBody UserResponse updatedInfo) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);

            UserCredentials creds = accountService.getUserCredentialsByUsername(username);
            if (creds == null) {
                return ResponseEntity.status(404).body(null);
            }

            User user = creds.getUser();
            if (user == null) {
                return ResponseEntity.status(404).body(null);
            }

            if (updatedInfo.getEmail() != null) {
                user.setEmail(updatedInfo.getEmail());
            }
            if (updatedInfo.getLastName() != null) {
                user.setLastName(updatedInfo.getLastName());
            }
            if (updatedInfo.getPhoneNumber() != null) {
                user.setPhoneNumber(updatedInfo.getPhoneNumber());
            }
            if (updatedInfo.getGender() != null) {
                user.setGender(updatedInfo.getGender());
            }
            if (updatedInfo.getAddress() != null) {
                user.setAddress(updatedInfo.getAddress());
            }
            if (updatedInfo.getCity() != null) {
                user.setCity(updatedInfo.getCity());
            }
            if (updatedInfo.getCountry() != null) {
                user.setCountry(updatedInfo.getCountry());
            }
            if (updatedInfo.getRole() != null) {
                user.setRole(updatedInfo.getRole());
            }
            if (updatedInfo.getNotificationPreference() != null) {
                user.setNotificationPreference(updatedInfo.getNotificationPreference());
            }

            accountService.updateUser(user);

            UserResponse response = new UserResponse();
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setGender(user.getGender());
            response.setAddress(user.getAddress());
            response.setCity(user.getCity());
            response.setCountry(user.getCountry());
            response.setRole(user.getRole());
            response.setNotificationPreference(user.getNotificationPreference());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }

}