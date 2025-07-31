package com.carelink.account.controller;

import com.carelink.account.dto.AdminResetDto;
import com.carelink.account.dto.ChangePasswordRequest;
import com.carelink.account.dto.UpdateProfileRequest;
import com.carelink.account.dto.LoginRequest;
import com.carelink.account.dto.RegisterRequest;
import com.carelink.account.dto.RequestResetDto;
import com.carelink.account.dto.UpdatePasswordRequest;
import com.carelink.account.dto.UserResponse;
import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.service.AccountService;
import com.carelink.security.JwtUtil;
import com.carelink.notification.emailService.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.access.AccessDeniedException;
import com.carelink.exception.ResourceNotFoundException;
import com.carelink.exception.BusinessLogicException;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AccountController(AccountService accountService, JwtUtil jwtUtil, EmailService emailService) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole("PATIENT");

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

        User user = credentials.getUser();
        if ("DEACTIVATED".equalsIgnoreCase(user.getRole())) {
            throw new AccessDeniedException("User account is deactivated.");
        }

        String token = jwtUtil.generateToken(
                user.getUserID(),
                credentials.getUsername(),
                user.getRole());

        UserResponse response = new UserResponse();
        response.setUsername(credentials.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);
        response.setGeneratedPassword(null);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🔍 Token username (getProfile): " + username);

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null || creds.getUser() == null) {
            throw new ResourceNotFoundException("User or credentials not found for username: " + username);
        }

        return ResponseEntity.ok(buildUserResponse(creds, creds.getUser()));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UserResponse updatedInfo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🔍 Token username (updateProfile): " + username);

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null || creds.getUser() == null) {
            throw new ResourceNotFoundException("User or credentials not found for username: " + username);
        }

        User user = creds.getUser();

        if (updatedInfo.getEmail() != null)
            user.setEmail(updatedInfo.getEmail());
        if (updatedInfo.getLastName() != null)
            user.setLastName(updatedInfo.getLastName());
        if (updatedInfo.getPhoneNumber() != null)
            user.setPhoneNumber(updatedInfo.getPhoneNumber());
        if (updatedInfo.getDateOfBirth() != null) {
            try {
                user.setDateOfBirth(java.sql.Date.valueOf(updatedInfo.getDateOfBirth()));
            } catch (IllegalArgumentException e) {
                throw new BusinessLogicException("Invalid date format for dateOfBirth. Expected format: yyyy-MM-dd");
            }
        }
        if (updatedInfo.getGender() != null)
            user.setGender(updatedInfo.getGender());
        if (updatedInfo.getAddress() != null)
            user.setAddress(updatedInfo.getAddress());
        if (updatedInfo.getCity() != null)
            user.setCity(updatedInfo.getCity());
        if (updatedInfo.getCountry() != null)
            user.setCountry(updatedInfo.getCountry());
        if (updatedInfo.getNotificationPreference() != null)
            user.setNotificationPreference(updatedInfo.getNotificationPreference());

        accountService.updateUser(user);

        UserResponse response = new UserResponse();
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth() != null ? new SimpleDateFormat("dd/MM/yyyy").format(user.getDateOfBirth()) : null);
        response.setGender(user.getGender());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setCountry(user.getCountry());
        response.setRole(user.getRole());
        response.setNotificationPreference(user.getNotificationPreference());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody RequestResetDto dto) {
        String username = dto.getUsername();
        if (username == null || username.isEmpty()) {
            throw new BusinessLogicException("Username must not be empty.");
        }

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null || creds.getUser() == null) {
            return ResponseEntity.ok("If a user with that username exists, a reset link has been sent to their email.");
        }

        User user = creds.getUser();
        String token = java.util.UUID.randomUUID().toString();
        creds.setResetPasswordToken(token);
        accountService.updateUser(user);

        String resetLink = "https://calm-sky-0157a6e03.1.azurestaticapps.net/pwd_reset.html?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetLink, user.getUserID());

        return ResponseEntity.ok("If a user with that username exists, a reset link has been sent to their email.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reset")
    public ResponseEntity<String> adminResetPassword(@RequestBody AdminResetDto dto) {
        String username = dto.getUsername();
        String newPassword = dto.getNewPassword();
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username must not be empty.");
        }

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null || creds.getUser() == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = creds.getUser();
        String generatedPassword = (newPassword != null && !newPassword.isEmpty())
                ? newPassword
                : java.util.UUID.randomUUID().toString().substring(0, 8);

        String hashedPassword = accountService.encodePassword(generatedPassword);
        creds.setPasswordHash(hashedPassword);
        creds.setResetPasswordToken(null);

        accountService.updateUserCredentials(creds);
        accountService.updateUser(user);
        emailService.sendAdminResetEmail(user.getEmail(), user.getFirstName(), generatedPassword, user.getUserID());
        return ResponseEntity.ok("Password reset successfully and email sent to user.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            throw new BusinessLogicException("Token must not be empty.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new BusinessLogicException("New password must not be empty.");
        }

        UserCredentials creds = accountService.getUserCredentialsByResetToken(request.getToken());
        if (creds == null) {
            throw new ResourceNotFoundException("Invalid or expired token.");
        }

        String hashedPassword = accountService.encodePassword(request.getNewPassword());
        creds.setPasswordHash(hashedPassword);
        creds.setResetPasswordToken(null);

        accountService.updateUserCredentials(creds);

        return ResponseEntity.ok("Password has been successfully changed.");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("🔍 Token username: " + username);

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        if (!BCrypt.checkpw(request.getOldPassword(), creds.getPasswordHash())) {
            throw new BusinessLogicException("Old password is incorrect.");
        }

        String hashedPassword = accountService.encodePassword(request.getNewPassword());
        creds.setPasswordHash(hashedPassword);

        accountService.updateUserCredentials(creds);

        return ResponseEntity.ok("Password updated successfully.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserResponse> getProfileById(@PathVariable Integer id) {
        UserCredentials creds = accountService.getUserCredentialsByUserId(id);
        if (creds == null) {
            throw new ResourceNotFoundException("User or credentials not found for ID: " + id);
        }

        User user = creds.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User or credentials not found for ID: " + id);
        }

        return ResponseEntity.ok(buildUserResponse(creds, user));
    }

    private UserResponse buildUserResponse(UserCredentials creds, User user) {
        UserResponse response = new UserResponse();
        response.setUsername(creds.getUsername());
        if (user.getUserID() != null)
            response.setUserId(user.getUserID().longValue());
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
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Integer id) {
        UserCredentials creds = accountService.getUserCredentialsByUserId(id);
        if (creds == null || creds.getUser() == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = creds.getUser();
        user.setRole("DEACTIVATED");
        accountService.updateUser(user);

        return ResponseEntity.ok("User has been deactivated.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-role/{id}")
    public ResponseEntity<String> updateUserRole(@PathVariable Integer id, @RequestParam String role) {
        UserCredentials creds = accountService.getUserCredentialsByUserId(id);
        if (creds == null || creds.getUser() == null) {
            throw new ResourceNotFoundException("User not found.");
        }

        User user = creds.getUser();
        user.setRole(role.toUpperCase());
        accountService.updateUser(user);

        return ResponseEntity.ok("User role updated to: " + role);
    }
    
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @GetMapping("/patients")
    public ResponseEntity<List<UserResponse>> getAllPatients() {
        List<User> patients = accountService.getAllUsersByRole("PATIENT");
        List<UserResponse> responseList = patients.stream()
            .map(user -> buildUserResponse(accountService.getUserCredentialsByUser(user), user))
            .toList();
        return ResponseEntity.ok(responseList);
    }
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @PostMapping("/register-patient")
    public ResponseEntity<UserResponse> registerPatient(@RequestBody RegisterRequest request) {
        if (request.getFirstName() == null || request.getLastName() == null ||
            request.getEmail() == null || request.getPhoneNumber() == null ||
            request.getNotificationPreference() == null) {
            throw new BusinessLogicException("First name, last name, email, phone number, and notification preference are required.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole("PATIENT");
        user.setNotificationPreference(request.getNotificationPreference());

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(java.sql.Date.valueOf(request.getDateOfBirth()));
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }

        User createdUser = accountService.createUser(user);

        String generatedUsername = accountService.getUserCredentialsByUser(createdUser).getUsername();
        String generatedPassword = createdUser.getTransientPassword();

        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), generatedUsername, generatedPassword, createdUser.getUserID());

        UserResponse response = new UserResponse();
        response.setUserId(createdUser.getUserID().longValue());
        response.setUsername(generatedUsername);
        response.setEmail(createdUser.getEmail());
        response.setGeneratedPassword(generatedPassword);
        response.setFirstName(createdUser.getFirstName());
        response.setLastName(createdUser.getLastName());

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @PutMapping("/update-profile")
    public ResponseEntity<String> updateUserProfile(@RequestBody UpdateProfileRequest request) {
        accountService.updateUserProfile(request);
        return ResponseEntity.ok("User profile updated successfully.");
    }
}