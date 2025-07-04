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
import com.carelink.medicalhistory.service.MedicalHistoryServiceImpl;
import com.carelink.medicalhistory.repository.MedicalRecordRepository;
import com.carelink.notification.service.NotificationService;
import com.carelink.notification.controller.NotificationServiceImpl;

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
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalHistoryServiceImpl medicalHistoryServiceImpl;
    private final NotificationService notificationService;
    private final NotificationServiceImpl notificationServiceImpl;

    public CarelinkApplication(AppointmentRepository appointmentRepository,
                               UserRepository userRepository,
                               UserCredentialsRepository credentialsRepository,
                               JwtUtil jwtUtil,
                               Environment environment,
                               MedicalRecordRepository medicalRecordRepository,
                               MedicalHistoryServiceImpl medicalHistoryServiceImpl,
                               NotificationService notificationService,
                               NotificationServiceImpl notificationServiceImpl) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.jwtUtil = jwtUtil;
        this.environment = environment;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalHistoryServiceImpl = medicalHistoryServiceImpl;
        this.notificationService = notificationService;
        this.notificationServiceImpl = notificationServiceImpl;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarelinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (List.of(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        Server server = ServerBuilder.forPort(9090)
                .addService(new AppointmentServiceImpl(appointmentRepository))
                .addService(new AccountServiceImpl(userRepository, credentialsRepository, jwtUtil))
                .addService(medicalHistoryServiceImpl)
                .addService(notificationServiceImpl)
                .build();

        server.start();
        System.out.println("gRPC Server started on port 9090");
        server.awaitTermination();
    }
}