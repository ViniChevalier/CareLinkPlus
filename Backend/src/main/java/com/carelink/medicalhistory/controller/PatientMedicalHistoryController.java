package com.carelink.medicalhistory.controller;

import com.carelink.medicalhistory.dto.PatientMedicalHistoryDto;
import com.carelink.medicalhistory.dto.PatientMedicalHistoryForm;
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
    public ResponseEntity<PatientMedicalHistoryDto> createHistory(
            @RequestParam Integer patientId,
            @RequestParam Integer doctorId,
            @RequestParam String diagnosis,
            @RequestParam String description,
            @RequestParam String diagnosisDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String updatedBy,
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
        history.setStatus(status != null ? status : "Active");
        history.setAttachmentName(blobName);

        try {
            if (diagnosisDate != null && !diagnosisDate.trim().isEmpty() && !"undefined".equalsIgnoreCase(diagnosisDate)) {
                history.setDiagnosisDate(Date.valueOf(diagnosisDate.trim()));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid date format: " + diagnosisDate);
            return ResponseEntity.badRequest().build();
        }

        if (updatedBy != null && !updatedBy.trim().isEmpty()) {
            try {
                history.setUpdateBy(Integer.parseInt(updatedBy.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Invalid updatedBy: " + updatedBy);
                return ResponseEntity.badRequest().build();
            }
        } else {
            history.setUpdateBy(null);
        }

        PatientMedicalHistory saved = service.save(history);

        PatientMedicalHistoryDto dto = new PatientMedicalHistoryDto();
        dto.setHistoryId(saved.getHistoryID());
        dto.setPatientId(saved.getPatientID());
        dto.setDoctorId(saved.getDoctorId());
        dto.setDiagnosis(saved.getDiagnosis());
        dto.setDescription(saved.getDescription());
        dto.setDiagnosisDate(saved.getDiagnosisDate());
        dto.setStatus(saved.getStatus());
        dto.setUpdatedBy(saved.getUpdateBy());
        dto.setAttachmentName(saved.getAttachmentName());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setLastUpdated(saved.getLastUpdated());

        if (saved.getAttachmentName() != null && !saved.getAttachmentName().isEmpty()) {
            dto.setAttachmentUrl(azureBlobService.generateSasUrl(saved.getAttachmentName()));
        }

        return ResponseEntity.ok(dto);
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
    public ResponseEntity<PatientMedicalHistoryDto> updateHistory(
            @PathVariable Integer historyId,
            @RequestParam(required = false) String diagnosis,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String diagnosisDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = true) String updatedBy,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Optional<PatientMedicalHistory> optional = service.findById(historyId);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PatientMedicalHistory existing = optional.get();

        if (diagnosis != null) {
            existing.setDiagnosis(diagnosis);
        }
        if (description != null) {
            existing.setDescription(description);
        }
        if (diagnosisDate != null && !diagnosisDate.trim().isEmpty() && !"undefined".equalsIgnoreCase(diagnosisDate)) {
            try {
                existing.setDiagnosisDate(Date.valueOf(diagnosisDate.trim()));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid date format: " + diagnosisDate);
                return ResponseEntity.badRequest().build();
            }
        }
        if (status != null) {
            existing.setStatus(status);
        }
        if (updatedBy != null) {
            try {
                existing.setUpdateBy(Integer.parseInt(updatedBy.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Invalid updatedBy: " + updatedBy);
                return ResponseEntity.badRequest().build();
            }
        }

        if (file != null && !file.isEmpty()) {
            try {
                String blobName = azureBlobService.uploadFile(file, "medical-histories");
                existing.setAttachmentName(blobName);
            } catch (Exception e) {
                return ResponseEntity.status(500).build();
            }
        }

        PatientMedicalHistory saved = service.save(existing);

        PatientMedicalHistoryDto dto = new PatientMedicalHistoryDto();
        dto.setHistoryId(saved.getHistoryID());
        dto.setPatientId(saved.getPatientID());
        dto.setDoctorId(saved.getDoctorId());
        dto.setDiagnosis(saved.getDiagnosis());
        dto.setDescription(saved.getDescription());
        dto.setDiagnosisDate(saved.getDiagnosisDate());
        dto.setStatus(saved.getStatus());
        dto.setUpdatedBy(saved.getUpdateBy());
        dto.setAttachmentName(saved.getAttachmentName());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setLastUpdated(saved.getLastUpdated());

        if (saved.getAttachmentName() != null && !saved.getAttachmentName().isEmpty()) {
            dto.setAttachmentUrl(azureBlobService.generateSasUrl(saved.getAttachmentName()));
        }

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(@PathVariable Integer historyId) {
        service.deleteById(historyId);
        return ResponseEntity.ok("History deleted successfully");
    }
}