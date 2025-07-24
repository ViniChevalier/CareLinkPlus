package com.carelink.account.controller;

import com.carelink.account.dto.AdminResetDto;
import com.carelink.account.dto.ChangePasswordRequest;
import com.carelink.account.dto.LoginRequest;
import com.carelink.account.dto.RegisterRequest;
import com.carelink.account.dto.RequestResetDto;
import com.carelink.account.dto.UpdatePasswordRequest;
import com.carelink.account.dto.UserResponse;
import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.repository.UserCredentialsRepository;
import com.carelink.account.service.AccountService;
import com.carelink.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserCredentialsRepository userCredentialsRepository;

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public AccountController(AccountService accountService, JwtUtil jwtUtil,
            UserCredentialsRepository userCredentialsRepository) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
        this.userCredentialsRepository = userCredentialsRepository;
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
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof com.carelink.security.CustomUserDetails userDetails) {
                Integer userId = userDetails.getId();

                UserCredentials creds = accountService.getUserCredentialsByUserId(userId);
                if (creds == null || creds.getUser() == null)
                    return ResponseEntity.status(404).body(null);

                return ResponseEntity.ok(buildUserResponse(creds, creds.getUser()));
            }

            return ResponseEntity.status(401).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UserResponse updatedInfo) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            UserCredentials creds = accountService.getUserCredentialsByUsername(username);
            if (creds == null)
                return ResponseEntity.status(404).body(null);

            User user = creds.getUser();
            if (user == null)
                return ResponseEntity.status(404).body(null);

            if (updatedInfo.getEmail() != null)
                user.setEmail(updatedInfo.getEmail());
            if (updatedInfo.getLastName() != null)
                user.setLastName(updatedInfo.getLastName());
            if (updatedInfo.getPhoneNumber() != null)
                user.setPhoneNumber(updatedInfo.getPhoneNumber());
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

    @PostMapping("/reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody RequestResetDto dto) {
        String username = dto.getUsername();
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username must not be empty.");
        }

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null || creds.getUser() == null) {
            return ResponseEntity.ok("If a user with that username exists, a reset link has been sent to their email.");
        }

        User user = creds.getUser();
        String token = java.util.UUID.randomUUID().toString();
        creds.setResetPasswordToken(token);
        accountService.updateUser(user);

        String resetLink = "https://calm-sky-0157a6e03.1.azurestaticapps.net/reset-password.html?token=" + token;

        String subject = "Reset Your Password – CareLink+";
        String htmlBody = String.format(
                """
                        <html>
                            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                                <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="text-align: center;">
                                            <h2 style="color: #2a7ec5;">CareLink+</h2>
                                            <p style="font-size: 18px; color: #333;">Password Reset Request</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <p>Hi %s,</p>
                                            <p>We received a request to reset your password for your CareLink+ account. Click the button below to reset it:</p>
                                            <p style="text-align: center;">
                                                <a href="%s" style="background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">Reset Password</a>
                                            </p>
                                            <p>If the button doesn't work, copy and paste the following link into your browser:</p>
                                            <p><a href="%s">%s</a></p>
                                            <hr style="margin-top: 30px;">
                                            <p style="font-size: 12px; color: #888;">If you didn’t request a password reset, please ignore this email.</p>
                                        </td>
                                    </tr>
                                </table>
                            </body>
                        </html>
                        """,
                user.getFirstName() != null ? user.getFirstName() : "User", resetLink, resetLink, resetLink);

        String plainText = String.format("""
                Hello %s,

                We received a request to reset your password for your CareLink+ account.

                To reset your password, click the link below:
                %s

                If you did not request this, please ignore this email.
                """, user.getFirstName() != null ? user.getFirstName() : "User", resetLink);

        accountService.sendEmail(user.getEmail(), subject, htmlBody, plainText);

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
            return ResponseEntity.status(404).body("User not found.");
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
        String subject = "CareLink+ – Your Password Has Been Reset";

        String htmlBody = String.format(
                """
                        <html>
                            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                                <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="text-align: center;">
                                            <h2 style="color: #2a7ec5;">CareLink+</h2>
                                            <p style="font-size: 18px; color: #333;">Password Reset Notification</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <p>Hi %s,</p>
                                            <p>Your password has been reset by a CareLink+ administrator.</p>
                                            <p><strong>Temporary Password:</strong> <code style="background-color: #eee; padding: 4px 8px; border-radius: 4px;">%s</code></p>
                                            <p style="text-align: center; margin-top: 20px;">
                                                <a href="https://calm-sky-0157a6e03.1.azurestaticapps.net/login"
                                                   style="background-color: #2a7ec5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
                                                   Log In to Your Account
                                                </a>
                                            </p>
                                            <p style="margin-top: 30px;">For your security, please change this password immediately after logging in.</p>
                                            <hr style="margin-top: 30px;">
                                            <p style="font-size: 12px; color: #888;">If you did not expect this email, please contact our support team.</p>
                                        </td>
                                    </tr>
                                </table>
                            </body>
                        </html>
                        """,
                user.getFirstName() != null ? user.getFirstName() : "User",
                generatedPassword);

        String plainText = String.format("""
                Hello %s,

                Your password has been reset by a CareLink+ administrator.

                Temporary Password: %s

                Please log in and change your password immediately for security.

                Log in: https://calm-sky-0157a6e03.1.azurestaticapps.net/login

                If you did not expect this reset, please contact our support team.
                """,
                user.getFirstName() != null ? user.getFirstName() : "User",
                generatedPassword);

        accountService.sendEmail(user.getEmail(), subject, htmlBody, plainText);

        return ResponseEntity.ok("Password reset successfully and email sent to user.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Token must not be empty.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("New password must not be empty.");
        }

        UserCredentials creds = accountService.getUserCredentialsByResetToken(request.getToken());
        if (creds == null) {
            return ResponseEntity.status(404).body("Invalid or expired token.");
        }

        String hashedPassword = accountService.encodePassword(request.getNewPassword());
        creds.setPasswordHash(hashedPassword);
        creds.setResetPasswordToken(null); // Invalidate token

        accountService.updateUserCredentials(creds);

        return ResponseEntity.ok("Password has been successfully changed.");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserCredentials creds = accountService.getUserCredentialsByUsername(username);
        if (creds == null) {
            return ResponseEntity.status(404).body("User not found.");
        }

        if (!BCrypt.checkpw(request.getOldPassword(), creds.getPasswordHash())) {
            return ResponseEntity.status(401).body("Old password is incorrect.");
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
            return ResponseEntity.status(404).body(null);
        }

        User user = creds.getUser();
        if (user == null) {
            return ResponseEntity.status(404).body(null);
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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable Integer id) {
        UserCredentials creds = accountService.getUserCredentialsByUserId(id);
        if (creds == null || creds.getUser() == null) {
            return ResponseEntity.status(404).body("User not found.");
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
            return ResponseEntity.status(404).body("User not found.");
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
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(java.sql.Date.valueOf(request.getDateOfBirth()));
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setNotificationPreference(request.getNotificationPreference());
        user.setRole("PATIENT");

        User createdUser = accountService.createUser(user);

        String generatedUsername = accountService.getUserCredentialsByUser(createdUser).getUsername();
        String generatedPassword = createdUser.getTransientPassword();

        // Envia e-mail de boas-vindas
        String subject = "Welcome to CareLink+ – Your Access Details";
        String body = String.format("""
            Hello %s,

            Your account has been created successfully.

            Username: %s
            Temporary Password: %s

            Please log in and change your password as soon as possible.

            Access the platform at: https://calm-sky-0157a6e03.1.azurestaticapps.net/login
            """, user.getFirstName(), generatedUsername, generatedPassword);

        accountService.sendEmail(user.getEmail(), subject, body, body);

        UserResponse response = new UserResponse();
        response.setUserId(createdUser.getUserID().longValue());
        response.setUsername(generatedUsername);
        response.setEmail(createdUser.getEmail());
        response.setGeneratedPassword(generatedPassword);
        response.setFirstName(createdUser.getFirstName());
        response.setLastName(createdUser.getLastName());

        return ResponseEntity.status(201).body(response);
    }
}