package com.carelink.medicalhistory.controller;

import com.carelink.exception.ResourceNotFoundException;
import com.carelink.exception.BusinessLogicException;

import com.carelink.medicalhistory.dto.MedicalRecordDto;
import com.carelink.medicalhistory.dto.MedicalRecordUpdateDto;
import com.carelink.medicalhistory.entity.MedicalRecord;
import com.carelink.medicalhistory.service.AzureBlobService;
import com.carelink.medicalhistory.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-history")
public class MedicalHistoryController {

    private final MedicalRecordService medicalRecordService;
    private final AzureBlobService azureBlobService;

    public MedicalHistoryController(MedicalRecordService medicalRecordService,
                                    AzureBlobService azureBlobService) {
        this.medicalRecordService = medicalRecordService;
        this.azureBlobService = azureBlobService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<MedicalRecordDto> createRecord(
            @RequestParam Integer patientId,
            @RequestParam Integer doctorId,
            @RequestParam String notes,
            @RequestParam String prescriptions,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) Integer historyId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        String blobName = "";
        if (file != null && !file.isEmpty()) {
            try {
                blobName = azureBlobService.uploadFile(file, "medical-records");
            } catch (Exception e) {
                throw new BusinessLogicException("Failed to upload medical record file.");
            }
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatientId(patientId);
        record.setDoctorId(doctorId);
        record.setNotes(notes);
        record.setPrescriptions(prescriptions);
        record.setUpdatedBy(updatedBy != null ? updatedBy : "System");
        record.setAttachmentName(blobName);
        record.setHistoryId(historyId);

        MedicalRecord saved = medicalRecordService.save(record);
        return ResponseEntity.ok(convertToDto(saved));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<List<MedicalRecordDto>> getRecordsByPatient(@PathVariable Integer patientId) {
        List<MedicalRecordDto> dtos = medicalRecordService.findByPatientId(patientId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MedicalRecordDto>> getAllRecords() {
        List<MedicalRecordDto> dtos = medicalRecordService.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{recordId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    public ResponseEntity<MedicalRecordDto> getRecordById(@PathVariable Integer recordId) {
        MedicalRecord record = medicalRecordService.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + recordId));
        return ResponseEntity.ok(convertToDto(record));
    }

    @PutMapping("/{recordId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<MedicalRecordDto> updateRecord(
        @PathVariable Integer recordId,
        @RequestBody MedicalRecordUpdateDto request
    ) {
        MedicalRecord record = medicalRecordService.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + recordId));

        if (request.getNotes() != null) {
            record.setNotes(request.getNotes());
        }
        if (request.getPrescriptions() != null) {
            record.setPrescriptions(request.getPrescriptions());
        }
        if (request.getUpdatedBy() != null) {
            record.setUpdatedBy(request.getUpdatedBy());
        }
        if (request.getHistoryId() != null) {
            record.setHistoryId(request.getHistoryId());
        }

        MedicalRecord updated = medicalRecordService.save(record);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteRecord(@PathVariable Integer recordId) {
        try {
            medicalRecordService.deleteById(recordId);
            return ResponseEntity.ok("Record deleted successfully.");
        } catch (Exception e) {
            throw new BusinessLogicException("Failed to delete medical record with ID: " + recordId);
        }
    }

    private MedicalRecordDto convertToDto(MedicalRecord record) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setRecordId(record.getRecordId());
        dto.setPatientId(record.getPatientId());
        dto.setDoctorId(record.getDoctorId());
        dto.setNotes(record.getNotes());
        dto.setPrescriptions(record.getPrescriptions());
        dto.setUpdatedBy(record.getUpdatedBy());
        dto.setHistoryId(record.getHistoryId());

        if (record.getAttachmentName() != null && !record.getAttachmentName().isEmpty()) {
            String sasUrl = azureBlobService.generateSasUrl(record.getAttachmentName());
            dto.setAttachmentUrl(sasUrl);
        } else {
            dto.setAttachmentUrl(null);
        }
        dto.setCreatedAt(record.getRecordDate());
        dto.setLastUpdated(record.getLastUpdated());

        return dto;
    }
}