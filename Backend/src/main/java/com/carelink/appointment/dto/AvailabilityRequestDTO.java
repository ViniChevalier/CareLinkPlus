package com.carelink.appointment.dto;

import com.carelink.account.model.User;

import java.util.List;

public class AvailabilityRequestDTO {
    private Integer doctorId;
    private String doctorName;
    private List<SlotDTO> availableSlots;

    // Getters and setters

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<SlotDTO> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<SlotDTO> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public static AvailabilityRequestDTO fromDoctor(User doctor, List<SlotDTO> slots) {
        AvailabilityRequestDTO dto = new AvailabilityRequestDTO();
        dto.setDoctorId(doctor.getUserID());
        dto.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        dto.setAvailableSlots(slots);
        return dto;
    }
}