package com.carelink.account.grpc;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.service.AccountService;
import com.carelink.grpc.account.AccountServiceGrpc;
import com.carelink.grpc.account.LoginRequest;
import com.carelink.grpc.account.LoginResponse;
import com.carelink.grpc.account.RegisterUserRequest;
import com.carelink.grpc.account.RegisterUserResponse;
import com.carelink.grpc.account.UserProfileRequest;
import com.carelink.grpc.account.UserProfileResponse;
import com.carelink.security.JwtUtil;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import com.carelink.grpc.account.ChangePasswordRequest;
import com.carelink.grpc.account.ChangePasswordResponse;
import com.carelink.grpc.account.UpdatePasswordRequest;
import com.carelink.grpc.account.UpdatePasswordResponse;
import org.springframework.security.crypto.bcrypt.BCrypt;
import com.carelink.notification.service.NotificationService;

@Service
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    public AccountGrpcService(AccountService accountService, JwtUtil jwtUtil, NotificationService notificationService) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
        this.notificationService = notificationService;
    }

    @Override
    public void registerUser(RegisterUserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {
        RegisterUserResponse.Builder response = RegisterUserResponse.newBuilder();

        User user = new User();
        String[] nameParts = request.getFullName().split(" ", 2);
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setRole(request.getRole());

        try {
            accountService.createUser(user);
            response.setSuccess(true).setMessage("User registered successfully");
        } catch (Exception e) {
            response.setSuccess(false).setMessage("Error: " + e.getMessage());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void loginUser(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        LoginResponse.Builder response = LoginResponse.newBuilder();

        boolean valid = accountService.validateLogin(request.getUsername(), request.getPassword());
        if (!valid) {
            response.setSuccess(false).setMessage("Invalid username or password");
        } else {
            UserCredentials creds = accountService.getUserCredentialsByUsername(request.getUsername());
            if (creds == null || creds.getUser() == null) {
                response.setSuccess(false).setMessage("User not found");
            } else {
                String role = creds.getUser().getRole();
                String jwt = jwtUtil.generateToken(creds.getUser().getUserID(), request.getUsername(), role);
                response.setSuccess(true).setJwtToken(jwt).setMessage("Login successful");
            }
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        User user = accountService.getUserById(Integer.parseInt(request.getUserId()));
        UserProfileResponse.Builder response = UserProfileResponse.newBuilder();

        if (user == null) {
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
            return;
        }

        response.setUserId(String.valueOf(user.getUserID()))
                .setUsername(getUsernameByUser(user))
                .setFullName(user.getFirstName() + " " + user.getLastName())
                .setEmail(user.getEmail())
                .setPhone(user.getPhoneNumber())
                .setRole(user.getRole());

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void requestPasswordReset(com.carelink.grpc.account.RequestPasswordResetRequest request, StreamObserver<com.carelink.grpc.account.RequestPasswordResetResponse> responseObserver) {
        com.carelink.grpc.account.RequestPasswordResetResponse.Builder response = com.carelink.grpc.account.RequestPasswordResetResponse.newBuilder();
        UserCredentials creds = accountService.getUserCredentialsByUsername(request.getUsername());

        if (creds == null || creds.getUser() == null) {
            response.setSuccess(false).setMessage("User not found");
        } else {
            User user = creds.getUser();
            String token = java.util.UUID.randomUUID().toString();
            creds.setResetPasswordToken(token);
            creds.setTokenExpiration(new java.sql.Timestamp(System.currentTimeMillis() + (60 * 60 * 1000))); // 1 hour

            accountService.updateUser(user);

            String resetLink = "https://yourfrontend.com/reset-password?token=" + token;
            String subject = "Reset Your Password – CareLink+";

            String htmlBody = String.format("""
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
                user.getFirstName() != null ? user.getFirstName() : "User", resetLink, resetLink, resetLink
            );

            String plainText = String.format("""
                Hello %s,

                We received a request to reset your password for your CareLink+ account.

                To reset your password, click the link below:
                %s

                If you did not request this, please ignore this email.
                """,
                user.getFirstName() != null ? user.getFirstName() : "User", resetLink
            );

             notificationService.sendEmail(user.getEmail(), subject, htmlBody, plainText, user.getUserID());
            response.setSuccess(true).setMessage("Password reset link sent to email");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void adminResetPassword(com.carelink.grpc.account.AdminResetPasswordRequest request, StreamObserver<com.carelink.grpc.account.AdminResetPasswordResponse> responseObserver) {
        com.carelink.grpc.account.AdminResetPasswordResponse.Builder response = com.carelink.grpc.account.AdminResetPasswordResponse.newBuilder();
        UserCredentials creds = accountService.getUserCredentialsByUsername(request.getUsername());

        if (creds == null || creds.getUser() == null) {
            response.setSuccess(false).setMessage("User not found");
        } else {
            User user = creds.getUser();
            String newPassword = request.getNewPassword() != null && !request.getNewPassword().isEmpty()
                ? request.getNewPassword()
                : java.util.UUID.randomUUID().toString().substring(0, 8);

            String hashed = accountService.encodePassword(newPassword);
            creds.setPasswordHash(hashed);
            creds.setResetPasswordToken(null);
            creds.setTokenExpiration(null);

            accountService.updateUserCredentials(creds);
            accountService.updateUser(user);

            String subject = "CareLink+ – Your Password Has Been Reset";

            String htmlBody = String.format("""
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
                user.getFirstName() != null ? user.getFirstName() : "User", newPassword
            );

            String plainText = String.format("""
                Hello %s,

                Your password has been reset by a CareLink+ administrator.

                Temporary Password: %s

                Please log in and change your password immediately for security.

                Log in: https://calm-sky-0157a6e03.1.azurestaticapps.net/login

                If you did not expect this reset, please contact our support team.
                """,
                user.getFirstName() != null ? user.getFirstName() : "User", newPassword
            );

             notificationService.sendEmail(user.getEmail(), subject, htmlBody, plainText, user.getUserID());
            response.setSuccess(true).setMessage("Password reset successfully and email sent to user");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        ChangePasswordResponse.Builder response = ChangePasswordResponse.newBuilder();

        if (request.getToken() == null || request.getToken().isEmpty()) {
            response.setSuccess(false).setMessage("Token must not be empty.");
        } else if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            response.setSuccess(false).setMessage("New password must not be empty.");
        } else {
            UserCredentials creds = accountService.getUserCredentialsByResetToken(request.getToken());
            if (creds == null) {
                response.setSuccess(false).setMessage("Invalid or expired token.");
            } else {
                String hashedPassword = accountService.encodePassword(request.getNewPassword());
                creds.setPasswordHash(hashedPassword);
                creds.setResetPasswordToken(null);
                creds.setTokenExpiration(null);

                accountService.updateUserCredentials(creds);
                response.setSuccess(true).setMessage("Password has been successfully changed.");
            }
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request, StreamObserver<UpdatePasswordResponse> responseObserver) {
        UpdatePasswordResponse.Builder response = UpdatePasswordResponse.newBuilder();

        UserCredentials creds = accountService.getUserCredentialsByUsername(request.getUsername());
        if (creds == null) {
            response.setSuccess(false).setMessage("User not found.");
        } else if (request.getOldPassword() == null || request.getOldPassword().isEmpty() ||
                   request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            response.setSuccess(false).setMessage("Old and new passwords must not be empty.");
        } else if (!BCrypt.checkpw(request.getOldPassword(), creds.getPasswordHash())) {
            response.setSuccess(false).setMessage("Old password is incorrect.");
        } else {
            String hashedPassword = accountService.encodePassword(request.getNewPassword());
            creds.setPasswordHash(hashedPassword);

            accountService.updateUserCredentials(creds);
            response.setSuccess(true).setMessage("Password updated successfully.");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private String getUsernameByUser(User user) {
        UserCredentials creds = accountService.getUserCredentialsByUser(user);
        return creds != null ? creds.getUsername() : "";
    }
}