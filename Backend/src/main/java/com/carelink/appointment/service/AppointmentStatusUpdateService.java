package com.carelink.appointment.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.carelink.appointment.repository.AppointmentRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.model.AvailabilityStatus;

@Service
public class AppointmentStatusUpdateService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public void updateYesterdayAttendedToCompleted() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<AppointmentEntity> appointments = appointmentRepository.findAttendedAppointmentsFromYesterday(yesterday);

        for (AppointmentEntity appointment : appointments) {
            appointment.setAppointmentStatus("Completed");
        }

        appointmentRepository.saveAll(appointments);
    }
}
