package com.carelink.appointment.controller;

import com.carelink.appointment.dto.AvailabilityRequestDTO;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.service.DoctorAvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;

    public DoctorAvailabilityController(DoctorAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<List<DoctorAvailability>> addAvailability(@RequestBody AvailabilityRequestDTO dto) {
        return ResponseEntity.ok(availabilityService.addAvailability(dto));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorAvailability>> getDoctorAvailability(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByDoctor(doctorId));
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Integer availabilityId) {
        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.noContent().build();
    }
}