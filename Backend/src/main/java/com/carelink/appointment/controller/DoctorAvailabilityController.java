package com.carelink.appointment.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.carelink.appointment.dto.AvailabilityRequestDTO;
import com.carelink.appointment.dto.SlotDTO;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.service.DoctorAvailabilityService;
import com.carelink.security.CustomUserDetails;
import com.carelink.exception.ResourceNotFoundException;
import com.carelink.exception.BusinessLogicException;

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
    public ResponseEntity<List<SlotDTO>> getDoctorAvailability(@PathVariable Integer doctorId) {
        List<SlotDTO> slots = availabilityService.getSlotDTOsByDoctor(doctorId);
        if (slots == null || slots.isEmpty()) {
            throw new ResourceNotFoundException("No availability found for doctor with ID: " + doctorId);
        }
        return ResponseEntity.ok(slots);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Integer availabilityId) {
        try {
            availabilityService.deleteAvailability(availabilityId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Availability slot not found with ID: " + availabilityId);
        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('PATIENT') or hasRole('DOCTOR')")
    @GetMapping("/all")
    public ResponseEntity<List<SlotDTO>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllSlotDTOs());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST') or hasRole('PATIENT') or hasRole('DOCTOR')")
    @GetMapping("/{availabilityId}")
    public ResponseEntity<SlotDTO> getAvailabilityById(@PathVariable Integer availabilityId) {
        SlotDTO slot = availabilityService.getSlotDTOById(availabilityId);
        if (slot == null) {
            throw new ResourceNotFoundException("Availability slot not found with ID: " + availabilityId);
        }
        return ResponseEntity.ok(slot);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @PutMapping("/{availabilityId}/cancel")
    public ResponseEntity<Void> cancelAvailability(@PathVariable Integer availabilityId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            availabilityService.cancelAvailabilitySlot(availabilityId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new BusinessLogicException("Failed to cancel availability slot with ID: " + availabilityId);
        }
    }
}