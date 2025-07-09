package com.carelink.medicalhistory.controller;

import com.carelink.medicalhistory.dto.PatientMedicalHistoryDto;
import com.carelink.medicalhistory.entity.PatientMedicalHistory;
import com.carelink.medicalhistory.service.PatientMedicalHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/patient-history")
public class PatientMedicalHistoryController {

    private final PatientMedicalHistoryService service;

    public PatientMedicalHistoryController(PatientMedicalHistoryService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createHistory(@RequestBody PatientMedicalHistoryDto dto) {
        PatientMedicalHistory history = new PatientMedicalHistory();
        history.setPatientID(dto.getPatientId());
        history.setDiagnosis(dto.getDiagnosis());
        history.setDescription(dto.getDescription());
        history.setDiagnosisDate(dto.getDiagnosisDate());
        history.setStatus(dto.getStatus() != null ? dto.getStatus() : "Active");

        PatientMedicalHistory saved = service.save(history);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{historyId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<?> getHistoryById(@PathVariable Integer historyId) {
        Optional<PatientMedicalHistory> optional = service.findById(historyId);
        return optional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<?> getHistoriesByPatient(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.findByPatientId(patientId));
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