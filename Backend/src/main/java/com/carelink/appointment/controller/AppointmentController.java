package com.carelink.appointment.controller;

import com.carelink.appointment.dto.AppointmentRequestDTO;
import com.carelink.appointment.dto.AppointmentWithPatientDTO;
import com.carelink.appointment.dto.AppointmentDTO;
import com.carelink.appointment.model.AppointmentEntity;
import com.carelink.appointment.service.AppointmentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;

    public AppointmentController(AppointmentServiceImpl appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @PostMapping
    public ResponseEntity<AppointmentEntity> createAppointment(@RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.createAppointment(dto));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('DOCTOR')")
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> dtos = appointmentService.getAllAppointments()
            .stream()
            .map(AppointmentDTO::new)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('DOCTOR')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(@PathVariable int patientId) {
        List<AppointmentEntity> appointments = appointmentService.getAppointmentsByPatient(patientId);
        List<AppointmentDTO> dtos = appointments.stream()
            .map(a -> {
                String doctorName = a.getDoctor() != null
                    ? a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName()
                    : "Unknown";

                Integer doctorId = a.getDoctor() != null
                    ? a.getDoctor().getUserID()
                    : null;

                AppointmentDTO dto = new AppointmentDTO(a, doctorId, doctorName);

                if (a.getAvailability() != null) {
                    dto.setAvailabilityId(a.getAvailability().getId());
                }

                return dto;
            })
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('RECEPTIONIST') ")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentWithPatientDTO>> getAppointmentsByDoctor(@PathVariable int doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsWithPatientNameByDoctorId(doctorId));
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable int id) {
        AppointmentEntity appointment = appointmentService.getAppointmentById(id);
        Integer doctorId = appointment.getDoctor() != null ? appointment.getDoctor().getUserID() : null;
        String doctorName = appointment.getDoctor() != null
            ? appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName()
            : "Unknown";

        AppointmentDTO dto = new AppointmentDTO(appointment, doctorId, doctorName);

        if (appointment.getAvailability() != null) {
            dto.setAvailabilityId(appointment.getAvailability().getId());
        }

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentEntity> updateAppointment(@PathVariable int id, @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable int id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable int id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @PutMapping("/{id}/check-in")
    public ResponseEntity<Void> checkInAppointment(@PathVariable int id) {
        appointmentService.checkInAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @PutMapping("/{id}/no-show")
    public ResponseEntity<Void> markNoShowAppointment(@PathVariable int id) {
        appointmentService.markNoShow(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @PutMapping("/{id}/undo-check-in")
    public ResponseEntity<Void> undoCheckInAppointment(@PathVariable int id) {
        appointmentService.undoCheckIn(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    @PutMapping("/{id}/undo-no-show")
    public ResponseEntity<Void> undoNoShowAppointment(@PathVariable int id) {
        appointmentService.undoNoShow(id);
        return ResponseEntity.ok().build();
    }
}