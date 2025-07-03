package com.carelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.carelink.account.service.AccountServiceImpl;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.appointment.service.AppointmentServiceImpl;
import com.carelink.account.repository.*;
import com.carelink.security.JwtUtil;

import io.grpc.Server;
import io.grpc.ServerBuilder;

@SpringBootApplication
public class CarelinkApplication implements org.springframework.boot.CommandLineRunner {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final JwtUtil jwtUtil;

    public CarelinkApplication(AppointmentRepository appointmentRepository,
                               UserRepository userRepository,
                               UserCredentialsRepository credentialsRepository,
                               JwtUtil jwtUtil) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.jwtUtil = jwtUtil;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarelinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Server server = ServerBuilder.forPort(9090)
                .addService(new AppointmentServiceImpl(appointmentRepository))
                .addService(new AccountServiceImpl(userRepository, credentialsRepository, jwtUtil))
                .build();

        server.start();
        System.out.println("gRPC Server started on port 9090");
        server.awaitTermination();
    }
}