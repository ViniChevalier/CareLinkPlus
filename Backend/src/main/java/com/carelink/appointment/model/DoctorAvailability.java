package com.carelink.appointment.model;

import java.util.ArrayList;
import java.util.List;

public class DoctorAvailability {
    private String doctorId;
    private List<String> availableSlots = new ArrayList<>();

    public DoctorAvailability() {}

    public DoctorAvailability(String doctorId, List<String> availableSlots) {
        this.doctorId = doctorId;
        this.availableSlots = availableSlots;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public List<String> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<String> availableSlots) {
        this.availableSlots = availableSlots;
    }
}
