package com.carelink.appointment.dto;

import java.time.LocalDateTime;

public class AppointmentWithPatientDTO {

    private Integer appointmentId;
    private Integer patientId;
    private Integer doctorId;
    private LocalDateTime appointmentDateTime;
    private String appointmentStatus;
    private String reason;
    private LocalDateTime createdAt;
    private Integer availabilityId;
    private String patientFirstName;
    private String patientLastName;

    public AppointmentWithPatientDTO(Integer appointmentId, Integer patientId, Integer doctorId,
                                     LocalDateTime appointmentDateTime, String appointmentStatus, String reason,
                                     LocalDateTime createdAt, Integer availabilityId,
                                     String patientFirstName, String patientLastName) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.appointmentStatus = appointmentStatus;
        this.reason = reason;
        this.createdAt = createdAt;
        this.availabilityId = availabilityId;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
    }

    // Getters and setters

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Integer availabilityId) {
        this.availabilityId = availabilityId;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }
}