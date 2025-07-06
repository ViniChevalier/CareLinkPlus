package com.carelink.config;

import com.carelink.grpc.medicalhistory.MedicalHistoryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ManagedChannel medicalHistoryChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }

    @Bean
    public MedicalHistoryServiceGrpc.MedicalHistoryServiceBlockingStub medicalHistoryServiceStub(ManagedChannel medicalHistoryChannel) {
        return MedicalHistoryServiceGrpc.newBlockingStub(medicalHistoryChannel);
    }
}