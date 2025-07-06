package com.carelink.medicalhistory.service;

import com.carelink.medicalhistory.entity.PatientMedicalHistory;
import com.carelink.medicalhistory.repository.PatientMedicalHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientMedicalHistoryServiceImpl implements PatientMedicalHistoryService {

    private final PatientMedicalHistoryRepository repository;

    public PatientMedicalHistoryServiceImpl(PatientMedicalHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public PatientMedicalHistory save(PatientMedicalHistory history) {
        return repository.save(history);
    }

    @Override
    public Optional<PatientMedicalHistory> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<PatientMedicalHistory> findByPatientId(Integer patientId) {
        return repository.findByPatientID(patientId);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}