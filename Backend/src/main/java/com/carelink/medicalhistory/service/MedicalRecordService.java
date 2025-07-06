package com.carelink.medicalhistory.service;

import com.carelink.medicalhistory.entity.MedicalRecord;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {
    MedicalRecord save(MedicalRecord record);
    Optional<MedicalRecord> findById(Integer id);
    List<MedicalRecord> findByPatientId(Integer patientId);
    void deleteById(Integer id);
}