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
            String jwt = jwtUtil.generateToken(request.getUsername());
            response.setSuccess(true).setJwtToken(jwt).setMessage("Login successful");
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

    private String getUsernameByUser(User user) {
        UserCredentials creds = accountService.getUserCredentialsByUser(user);
        return creds != null ? creds.getUsername() : "";
    }
}