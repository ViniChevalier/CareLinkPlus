package com.carelink.appointment.dto;

import java.util.List;

public class AvailabilityRequestDTO {
    private Integer doctorId;
    private List<SlotDTO> availableSlots;

    // Getters and setters

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public List<SlotDTO> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<SlotDTO> availableSlots) {
        this.availableSlots = availableSlots;
    }
}