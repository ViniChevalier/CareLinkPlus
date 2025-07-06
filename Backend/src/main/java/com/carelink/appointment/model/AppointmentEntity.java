package com.carelink.appointment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AppointmentID")
    private Integer appointmentId;

    @Column(name = "PatientID", nullable = false)
    private Integer patientId;

    @Column(name = "DoctorID", nullable = false)
    private Integer doctorId;

    @Column(name = "AppointmentDateTime", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "AppointmentStatus", length = 20)
    private String appointmentStatus = "Scheduled";

    @Column(name = "Reason", length = 255)
    private String reason;

    @Column(name = "CreatedAt", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "AvailabilityID")
    private Integer availabilityId;

    public AppointmentEntity() {
    }

    public AppointmentEntity(Integer appointmentId, Integer patientId, Integer doctorId,
            LocalDateTime appointmentDateTime, String appointmentStatus, String reason, Integer availabilityId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.appointmentStatus = appointmentStatus;
        this.reason = reason;
        this.availabilityId = availabilityId;
    }

    // Getters e setters
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

    public Integer getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Integer availabilityId) {
        this.availabilityId = availabilityId;
    }
}