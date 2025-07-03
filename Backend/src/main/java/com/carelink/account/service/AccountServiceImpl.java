package com.carelink.account.service;

import com.carelink.account.model.User;
import com.carelink.account.model.UserCredentials;
import com.carelink.account.repository.UserCredentialsRepository;
import com.carelink.account.repository.UserRepository;
import com.carelink.grpc.account.AccountServiceGrpc;
import com.carelink.grpc.account.LoginRequest;
import com.carelink.grpc.account.LoginResponse;
import com.carelink.grpc.account.RegisterUserRequest;
import com.carelink.grpc.account.RegisterUserResponse;
import com.carelink.grpc.account.UserProfileRequest;
import com.carelink.grpc.account.UserProfileResponse;
import com.carelink.security.JwtUtil;
import io.grpc.stub.StreamObserver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {

    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountServiceImpl(UserRepository userRepository, UserCredentialsRepository credentialsRepository,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.jwtUtil = jwtUtil;
    }

    public AccountServiceImpl() {
        this.userRepository = null;
        this.credentialsRepository = null;
        this.jwtUtil = null;
    }

    @Override
    public void registerUser(RegisterUserRequest request, StreamObserver<RegisterUserResponse> responseObserver) {
        RegisterUserResponse.Builder response = RegisterUserResponse.newBuilder();

        User existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser != null) {
            response.setSuccess(false).setMessage("Email already registered");
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
            return;
        }

        UserCredentials existingCreds = credentialsRepository.findByUsername(request.getUsername());
        if (existingCreds != null) {
            response.setSuccess(false).setMessage("Username already taken");
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
            return;
        }

        User user = new User();
        // Se request tiver fullName, separar em firstName e lastName
        String[] nameParts = request.getFullName().split(" ", 2);
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone());
        user.setRole(request.getRole());
        userRepository.save(user);

        UserCredentials credentials = new UserCredentials();
        credentials.setUsername(request.getUsername());
        credentials.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        credentials.setUser(user);
        credentialsRepository.save(credentials);

        response.setSuccess(true).setMessage("User registered successfully");
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void loginUser(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        LoginResponse.Builder response = LoginResponse.newBuilder();

        UserCredentials credentials = credentialsRepository.findByUsername(request.getUsername());
        if (credentials == null || !passwordEncoder.matches(request.getPassword(), credentials.getPasswordHash())) {
            response.setSuccess(false).setMessage("Invalid username or password");
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
            return;
        }

        String jwt = jwtUtil.generateToken(credentials.getUsername());

        response.setSuccess(true).setJwtToken(jwt).setMessage("Login successful");
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        User user = userRepository.findById(Integer.parseInt(request.getUserId())).orElse(null);
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
        UserCredentials credentials = credentialsRepository.findByUser(user);
        return credentials != null ? credentials.getUsername() : "";
    }
}