package com.carelink.appointment.service;

import com.carelink.notification.emailService.EmailService;

import com.carelink.appointment.model.AvailabilityStatus;
import com.carelink.appointment.dto.AppointmentRequestDTO;
import com.carelink.appointment.dto.AppointmentWithPatientDTO;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.appointment.repository.DoctorAvailabilityRepository;
import com.carelink.exception.ResourceNotFoundException;
import com.carelink.exception.BusinessLogicException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final EmailService emailService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, DoctorAvailabilityRepository doctorAvailabilityRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.emailService = emailService;
    }


    public AppointmentEntity createAppointment(AppointmentRequestDTO dto) {
        Integer availabilityIdInt = dto.getAvailabilityId() != null ? dto.getAvailabilityId().intValue() : null;

        DoctorAvailability availabilitySlot = doctorAvailabilityRepository.findById(availabilityIdInt)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        if (availabilitySlot.getIsBooked()) {
            throw new BusinessLogicException("This slot is already booked");
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatientId(dto.getPatientId() != null ? dto.getPatientId().intValue() : null);
        appointment.setDoctorId(availabilitySlot.getDoctorId());
        appointment.setAppointmentDateTime(LocalDateTime.of(availabilitySlot.getAvailableDate(), availabilitySlot.getStartTime()));
        appointment.setReason(dto.getReason());
        appointment.setAppointmentStatus("Scheduled");
        appointment.setAvailabilityId(availabilityIdInt);

        availabilitySlot.setIsBooked(true);
        availabilitySlot.setStatus(AvailabilityStatus.BOOKED);
        doctorAvailabilityRepository.save(availabilitySlot);

        AppointmentEntity savedAppointment = appointmentRepository.save(appointment);

        if (savedAppointment.getPatient() != null) {
            emailService.sendAppointmentNotificationEmail(
                savedAppointment.getPatient().getEmail(),
                savedAppointment.getPatient().getFirstName(),
                availabilitySlot.getDoctorName(),
                availabilitySlot.getAvailableDate().toString(),
                availabilitySlot.getStartTime().toString()
            );
        }

        return savedAppointment;
    }

 
    public List<AppointmentEntity> getAllAppointments() {
        return appointmentRepository.findAllWithPatient();
    }

    public List<AppointmentEntity> getAppointmentsByPatient(int patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }


    public List<AppointmentEntity> getAppointmentsByDoctor(int doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public AppointmentEntity updateAppointment(int id, AppointmentRequestDTO dto) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (dto.getAvailabilityId() != null) {
            Integer availabilityIdInt = dto.getAvailabilityId().intValue();


            DoctorAvailability oldAvailabilitySlot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Old availability slot not found"));

            oldAvailabilitySlot.setIsBooked(false);
            oldAvailabilitySlot.setStatus(AvailabilityStatus.AVAILABLE);
            doctorAvailabilityRepository.save(oldAvailabilitySlot);

            DoctorAvailability newAvailabilitySlot = doctorAvailabilityRepository.findById(availabilityIdInt)
                .orElseThrow(() -> new ResourceNotFoundException("New availability slot not found"));

            if (newAvailabilitySlot.getIsBooked()) {
                throw new BusinessLogicException("This slot is already booked");
            }

            appointment.setDoctorId(newAvailabilitySlot.getDoctorId());
            appointment.setAppointmentDateTime(LocalDateTime.of(newAvailabilitySlot.getAvailableDate(), newAvailabilitySlot.getStartTime()));
            appointment.setAvailabilityId(availabilityIdInt);

            newAvailabilitySlot.setIsBooked(true);
            newAvailabilitySlot.setStatus(AvailabilityStatus.BOOKED);
            doctorAvailabilityRepository.save(newAvailabilitySlot);

            if (appointment.getPatient() != null) {
                emailService.sendAppointmentNotificationEmail(
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getFirstName(),
                    newAvailabilitySlot.getDoctorName(),
                    newAvailabilitySlot.getAvailableDate().toString(),
                    newAvailabilitySlot.getStartTime().toString()
                );
            }
        }

        if (dto.getReason() != null) {
            appointment.setReason(dto.getReason());
        }

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        DoctorAvailability slot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        slot.setIsBooked(false);
        slot.setStatus(AvailabilityStatus.AVAILABLE);
        doctorAvailabilityRepository.save(slot);

        appointmentRepository.deleteById(id);
    }

    public AppointmentEntity createAppointmentFromEntity(AppointmentEntity entity) {
        DoctorAvailability slot = doctorAvailabilityRepository.findById(entity.getAvailabilityId())
            .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        if (slot.getIsBooked()) {
            throw new BusinessLogicException("This slot is already booked");
        }

        slot.setIsBooked(true);
        slot.setStatus(AvailabilityStatus.BOOKED);
        doctorAvailabilityRepository.save(slot);

        return appointmentRepository.save(entity);
    }

    public AppointmentEntity getAppointmentById(int id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    public List<AppointmentWithPatientDTO> getAppointmentsWithPatientNameByDoctorId(int doctorId) {
        return appointmentRepository.findAppointmentsWithPatientNameByDoctorId(doctorId);
    }

    public void cancelAppointment(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setAppointmentStatus("Cancelled");
        appointmentRepository.save(appointment);

        DoctorAvailability slot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
            .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        slot.setIsBooked(false);
        slot.setStatus(AvailabilityStatus.AVAILABLE);
        doctorAvailabilityRepository.save(slot);

        // Send cancellation email to patient if exists
        if (appointment.getPatient() != null) {
            emailService.sendAppointmentCancellationEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getFirstName(),
                slot.getDoctorName(),
                slot.getAvailableDate().toString(),
                slot.getStartTime().toString()
            );
        }
    }

    public void checkInAppointment(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setAppointmentStatus("Attended");
        appointmentRepository.save(appointment);
    }

    public void markNoShow(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setAppointmentStatus("No_Show");
        appointmentRepository.save(appointment);
    }

    public void undoCheckIn(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setAppointmentStatus("Scheduled");
        appointmentRepository.save(appointment);
    }

    public void undoNoShow(int id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setAppointmentStatus("Scheduled");
        appointmentRepository.save(appointment);
    }
}