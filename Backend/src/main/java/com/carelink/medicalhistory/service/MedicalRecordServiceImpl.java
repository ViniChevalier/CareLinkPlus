package com.carelink.medicalhistory.service;

import com.carelink.medicalhistory.entity.MedicalRecord;
import com.carelink.medicalhistory.repository.MedicalRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public MedicalRecord save(MedicalRecord record) {
        return medicalRecordRepository.save(record);
    }

    @Override
    public Optional<MedicalRecord> findById(Integer recordId) {
        return medicalRecordRepository.findById(recordId);
    }

    @Override
    public List<MedicalRecord> findByPatientId(Integer patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    @Override
    public void deleteById(Integer recordId) {
        medicalRecordRepository.deleteById(recordId);
    }

    @Override
    public List<MedicalRecord> findAll() {
        return medicalRecordRepository.findAll();
    }

}