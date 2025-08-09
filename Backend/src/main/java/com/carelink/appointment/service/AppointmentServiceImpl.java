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
import com.carelink.account.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentServiceImpl {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final EmailService emailService;
    private final com.carelink.account.repository.UserRepository userRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  DoctorAvailabilityRepository doctorAvailabilityRepository,
                                  EmailService emailService,
                                  com.carelink.account.repository.UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }


    public AppointmentEntity createAppointment(AppointmentRequestDTO dto) {
        Integer availabilityIdInt = dto.getAvailabilityId() != null ? dto.getAvailabilityId().intValue() : null;

        DoctorAvailability availabilitySlot = doctorAvailabilityRepository.findById(availabilityIdInt)
                .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        if (availabilitySlot.getIsBooked()) { // Prevent double-booking
            throw new BusinessLogicException("This slot is already booked");
        }

        // Build the appointment entity from request + slot info
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setPatientId(dto.getPatientId() != null ? dto.getPatientId().intValue() : null);
        appointment.setDoctorId(availabilitySlot.getDoctorId());
        appointment.setAppointmentDateTime(LocalDateTime.of(availabilitySlot.getAvailableDate(), availabilitySlot.getStartTime()));
        appointment.setReason(dto.getReason());
        appointment.setAppointmentStatus("Scheduled");
        appointment.setAvailabilityId(availabilityIdInt);

        // Update slot to booked state BEFORE saving appointment to reflect current capacity
        availabilitySlot.setIsBooked(true);
        availabilitySlot.setStatus(AvailabilityStatus.BOOKED);
        doctorAvailabilityRepository.save(availabilitySlot); // Persist slot state change

        AppointmentEntity savedAppointment = appointmentRepository.save(appointment); // Persist new appointment

        // Fetch patient for email personalization
        User patient = userRepository.findById(savedAppointment.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // (Could be moved to async/event in future) Notify patient of confirmation
        System.out.println("Sending email to: " + patient.getEmail());
        emailService.sendAppointmentNotificationEmail(
            patient.getEmail(),
            patient.getFirstName(),
            availabilitySlot.getDoctorName(),
            availabilitySlot.getAvailableDate().toString(),
            availabilitySlot.getStartTime().toString(),
            patient.getUserID()
        );
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

            // Retrieve and validate the new slot
            DoctorAvailability newAvailabilitySlot = doctorAvailabilityRepository.findById(availabilityIdInt)
                .orElseThrow(() -> new ResourceNotFoundException("New availability slot not found"));

            if (newAvailabilitySlot.getIsBooked()) {
                throw new BusinessLogicException("This slot is already booked");
            }

            // Rebind appointment to new slot and update scheduling data
            appointment.setDoctorId(newAvailabilitySlot.getDoctorId());
            appointment.setAppointmentDateTime(LocalDateTime.of(newAvailabilitySlot.getAvailableDate(), newAvailabilitySlot.getStartTime()));
            appointment.setAvailabilityId(availabilityIdInt);

            // Mark new slot as occupied
            newAvailabilitySlot.setIsBooked(true);
            newAvailabilitySlot.setStatus(AvailabilityStatus.BOOKED);
            doctorAvailabilityRepository.save(newAvailabilitySlot);

            // Notify patient if the relationship is loaded (avoid lazy load issues if null)
            if (appointment.getPatient() != null) {
                emailService.sendAppointmentNotificationEmail(
                    appointment.getPatient().getEmail(),
                    appointment.getPatient().getFirstName(),
                    newAvailabilitySlot.getDoctorName(),
                    newAvailabilitySlot.getAvailableDate().toString(),
                    newAvailabilitySlot.getStartTime().toString(),
                    appointment.getPatient().getUserID()
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

        slot.setIsBooked(false); // Free slot
        slot.setStatus(AvailabilityStatus.AVAILABLE);
        doctorAvailabilityRepository.save(slot);

        appointmentRepository.deleteById(id); // Remove appointment record
    }

    public AppointmentEntity createAppointmentFromEntity(AppointmentEntity entity) {
        DoctorAvailability slot = doctorAvailabilityRepository.findById(entity.getAvailabilityId())
            .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        if (slot.getIsBooked()) {
            throw new BusinessLogicException("This slot is already booked");
        }

        slot.setIsBooked(true); // Reserve slot
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

        appointment.setAppointmentStatus("Cancelled"); // Status transition only
        appointmentRepository.save(appointment);

        DoctorAvailability slot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
            .orElseThrow(() -> new ResourceNotFoundException("Availability slot not found"));

        slot.setIsBooked(false); // Free slot for reuse
        slot.setStatus(AvailabilityStatus.AVAILABLE);
        doctorAvailabilityRepository.save(slot);

        if (appointment.getPatient() != null) { // Notify patient (side effect)
            emailService.sendAppointmentCancellationEmail(
                appointment.getPatient().getEmail(),
                appointment.getPatient().getFirstName(),
                slot.getDoctorName(),
                slot.getAvailableDate().toString(),
                slot.getStartTime().toString(),
                appointment.getPatient().getUserID()
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