package com.carelink.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.carelink.appointment.model.DoctorAvailability;

public class SlotDTO {

    private LocalDate availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String doctorName;
    private Integer availabilityId;
    private Integer doctorId;
    private String status;

    // Getters e setters

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Integer getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Integer availabilityId) {
        this.availabilityId = availabilityId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
 
    public static SlotDTO fromDoctorAvailability(DoctorAvailability availability) {
        SlotDTO dto = new SlotDTO();
        dto.setAvailableDate(availability.getAvailableDate());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        if (availability.getDoctor() != null) {
            String fullName = availability.getDoctor().getFirstName() + " " + availability.getDoctor().getLastName();
            dto.setDoctorName(fullName);
            dto.setDoctorId(availability.getDoctor().getUserID());
        }
        dto.setAvailabilityId(availability.getId());
        if (availability.getStatus() != null) {
            dto.setStatus(availability.getStatus().toString());
        } else {
            dto.setStatus("UNKNOWN");
        }
        return dto;
    }
}