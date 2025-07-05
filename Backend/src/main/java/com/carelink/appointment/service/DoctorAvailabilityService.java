package com.carelink.appointment.service;

import com.carelink.appointment.dto.AvailabilityRequestDTO;
import com.carelink.appointment.dto.SlotDTO;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.repository.DoctorAvailabilityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public List<DoctorAvailability> addAvailability(AvailabilityRequestDTO dto) {
        List<DoctorAvailability> savedSlots = new ArrayList<>();
        for (SlotDTO slot : dto.getAvailableSlots()) {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctorId(dto.getDoctorId());
            availability.setAvailableDate(slot.getAvailableDate());
            availability.setStartTime(slot.getStartTime());
            availability.setEndTime(slot.getEndTime());
            availability.setIsBooked(false);
            savedSlots.add(availabilityRepository.save(availability));
        }
        return savedSlots;
    }

    public List<DoctorAvailability> getAvailabilityByDoctor(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    public void deleteAvailability(Long availabilityId) {
        availabilityRepository.deleteById(availabilityId);
    }
}