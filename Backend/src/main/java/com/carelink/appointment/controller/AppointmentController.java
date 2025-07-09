package com.carelink.appointment.controller;

import com.carelink.appointment.dto.AppointmentRequestDTO;
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @GetMapping
    public ResponseEntity<List<AppointmentEntity>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentEntity>> getAppointmentsByPatient(@PathVariable int patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentEntity>> getAppointmentsByDoctor(@PathVariable int doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentEntity> getAppointmentById(@PathVariable int id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentEntity> updateAppointment(@PathVariable int id, @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable int id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}