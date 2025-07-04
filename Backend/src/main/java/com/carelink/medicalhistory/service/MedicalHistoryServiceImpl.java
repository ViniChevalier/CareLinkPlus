package com.carelink.medicalhistory.service;

import com.carelink.medicalhistory.entity.MedicalRecord; 
import com.carelink.grpc.medicalhistory.AddMedicalRecordRequest;
import com.carelink.grpc.medicalhistory.AddMedicalRecordResponse;
import com.carelink.grpc.medicalhistory.GetMedicalHistoryRequest;
import com.carelink.grpc.medicalhistory.GetMedicalHistoryResponse;
import com.carelink.grpc.medicalhistory.MedicalHistoryServiceGrpc;
import com.carelink.medicalhistory.repository.MedicalRecordRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalHistoryServiceImpl extends MedicalHistoryServiceGrpc.MedicalHistoryServiceImplBase {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Override
    public void addMedicalRecord(AddMedicalRecordRequest request, StreamObserver<AddMedicalRecordResponse> responseObserver) {
        try {
            MedicalRecord record = new MedicalRecord();
            record.setPatientID(Integer.parseInt(request.getPatientId()));
            record.setDoctorID(0); // Caso precise, pode vir do request ou ser setado corretamente
            record.setRecordDate(Timestamp.valueOf(request.getRecordDate().replace("T", " ").replace("Z", ""))); // Adaptar se vier em ISO
            record.setNotes(request.getDescription());
            record.setPrescriptions(request.getTreatment());

            medicalRecordRepository.save(record);

            AddMedicalRecordResponse response = AddMedicalRecordResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Medical record saved successfully!")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            AddMedicalRecordResponse response = AddMedicalRecordResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error saving medical record: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getMedicalHistory(GetMedicalHistoryRequest request, StreamObserver<GetMedicalHistoryResponse> responseObserver) {
        List<MedicalRecord> recordEntities = medicalRecordRepository.findByPatientID(Integer.parseInt(request.getPatientId()));

        List<com.carelink.grpc.medicalhistory.MedicalRecord> grpcRecords = recordEntities.stream().map(entity -> 
            com.carelink.grpc.medicalhistory.MedicalRecord.newBuilder()
                .setRecordId(String.valueOf(entity.getRecordID()))
                .setPatientId(String.valueOf(entity.getPatientID()))
                .setRecordDate(entity.getRecordDate() != null ? entity.getRecordDate().toString() : "")
                .setDescription(entity.getNotes() != null ? entity.getNotes() : "")
                .setDiagnosis("")
                .setTreatment(entity.getPrescriptions() != null ? entity.getPrescriptions() : "")
                .build()
        ).collect(Collectors.toList());

        GetMedicalHistoryResponse response = GetMedicalHistoryResponse.newBuilder()
                .addAllRecords(grpcRecords)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}