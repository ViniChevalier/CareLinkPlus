package com.carelink.medicalhistory.repository;

import com.carelink.medicalhistory.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {
    List<MedicalRecord> findByPatientID(Integer patientID);
}