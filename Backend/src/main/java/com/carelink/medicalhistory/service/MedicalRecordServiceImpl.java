package com.carelink.medicalhistory.service;

import com.carelink.medicalhistory.entity.MedicalRecord;
import com.carelink.medicalhistory.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository repository;

    public MedicalRecordServiceImpl(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public MedicalRecord save(MedicalRecord record) {
        return repository.save(record);
    }

    @Override
    public Optional<MedicalRecord> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<MedicalRecord> findByPatientId(Integer patientId) {
        return repository.findByPatientId(patientId);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}