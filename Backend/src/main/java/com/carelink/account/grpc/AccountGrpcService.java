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

@Service
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public AccountGrpcService(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
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
                String jwt = jwtUtil.generateToken(request.getUsername(), role);
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
            String subject = "CareLink+ Password Reset Request";
            String body = String.format(
                "Hello %s,\n\nWe received a request to reset your password.\nPlease click the link below to set a new password:\n\n%s\n\nIf you did not request this, please ignore this email.",
                user.getFirstName() != null ? user.getFirstName() : "User",
                resetLink
            );

            accountService.sendEmail(user.getEmail(), subject, body);
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

            String subject = "Your CareLink+ account password has been reset";
            String body = String.format(
                "Hello %s,\n\nYour password has been reset by an administrator.\n\nYour new password is: %s\n\nPlease log in and change your password as soon as possible for security.",
                user.getFirstName() != null ? user.getFirstName() : "User",
                newPassword
            );

            accountService.sendEmail(user.getEmail(), subject, body);
            response.setSuccess(true).setMessage("Password reset successfully and email sent to user");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    private String getUsernameByUser(User user) {
        UserCredentials creds = accountService.getUserCredentialsByUser(user);
        return creds != null ? creds.getUsername() : "";
    }
}