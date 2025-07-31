package com.carelink.appointment.service;

import java.util.List;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.repository.AppointmentRepository;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.notification.emailService.EmailService;
import org.springframework.stereotype.Service;
import com.carelink.appointment.repository.DoctorAvailabilityRepository;

@Service
public class SendAppointmentRemindersService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final EmailService emailService;

    public SendAppointmentRemindersService(AppointmentRepository appointmentRepository,
                                           DoctorAvailabilityRepository doctorAvailabilityRepository,
                                           EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.emailService = emailService;
    }

    public void sendAppointmentReminders() {
        List<AppointmentEntity> upcomingAppointments = appointmentRepository.findUpcomingAppointmentsForReminder(java.time.LocalDate.now().plusDays(1));
        for (AppointmentEntity appointment : upcomingAppointments) {
            if (appointment.getPatient() != null) {
                DoctorAvailability availabilitySlot = doctorAvailabilityRepository.findById(appointment.getAvailabilityId())
                    .orElse(null);
                if (availabilitySlot != null) {
                    emailService.sendAppointmentReminderEmail(
                        appointment.getPatient().getEmail(),
                        appointment.getPatient().getFirstName(),
                        availabilitySlot.getDoctorName(),
                        availabilitySlot.getAvailableDate().toString(),
                        availabilitySlot.getStartTime().toString(),
                        appointment.getPatient().getUserID()
                    );
                }
            }
        }
    }
}
