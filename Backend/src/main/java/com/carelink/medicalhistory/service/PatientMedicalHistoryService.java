package com.carelink.medicalhistory.service;

import com.carelink.medicalhistory.entity.PatientMedicalHistory;

import java.util.List;
import java.util.Optional;

public interface PatientMedicalHistoryService {
    PatientMedicalHistory save(PatientMedicalHistory history);
    Optional<PatientMedicalHistory> findById(Integer id);
    List<PatientMedicalHistory> findByPatientId(Integer patientId);
    void deleteById(Integer id);
}