package com.carelink.appointment.service;

import com.carelink.appointment.dto.AvailabilityRequestDTO;
import com.carelink.appointment.dto.SlotDTO;
import com.carelink.appointment.model.DoctorAvailability;

import java.util.List;

public interface DoctorAvailabilityService {
    List<DoctorAvailability> addAvailability(AvailabilityRequestDTO dto);
    List<DoctorAvailability> getAvailabilityByDoctor(Integer doctorId);
    void deleteAvailability(Integer availabilityId);
    List<DoctorAvailability> getAllAvailabilities();
    DoctorAvailability getAvailabilityById(Integer id);
    void cancelAvailabilitySlot(Integer availabilityId, Integer doctorId);

    // Missing methods used in the controller
    List<SlotDTO> getSlotDTOsByDoctor(Integer doctorId);
    List<SlotDTO> getAllSlotDTOs();
    SlotDTO getSlotDTOById(Integer id);
}