package com.carelink.appointment.service;

import com.carelink.appointment.dto.AvailabilityRequestDTO;
import com.carelink.appointment.dto.SlotDTO;
import com.carelink.appointment.model.DoctorAvailability;
import com.carelink.appointment.model.AvailabilityStatus;
import com.carelink.appointment.repository.DoctorAvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
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

            boolean conflictExists = availabilityRepository.existsByDoctorIdAndAvailableDateAndStartTimeLessThanAndEndTimeGreaterThan(
                dto.getDoctorId(),
                slot.getAvailableDate(),
                slot.getEndTime(),
                slot.getStartTime()
            );

            if (conflictExists) {
                throw new IllegalArgumentException("Conflict with existing availability slot on " + slot.getAvailableDate() +
                    " from " + slot.getStartTime() + " to " + slot.getEndTime());
            }

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

    public List<DoctorAvailability> getAvailabilityByDoctor(Integer doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    public void deleteAvailability(Integer availabilityId) {
        availabilityRepository.deleteById(availabilityId);
    }
    public List<DoctorAvailability> getAllAvailabilities() {
        return availabilityRepository.findAll();
    }

    public DoctorAvailability getAvailabilityById(Integer id) {
        return availabilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Availability not found with id " + id));
    }

    @Scheduled(fixedRate = 300000)
    public void expireOldSlots() {
        List<DoctorAvailability> expiredSlots = availabilityRepository.findExpiredSlots();

        for (DoctorAvailability slot : expiredSlots) {
            slot.setStatus(AvailabilityStatus.EXPIRED);
            availabilityRepository.save(slot);
        }

        if (!expiredSlots.isEmpty()) {
            System.out.println("Expired " + expiredSlots.size() + " old availability slots.");
        }
    }
}