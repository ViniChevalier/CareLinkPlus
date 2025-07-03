package com.carelink.appointment.repository;

import com.carelink.appointment.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {
    List<AppointmentEntity> findByPatientIdOrDoctorId(Integer patientId, Integer doctorId);
}