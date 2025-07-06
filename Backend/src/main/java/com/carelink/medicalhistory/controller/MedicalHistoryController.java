package com.carelink.medicalhistory.controller;

import com.carelink.medicalhistory.service.AzureBlobService;
import com.carelink.grpc.medicalhistory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/medical-history")
public class MedicalHistoryController {

    private final MedicalHistoryServiceGrpc.MedicalHistoryServiceBlockingStub grpcStub;
    private final AzureBlobService azureBlobService;

    @Autowired
    public MedicalHistoryController(MedicalHistoryServiceGrpc.MedicalHistoryServiceBlockingStub grpcStub,
                                    AzureBlobService azureBlobService) {
        this.grpcStub = grpcStub;
        this.azureBlobService = azureBlobService;
    }

    @PostMapping
    public ResponseEntity<?> createRecord(
            @RequestParam Integer patientId,
            @RequestParam Integer doctorId,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) String prescriptions,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) Integer historyId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            String fileUrl = "";
            if (file != null && !file.isEmpty()) {
                fileUrl = azureBlobService.uploadFile(file);
            }

            AddMedicalRecordRequest request = AddMedicalRecordRequest.newBuilder()
                    .setPatientId(patientId)
                    .setDoctorId(doctorId)
                    .setNotes(notes != null ? notes : "")
                    .setPrescriptions(prescriptions != null ? prescriptions : "")
                    .setUpdatedBy(updatedBy != null ? updatedBy : "System")
                    .setAttachmentUrl(fileUrl)
                    .setHistoryId(historyId != null ? historyId : 0)
                    .build();

            AddMedicalRecordResponse response = grpcStub.addMedicalRecord(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating record: " + e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getRecordsByPatient(@PathVariable Integer patientId) {
        GetMedicalHistoryRequest request = GetMedicalHistoryRequest.newBuilder()
                .setPatientId(patientId)
                .build();

        GetMedicalHistoryResponse response = grpcStub.getMedicalHistory(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllRecords() {
        GetMedicalHistoryRequest request = GetMedicalHistoryRequest.newBuilder()
                .setPatientId(0) // Busca todos
                .build();

        GetMedicalHistoryResponse response = grpcStub.getMedicalHistory(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<?> getRecordById(@PathVariable Integer recordId) {
        GetMedicalRecordRequest request = GetMedicalRecordRequest.newBuilder()
                .setRecordId(recordId)
                .build();

        GetMedicalRecordResponse response = grpcStub.getMedicalRecord(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateRecord(
            @PathVariable Integer recordId,
            @RequestParam Integer patientId,
            @RequestParam Integer doctorId,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) String prescriptions,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) Integer historyId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            String fileUrl = "";
            if (file != null && !file.isEmpty()) {
                fileUrl = azureBlobService.uploadFile(file);
            }

            UpdateMedicalRecordRequest request = UpdateMedicalRecordRequest.newBuilder()
                    .setRecordId(recordId)
                    .setPatientId(patientId)
                    .setDoctorId(doctorId)
                    .setNotes(notes != null ? notes : "")
                    .setPrescriptions(prescriptions != null ? prescriptions : "")
                    .setUpdatedBy(updatedBy != null ? updatedBy : "System")
                    .setAttachmentUrl(fileUrl)
                    .setHistoryId(historyId != null ? historyId : 0)
                    .build();

            UpdateMedicalRecordResponse response = grpcStub.updateMedicalRecord(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating record: " + e.getMessage());
        }
    }

    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRecord(@PathVariable Integer recordId) {
        DeleteMedicalRecordRequest request = DeleteMedicalRecordRequest.newBuilder()
                .setRecordId(recordId)
                .build();

        DeleteMedicalRecordResponse response = grpcStub.deleteMedicalRecord(request);

        return ResponseEntity.ok(response);
    }
}