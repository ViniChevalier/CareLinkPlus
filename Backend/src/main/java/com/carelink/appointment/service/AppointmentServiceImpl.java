package com.carelink.appointment.service;

import com.carelink.appointment.dto.AppointmentRequestDTO;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl {

        private final AppointmentRepository appointmentRepository;

        public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
                this.appointmentRepository = appointmentRepository;
        }

        public AppointmentEntity createAppointment(AppointmentRequestDTO dto) {
                AppointmentEntity appointment = new AppointmentEntity();
                appointment.setPatientId(dto.getPatientId() != null ? dto.getPatientId().intValue() : null);
                appointment.setDoctorId(dto.getDoctorId() != null ? dto.getDoctorId().intValue() : null);
                appointment.setAppointmentDateTime(dto.getAppointmentDate());
                appointment.setReason(dto.getReason());
                return appointmentRepository.save(appointment);
        }

        public List<AppointmentEntity> getAllAppointments() {
                return appointmentRepository.findAll();
        }

        public List<AppointmentEntity> getAppointmentsByPatient(Long patientId) {
                return appointmentRepository.findByPatientId(patientId);
        }

        public List<AppointmentEntity> getAppointmentsByDoctor(Long doctorId) {
                return appointmentRepository.findByDoctorId(doctorId);
        }

        public AppointmentEntity updateAppointment(Long id, AppointmentRequestDTO dto) {
                AppointmentEntity appointment = appointmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Appointment not found"));

                appointment.setAppointmentDateTime(dto.getAppointmentDate());
                appointment.setReason(dto.getReason());
                return appointmentRepository.save(appointment);
        }

        public void deleteAppointment(Long id) {
                appointmentRepository.deleteById(id);
        }

        public AppointmentEntity createAppointmentFromEntity(AppointmentEntity entity) {
                return appointmentRepository.save(entity);
        }

        public AppointmentEntity getAppointmentById(Long id) {
                return appointmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        }

        public AppointmentEntity updateAppointment(Long id, LocalDateTime appointmentDate, String reason) {
                AppointmentEntity appointment = getAppointmentById(id);
                if (appointmentDate != null) {
                        appointment.setAppointmentDateTime(appointmentDate);
                }
                if (reason != null) {
                        appointment.setReason(reason);
                }
                return appointmentRepository.save(appointment);
        }
}