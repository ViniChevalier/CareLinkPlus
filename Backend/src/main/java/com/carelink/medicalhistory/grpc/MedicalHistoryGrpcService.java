package com.carelink.medicalhistory.grpc;

import com.carelink.grpc.medicalhistory.*;
import com.carelink.medicalhistory.entity.MedicalRecord;
import com.carelink.medicalhistory.service.MedicalRecordService;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;

public class MedicalHistoryGrpcService extends MedicalHistoryServiceGrpc.MedicalHistoryServiceImplBase {

    private final MedicalRecordService medicalRecordService;

    public MedicalHistoryGrpcService(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @Override
    public void addMedicalRecord(AddMedicalRecordRequest request, StreamObserver<AddMedicalRecordResponse> responseObserver) {
        MedicalRecord record = new MedicalRecord();
        record.setPatientId(request.getPatientId());
        record.setDoctorId(request.getDoctorId());
        record.setNotes(request.getNotes());
        record.setPrescriptions(request.getPrescriptions());
        record.setUpdatedBy(request.getUpdatedBy());
        record.setAttachmentName(request.getAttachmentUrl());
        record.setHistoryId(request.getHistoryId() > 0 ? request.getHistoryId() : null);

        MedicalRecord savedRecord = medicalRecordService.save(record);

        AddMedicalRecordResponse response = AddMedicalRecordResponse.newBuilder()
                .setRecordId(savedRecord.getRecordId())
                .setMessage("Medical record created successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMedicalRecord(GetMedicalRecordRequest request, StreamObserver<GetMedicalRecordResponse> responseObserver) {
        Optional<MedicalRecord> optionalRecord = medicalRecordService.findById(request.getRecordId());

        if (optionalRecord.isPresent()) {
            MedicalRecord record = optionalRecord.get();
            GetMedicalRecordResponse response = GetMedicalRecordResponse.newBuilder()
                    .setRecordId(record.getRecordId())
                    .setPatientId(record.getPatientId())
                    .setDoctorId(record.getDoctorId())
                    .setNotes(record.getNotes())
                    .setPrescriptions(record.getPrescriptions())
                    .setUpdatedBy(record.getUpdatedBy() != null ? record.getUpdatedBy() : "")
                    .setAttachmentUrl(record.getAttachmentName() != null ? record.getAttachmentName() : "")
                    .setHistoryId(record.getHistoryId() != null ? record.getHistoryId() : 0)
                    .build();
            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new RuntimeException("Record not found"));
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void getMedicalHistory(GetMedicalHistoryRequest request, StreamObserver<GetMedicalHistoryResponse> responseObserver) {
        List<MedicalRecord> records = medicalRecordService.findByPatientId(request.getPatientId());

        GetMedicalHistoryResponse.Builder responseBuilder = GetMedicalHistoryResponse.newBuilder();

        for (MedicalRecord record : records) {
            MedicalRecordMessage msg = MedicalRecordMessage.newBuilder()
                    .setRecordId(record.getRecordId())
                    .setPatientId(record.getPatientId())
                    .setDoctorId(record.getDoctorId())
                    .setNotes(record.getNotes())
                    .setPrescriptions(record.getPrescriptions())
                    .setUpdatedBy(record.getUpdatedBy() != null ? record.getUpdatedBy() : "")
                    .setAttachmentUrl(record.getAttachmentName() != null ? record.getAttachmentName() : "")
                    .setHistoryId(record.getHistoryId() != null ? record.getHistoryId() : 0)
                    .build();
            responseBuilder.addRecords(msg);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateMedicalRecord(UpdateMedicalRecordRequest request, StreamObserver<UpdateMedicalRecordResponse> responseObserver) {
        Optional<MedicalRecord> optionalRecord = medicalRecordService.findById(request.getRecordId());

        if (optionalRecord.isPresent()) {
            MedicalRecord record = optionalRecord.get();
            record.setPatientId(request.getPatientId());
            record.setDoctorId(request.getDoctorId());
            record.setNotes(request.getNotes());
            record.setPrescriptions(request.getPrescriptions());
            record.setUpdatedBy(request.getUpdatedBy());
            record.setAttachmentName(request.getAttachmentUrl());
            record.setHistoryId(request.getHistoryId() > 0 ? request.getHistoryId() : null);

            medicalRecordService.save(record);

            UpdateMedicalRecordResponse response = UpdateMedicalRecordResponse.newBuilder()
                    .setMessage("Medical record updated successfully")
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new RuntimeException("Record not found"));
            return;
        }

        responseObserver.onCompleted();
    }

    @Override
    public void deleteMedicalRecord(DeleteMedicalRecordRequest request, StreamObserver<DeleteMedicalRecordResponse> responseObserver) {
        Optional<MedicalRecord> optionalRecord = medicalRecordService.findById(request.getRecordId());

        if (optionalRecord.isPresent()) {
            medicalRecordService.deleteById(request.getRecordId());

            DeleteMedicalRecordResponse response = DeleteMedicalRecordResponse.newBuilder()
                    .setMessage("Medical record deleted successfully")
                    .build();

            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new RuntimeException("Record not found"));
            return;
        }

        responseObserver.onCompleted();
    }
}