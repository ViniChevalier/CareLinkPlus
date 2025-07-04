package com.carelink.medicalhistory.repository;

import com.carelink.medicalhistory.entity.PatientMedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientMedicalHistoryRepository extends JpaRepository<PatientMedicalHistory, Integer> {
    List<PatientMedicalHistory> findByPatientID(Integer patientID);
}