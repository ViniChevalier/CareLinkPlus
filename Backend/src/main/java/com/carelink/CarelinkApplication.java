package com.carelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import com.carelink.account.service.AccountServiceImpl;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.appointment.service.AppointmentServiceImpl;
import com.carelink.account.repository.UserRepository;
import com.carelink.account.repository.UserCredentialsRepository;
import com.carelink.security.JwtUtil;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.List;

@SpringBootApplication
public class CarelinkApplication implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final JwtUtil jwtUtil;
    private final Environment environment;

    public CarelinkApplication(AppointmentRepository appointmentRepository,
                               UserRepository userRepository,
                               UserCredentialsRepository credentialsRepository,
                               JwtUtil jwtUtil,
                               Environment environment) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.jwtUtil = jwtUtil;
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarelinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Checa se profile "test" está ativo
        if (List.of(environment.getActiveProfiles()).contains("test")) {
            return; // Não inicializa gRPC no teste
        }

        Server server = ServerBuilder.forPort(9090)
                .addService(new AppointmentServiceImpl(appointmentRepository))
                .addService(new AccountServiceImpl(userRepository, credentialsRepository, jwtUtil))
                .build();

        server.start();
        System.out.println("gRPC Server started on port 9090");
        server.awaitTermination();
    }
}