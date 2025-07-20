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
    public ResponseEntity<PatientMedicalHistory> createHistory(@ModelAttribute PatientMedicalHistoryForm form) {
        System.out.println("POST recebido com form: " + form);
        String blobName = "";
        MultipartFile file = form.getFile();

        if (file != null && !file.isEmpty()) {
            try {
                blobName = azureBlobService.uploadFile(file, "medical-histories");
            } catch (Exception e) {
                return ResponseEntity.status(500).build();
            }
        }

        PatientMedicalHistory history = new PatientMedicalHistory();

        // Debug logs for raw form values
        System.out.println("Raw patientId: '" + form.getPatientId() + "'");
        System.out.println("Raw doctorId: '" + form.getDoctorId() + "'");
        System.out.println("Raw updatedBy: '" + form.getUpdatedBy() + "'");
        System.out.println("Raw diagnosisDate: '" + form.getDiagnosisDate() + "'");

        Integer patientId = null;
        try {
            patientId = Integer.parseInt(Optional.ofNullable(form.getPatientId()).orElse("").trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid patientId: " + form.getPatientId());
            return ResponseEntity.badRequest().build();
        }

        Integer doctorId = null;
        try {
            doctorId = Integer.parseInt(Optional.ofNullable(form.getDoctorId()).orElse("").trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid doctorId: " + form.getDoctorId());
            return ResponseEntity.badRequest().build();
        }

        Integer updatedBy = null;
        try {
            updatedBy = Integer.parseInt(Optional.ofNullable(form.getUpdatedBy()).orElse("").trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid updatedBy: " + form.getUpdatedBy());
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Parsed values => patientId: " + patientId + ", doctorId: " + doctorId + ", updatedBy: " + updatedBy);

        java.sql.Date diagnosisDate = null;
        String diagnosisDateStr = form.getDiagnosisDate();
        if (diagnosisDateStr != null && !diagnosisDateStr.trim().isEmpty() && !"undefined".equals(diagnosisDateStr.trim())) {
            try {
                diagnosisDate = java.sql.Date.valueOf(diagnosisDateStr.trim());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid date format: " + diagnosisDateStr);
                return ResponseEntity.badRequest().build();
            }
        }

        // Set parsed values
        history.setPatientID(patientId);
        history.setDoctorId(doctorId);
        history.setUpdateBy(updatedBy != null ? updatedBy : 0);
        history.setDiagnosisDate(diagnosisDate);

        history.setDiagnosis(form.getDiagnosis());
        history.setDescription(form.getDescription());
        history.setStatus(form.getStatus() != null ? form.getStatus() : "Active");
        history.setAttachmentName(blobName);

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