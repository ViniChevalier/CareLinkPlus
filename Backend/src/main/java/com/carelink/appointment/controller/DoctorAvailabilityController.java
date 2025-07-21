package com.carelink.appointment.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.carelink.appointment.dto.AvailabilityRequestDTO;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.service.DoctorAvailabilityService;
import com.carelink.security.CustomUserDetails;

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

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT') or hasRole('RECEPTIONIST')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('PATIENT') or hasRole('DOCTOR')")
    @GetMapping("/all")
    public ResponseEntity<List<DoctorAvailability>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('PATIENT') or hasRole('DOCTOR')")
    @GetMapping("/{availabilityId}")
    public ResponseEntity<DoctorAvailability> getAvailabilityById(@PathVariable Integer availabilityId) {
        DoctorAvailability availability = availabilityService.getAvailabilityById(availabilityId);
        return ResponseEntity.ok(availability);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @PutMapping("/{availabilityId}/cancel")
    public ResponseEntity<Void> cancelAvailability(@PathVariable Integer availabilityId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        availabilityService.cancelAvailabilitySlot(availabilityId, userId);
        return ResponseEntity.noContent().build();
    }
}