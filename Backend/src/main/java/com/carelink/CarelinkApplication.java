package com.carelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import com.carelink.appointment.grpc.AppointmentGrpcService;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.appointment.service.AppointmentServiceImpl;
import com.carelink.account.repository.UserRepository;
import com.carelink.account.grpc.AccountGrpcService;
import com.carelink.account.repository.UserCredentialsRepository;
import com.carelink.security.JwtUtil;
import com.carelink.medicalhistory.service.MedicalRecordServiceImpl;
import com.carelink.medicalhistory.grpc.MedicalHistoryGrpcService;
import com.carelink.medicalhistory.repository.MedicalRecordRepository;
import com.carelink.notification.service.NotificationService;
import com.carelink.notification.service.NotificationServiceImpl;
import com.carelink.notification.grpc.NotificationGrpcService;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.util.List;

@SpringBootApplication
public class CarelinkApplication implements CommandLineRunner {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCredentialsRepository credentialsRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Environment environment;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private MedicalRecordServiceImpl medicalRecordServiceImpl;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationServiceImpl notificationServiceImpl;

    @Autowired
    private AccountGrpcService accountGrpcService;

    @Autowired
    private AppointmentServiceImpl appointmentServiceImpl;

    public static void main(String[] args) {
        SpringApplication.run(CarelinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (List.of(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        Server server = ServerBuilder.forPort(9090)
                .addService(new AppointmentGrpcService(appointmentServiceImpl))
                .addService(accountGrpcService)
                .addService(new MedicalHistoryGrpcService(medicalRecordServiceImpl))
                .addService(new NotificationGrpcService(notificationServiceImpl))
                .build();

        server.start();
        System.out.println("gRPC Server started on port 9090");
        server.awaitTermination();
    }
}