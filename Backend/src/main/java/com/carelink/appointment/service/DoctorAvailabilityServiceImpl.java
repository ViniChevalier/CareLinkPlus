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
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;

    public DoctorAvailabilityServiceImpl(DoctorAvailabilityRepository availabilityRepository) {
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
    public void cancelAvailabilitySlot(Integer availabilityId, Integer doctorId) {
        int updated = availabilityRepository.cancelSlot(availabilityId, doctorId);
        if (updated == 0) {
            throw new RuntimeException("Slot not found or does not belong to doctor.");
        }
    }

    @Override
    public List<SlotDTO> getSlotDTOsByDoctor(Integer doctorId) {
        List<DoctorAvailability> slots = availabilityRepository.getSlotDTOsByDoctor(doctorId);
        return convertToSlotDTOList(slots);
    }

    @Override
    public List<SlotDTO> getAllSlotDTOs() {
        List<DoctorAvailability> slots = availabilityRepository.getAllSlotDTOs();
        return convertToSlotDTOList(slots);
    }

    public SlotDTO getSlotById(Integer id) {
        DoctorAvailability slot = availabilityRepository.getSlotDTOById(id);
        return convertToSlotDTO(slot);
    }
    
    @Override
    public SlotDTO getSlotDTOById(Integer id) {
        DoctorAvailability slot = availabilityRepository.getSlotDTOById(id);
        return convertToSlotDTO(slot);
    }

    private SlotDTO convertToSlotDTO(DoctorAvailability availability) {
        SlotDTO dto = new SlotDTO();
        dto.setAvailabilityId(availability.getId());
        dto.setDoctorId(availability.getDoctor().getUserID());
        dto.setDoctorName(availability.getDoctor().getFirstName() + " " + availability.getDoctor().getLastName());
        dto.setAvailableDate(availability.getAvailableDate());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setStatus(availability.getStatus().toString());
        return dto;
    }

    private List<SlotDTO> convertToSlotDTOList(List<DoctorAvailability> availabilityList) {
        List<SlotDTO> dtoList = new ArrayList<>();
        for (DoctorAvailability availability : availabilityList) {
            dtoList.add(convertToSlotDTO(availability));
        }
        return dtoList;
    }

    @Scheduled(fixedRate = 300000)
    public void releaseCancelledUnbookedSlots() {
        List<DoctorAvailability> cancelledUnbookedSlots = availabilityRepository.findByIsBookedFalseAndStatus(AvailabilityStatus.CANCELLED);

        for (DoctorAvailability slot : cancelledUnbookedSlots) {
            slot.setStatus(AvailabilityStatus.AVAILABLE);
            availabilityRepository.save(slot);
        }

        if (!cancelledUnbookedSlots.isEmpty()) {
            System.out.println("Updated " + cancelledUnbookedSlots.size() + " cancelled unbooked slots to AVAILABLE.");
        }
    }
}