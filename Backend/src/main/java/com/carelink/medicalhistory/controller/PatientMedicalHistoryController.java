package com.carelink.medicalhistory.controller;

import com.carelink.medicalhistory.dto.PatientMedicalHistoryDto;
import com.carelink.medicalhistory.entity.PatientMedicalHistory;
import com.carelink.medicalhistory.service.AzureBlobService;
import com.carelink.medicalhistory.service.PatientMedicalHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient-history")
public class PatientMedicalHistoryController {

    private final PatientMedicalHistoryService service;
    private final AzureBlobService azureBlobService;

    public PatientMedicalHistoryController(PatientMedicalHistoryService service, AzureBlobService azureBlobService) {
        this.service = service;
        this.azureBlobService = azureBlobService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<PatientMedicalHistory> createHistory(
            @RequestParam Integer patientId,
            @RequestParam Integer doctorId,
            @RequestParam(required = false) String diagnosis,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) Date diagnosisDate,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        String blobName = "";
        if (file != null && !file.isEmpty()) {
            try {
                blobName = azureBlobService.uploadFile(file, "medical-histories");
            } catch (Exception e) {
                return ResponseEntity.status(500).build();
            }
        }

        PatientMedicalHistory history = new PatientMedicalHistory();
        history.setPatientID(patientId);
        history.setDoctorId(doctorId);
        history.setDiagnosis(diagnosis);
        history.setDescription(description);
        history.setDiagnosisDate(diagnosisDate);
        history.setStatus(status != null ? status : "Active");
        history.setAttachmentName(blobName);
        history.setUpdateBy(updatedBy != null ? Integer.valueOf(updatedBy) : 0);

        PatientMedicalHistory saved = service.save(history);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{historyId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<?> getHistoryById(@PathVariable Integer historyId) {
        Optional<PatientMedicalHistory> optional = service.findById(historyId);

        if (optional.isPresent()) {
            PatientMedicalHistory entity = optional.get();
            PatientMedicalHistoryDto dto = new PatientMedicalHistoryDto();
            dto.setHistoryId(entity.getHistoryID());
            dto.setPatientId(entity.getPatientID());
            dto.setDoctorId(entity.getDoctorId());
            dto.setDiagnosis(entity.getDiagnosis());
            dto.setDescription(entity.getDescription());
            dto.setDiagnosisDate(entity.getDiagnosisDate());
            dto.setStatus(entity.getStatus());
            dto.setUpdatedBy(entity.getUpdateBy());
            dto.setAttachmentName(entity.getAttachmentName());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setLastUpdated(entity.getLastUpdated());

            if (entity.getAttachmentName() != null && !entity.getAttachmentName().isEmpty()) {
                String url = azureBlobService.generateSasUrl(entity.getAttachmentName());
                dto.setAttachmentUrl(url);
            }

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<?> getHistoriesByPatient(@PathVariable Integer patientId) {
        var histories = service.findByPatientId(patientId);
        var dtoList = histories.stream().map(entity -> {
            PatientMedicalHistoryDto dto = new PatientMedicalHistoryDto();
            dto.setHistoryId(entity.getHistoryID());
            dto.setPatientId(entity.getPatientID());
            dto.setDoctorId(entity.getDoctorId());
            dto.setDiagnosis(entity.getDiagnosis());
            dto.setDescription(entity.getDescription());
            dto.setDiagnosisDate(entity.getDiagnosisDate());
            dto.setStatus(entity.getStatus());
            dto.setUpdatedBy(entity.getUpdateBy());
            dto.setAttachmentName(entity.getAttachmentName());
            dto.setCreatedAt(entity.getCreatedAt());
            dto.setLastUpdated(entity.getLastUpdated());

            if (entity.getAttachmentName() != null && !entity.getAttachmentName().isEmpty()) {
                String url = azureBlobService.generateSasUrl(entity.getAttachmentName());
                dto.setAttachmentUrl(url);
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{historyId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateHistory(@PathVariable Integer historyId, @RequestBody PatientMedicalHistoryDto dto) {
        Optional<PatientMedicalHistory> optional = service.findById(historyId);

        if (optional.isPresent()) {
            PatientMedicalHistory existing = optional.get();
            if (dto.getDiagnosis() != null) {
                existing.setDiagnosis(dto.getDiagnosis());
            }
            if (dto.getDescription() != null) {
                existing.setDescription(dto.getDescription());
            }
            if (dto.getDiagnosisDate() != null) {
                existing.setDiagnosisDate(dto.getDiagnosisDate());
            }
            if (dto.getStatus() != null) {
                existing.setStatus(dto.getStatus());
            }
            if (dto.getUpdatedBy() != null) {
                existing.setUpdateBy(dto.getUpdatedBy());
            }

            service.save(existing);
            return ResponseEntity.ok(existing);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(@PathVariable Integer historyId) {
        service.deleteById(historyId);
        return ResponseEntity.ok("History deleted successfully");
    }
}