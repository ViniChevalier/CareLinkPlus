package com.carelink.appointment.service;

import com.carelink.appointment.model.AvailabilityStatus;

import com.carelink.appointment.dto.AppointmentRequestDTO;
import com.carelink.appointment.dto.AppointmentWithPatientDTO;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.appointment.repository.DoctorAvailabilityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }


    public AppointmentEntity createAppointment(AppointmentRequestDTO dto) {
        Integer availabilityIdInt = dto.getAvailabilityId() != null ? dto.getAvailabilityId().intValue() : null;

        DoctorAvailability availabilitySlot = doctorAvailabilityRepository.findById(availabilityIdInt)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        if (availabilitySlot.getIsBooked()) {
            throw new RuntimeException("This slot is already booked");
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatientId(dto.getPatientId() != null ? dto.getPatientId().intValue() : null);
        appointment.setDoctorId(availabilitySlot.getDoctorId());
        appointment.setAppointmentDateTime(LocalDateTime.of(availabilitySlot.getAvailableDate(), availabilitySlot.getStartTime()));
        appointment.setReason(dto.getReason());
        appointment.setAppointmentStatus("Scheduled");
        appointment.setAvailabilityId(availabilityIdInt);

        // Mark slot as booked
        availabilitySlot.setIsBooked(true);
        availabilitySlot.setStatus(AvailabilityStatus.BOOKED);
        doctorAvailabilityRepository.save(availabilitySlot);

        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments.
     */
    public List<AppointmentEntity> getAllAppointments() {
        return appointmentRepository.findAllWithPatient();
    }

    /**
     * Retrieves appointments by patient ID.
     */
    public List<AppointmentEntity> getAppointmentsByPatient(int patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    /**
     * Retrieves appointments by doctor ID.
     */
    public List<AppointmentEntity> getAppointmentsByDoctor(int doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    /**
     * Updates an existing appointment by changing its slot and/or reason.
     * Frees the old slot and books the new one if applicable.
     */
    public AppointmentEntity updateAppointment(int id, AppointmentRequestDTO dto) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (dto.getAvailabilityId() != null) {
            Integer availabilityIdInt = dto.getAvailabilityId().intValue();

            // Free the old slot
            DoctorAvailability oldAvailabilitySlot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
                .orElseThrow(() -> new RuntimeException("Old availability slot not found"));

            oldAvailabilitySlot.setIsBooked(false);
            oldAvailabilitySlot.setStatus(AvailabilityStatus.AVAILABLE);
            doctorAvailabilityRepository.save(oldAvailabilitySlot);

            // Reserve the new slot
            DoctorAvailability newAvailabilitySlot = doctorAvailabilityRepository.findById(availabilityIdInt)
                .orElseThrow(() -> new RuntimeException("New availability slot not found"));

            if (newAvailabilitySlot.getIsBooked()) {
                throw new RuntimeException("This slot is already booked");
            }

            appointment.setDoctorId(newAvailabilitySlot.getDoctorId());
            appointment.setAppointmentDateTime(LocalDateTime.of(newAvailabilitySlot.getAvailableDate(), newAvailabilitySlot.getStartTime()));
            appointment.setAvailabilityId(availabilityIdInt);

            newAvailabilitySlot.setIsBooked(true);
            newAvailabilitySlot.setStatus(AvailabilityStatus.BOOKED);
            doctorAvailabilityRepository.save(newAvailabilitySlot);
        }

        if (dto.getReason() != null) {
            appointment.setReason(dto.getReason());
        }

        return appointmentRepository.save(appointment);
    }

    /**
     * Deletes an appointment and frees the associated slot.
     */
    public void deleteAppointment(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        DoctorAvailability slot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
            .orElseThrow(() -> new RuntimeException("Slot not found"));

        slot.setIsBooked(false);
        slot.setStatus(AvailabilityStatus.AVAILABLE);
        doctorAvailabilityRepository.save(slot);

        appointmentRepository.deleteById(id);
    }

    /**
     * Creates an appointment directly from an entity and marks its slot as booked.
     */
    public AppointmentEntity createAppointmentFromEntity(AppointmentEntity entity) {
        DoctorAvailability slot = doctorAvailabilityRepository.findById(entity.getAvailabilityId())
            .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        if (slot.getIsBooked()) {
            throw new RuntimeException("This slot is already booked");
        }

        slot.setIsBooked(true);
        slot.setStatus(AvailabilityStatus.BOOKED);
        doctorAvailabilityRepository.save(slot);

        return appointmentRepository.save(entity);
    }

    /**
     * Retrieves an appointment by its ID.
     */
    public AppointmentEntity getAppointmentById(int id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<AppointmentWithPatientDTO> getAppointmentsWithPatientNameByDoctorId(int doctorId) {
        return appointmentRepository.findAppointmentsWithPatientNameByDoctorId(doctorId);
    }

    public void cancelAppointment(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));


        appointment.setAppointmentStatus("Cancelled");
        appointmentRepository.save(appointment);


        DoctorAvailability slot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
            .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        slot.setIsBooked(false);
        slot.setStatus(AvailabilityStatus.AVAILABLE);
        doctorAvailabilityRepository.save(slot);
    }
}